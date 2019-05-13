import CRC8.CRC8;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.io.OutputStream;
import java.util.ArrayList;

public class Asr implements SerialPortDataListener {
    SerialPort comPort;

    public Asr(SerialPort port) {
        comPort = port;
        comPort.openPort();
        comPort.addDataListener(this);
        //this.start();
    }

    public static void main(String[] args) {
        SerialPort port = SerialPort.getCommPorts()[0];
        Asr r = new Asr(port);

        //r.start();
        //r.gotoPos((byte) 3, (byte) 2);
    }

    public void sendPacket(Packet packet){
        byte[] bytes = packet.getBytes();

        comPort.writeBytes(bytes, bytes.length);
    }

    public void gotoPos(int x, int y){
        byte[] payload = {(byte)x, (byte)y};
        final byte commandId = 11;

        Packet packet = new Packet(commandId,payload);

        sendPacket(packet);
    }

    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
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

        byte commandId = 0;
        while (commandId == 0){
            byte[] commandBuf = new byte[1];
            comPort.readBytes(commandBuf, 1);
            commandId = commandBuf[0];
        }

        byte[] payload = new byte[size];

        int timeout = 100;

        while (comPort.bytesAvailable() < size + 1) {
            try {
                System.out.println("Not all received - " + comPort.bytesAvailable() + "/ " + (size + 1) + " bytes");
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("All " + (size + 1) + " bytes received");
        comPort.readBytes(payload, size);

        System.out.println("Payload content:");
        for (int i = 0; i < size; i++) {
            System.out.println(i + 1 + ": " + payload[i]);
        }

        byte checkBuffer[] = new byte[1];
        comPort.readBytes(checkBuffer, 1);
        byte packetChecksum = checkBuffer[0];

        byte packet[] = new byte[size + 2];

        packet[0] = size;
        packet[1] = commandId;

        System.arraycopy(payload, 0, packet, 2, payload.length);

        CRC8 receivedChecksum = new CRC8(0x07, (short) 0x00);
        receivedChecksum.update(packet);
        byte calcChecksum = (byte)receivedChecksum.getValue();

        System.out.println("Received Check: " + packetChecksum + ", Calculated Checksum: " + calcChecksum);

        if (packetChecksum == calcChecksum) {
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

        //gotoPos(currentItem[0], currentItem[1]);
    }
}
