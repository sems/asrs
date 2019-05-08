import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.stream.Stream;

public class Asr implements SerialPortDataListener {
    SerialPort comPort;

    public Asr(SerialPort port) {
        comPort = port;
        comPort.openPort();
        comPort.addDataListener(this);
        this.start();
    }

    public static void main(String[] args) {
        SerialPort port = SerialPort.getCommPorts()[0];
        Asr r = new Asr(port);

        r.start();
        r.gotoPos((byte) 3, (byte) 2);
    }

    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
    }

    public void start() {
        OutputStream out = comPort.getOutputStream();
        final byte size = 0;
        final byte commandCode = 3;

        byte buffer[] = { size, commandCode, 0, 0 };

        try {
            out.write(buffer);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void stop() {
        OutputStream out = comPort.getOutputStream();
        final byte size = 0;
        final byte commandCode = 2;

        byte buffer[] = { size, commandCode, 0, 0 };

        try {
            out.write(buffer);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void gotoPos(byte x, byte y) {
        OutputStream out = comPort.getOutputStream();
        final byte size = 2;
        final byte commandCode = 11;
        byte payload[] = { x, y };

        int check = makeChecksom(payload);

        byte checksum[] = { (byte) (check >> 8 & 0xFF), (byte) (check & 0xFF) };

        byte buffer[] = { size, commandCode, payload[0], payload[1], checksum[0], checksum[1] };

        try {
            out.write(buffer);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public static int makeChecksom(byte payload[]) {
        byte checksumArr[] = new byte[2];

        int i;
        int crc_value = 0;
        for (int len = 0; len < payload.length; len++) {
            for (i = 0x80; i != 0; i >>= 1) {
                if ((crc_value & 0x8000) != 0) {
                    crc_value = (crc_value << 1) ^ 0x8005;
                } else {
                    crc_value = crc_value << 1;
                }
                if ((payload[len] & i) != 0) {
                    crc_value ^= 0x8005;
                }
            }
        }

        System.out.println(crc_value);

        return crc_value;
    }

    public void pick() {

    }

    public void serialEvent(SerialPortEvent event) {
        if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
            return;

        byte size = 0;
        while (size == 0) {
            byte[] sizeBuffer = new byte[1];
            comPort.readBytes(sizeBuffer, 1);
            size = sizeBuffer[0];
        }

        byte[] payload = new byte[size];

        int timeout = 100;

        System.out.println("Size is " + size);
        while (comPort.bytesAvailable() < size + 2) {
            try {
                System.out.println("Not all received - " + comPort.bytesAvailable() + "/ " + (size + 2) + " bytes");
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        comPort.readBytes(payload, size);

        for (int i = 0; i < size; i++) {
            System.out.println(i + 1 + ": " + payload[i]);
        }

        byte checkBuffer[] = new byte[2];
        comPort.readBytes(checkBuffer, 2);

        int checkPayload = this.makeChecksom(payload);
        int checkPacket = (checkBuffer[0] << 8) + (checkBuffer[1]);

        if (checkPayload == checkPacket) {
            System.out.print("Packet is valid");
        } else {
            System.out.print("Packet is invalid");
        }

    }

    private ArrayList<byte[]> activeOrder;

    private byte[] currentItem;

    // private byte[][] completedItems;

    private boolean orderComplete = true;

    public void addOrder(byte[][] order) {
        if (orderComplete) {
            if (activeOrder.size() > 0) {

                // activeOrder = order;

                orderComplete = false;
                NextItem();
            } else {
                System.err.println("There is nothing in the order");
            }
        } else {
            System.err.println("Order still in progress");
        }
    }

    public void NextItem() {
        byte item[] = activeOrder.get(0);
        currentItem = item;
        activeOrder.remove(0);

        gotoPos(currentItem[0], currentItem[1]);
    }

}