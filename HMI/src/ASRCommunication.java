import CRC8.CRC8;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Wraps the communication to and from the ASR robot.
 */
public class ASRCommunication implements SerialPortDataListener {
    SerialPort comPort;

    public ASRCommunication(SerialPort port) {
        comPort = port;
        comPort.openPort();
        comPort.addDataListener(this);
        // this.start();
    }

    public static void main(String[] args) {
        SerialPort port = SerialPort.getCommPorts()[0];
        port.setBaudRate(115200);
        ASRCommunication r = new ASRCommunication(port);



         r.start();
         r.gotoPos((byte) 3, (byte) 2);
    }

    public void sendPacket(Packet packet) {
        byte[] bytes = packet.getBytes();

        comPort.writeBytes(bytes, bytes.length);
    }

    public void gotoPos(int x, int y) {
        byte[] payload = { (byte) x, (byte) y };
        Packet packet = new Packet((byte) 11, payload);

        sendPacket(packet);
    }

    public void pick(){
        Packet p = new Packet((byte) 13, new byte[0]);

        sendPacket(p);
    }

    public void start(){
        Packet p = new Packet((byte) 3, new byte[0]);

        sendPacket(p);
    }

    public void stop(){
        Packet p = new Packet((byte) 2, new byte[0]);

        sendPacket(p);
    }

    public void getPos(){
        Packet p = new Packet((byte) 10, new byte[0]);

        sendPacket(p);
    }

    public void unload(){
        Packet p = new Packet((byte) 14, new byte[0]);

        sendPacket(p);
    }

    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
    }

    public void serialEvent(SerialPortEvent event) {
        if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
            return;

        try {
            Thread.sleep(3000);
        }
        catch (Exception e) {

        }

        byte[] sizeBuffer = new byte[1];
        comPort.readBytes(sizeBuffer, 1);
        byte size = sizeBuffer[0];

        byte commandId = 0;
        while (commandId == 0) {
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
        long packetChecksum = checkBuffer[0];

        byte packet[] = new byte[size + 2];

        packet[0] = size;
        packet[1] = commandId;

        System.arraycopy(payload, 0, packet, 2, payload.length);

        CRC8 receivedChecksum = new CRC8();
        receivedChecksum.update(packet, 0, packet.length);
        long calcChecksum = receivedChecksum.getValue();

        System.out.println("Received Check: " + packetChecksum + ", Calculated Checksum: " + calcChecksum);

        if (packetChecksum == calcChecksum) {
            System.out.println("Packet is valid");
        } else {
            System.out.println("Packet is invalid");
        }

        // getPos response 110
        if (commandId == 110) {
            System.out.println("Response to getPos (110)");
            if(size == 2)
                System.out.println("Asr is at position x: " + payload[0] + ", y: " + payload[1]);
            else
                System.out.println("Size is wrong");
        }

        //gotoPos reponse 111
        if (commandId == 111){
            if (size == 1){
                if(payload[0] == 0){
                    System.out.println("GotoPos success");

                    pick();
                }
                else {
                    System.out.println("GotoPos went wrong");
                }
            }
            else{
                System.out.println("size diverse from expected");
            }
        }
    }

    private ArrayList<byte[]> activeOrder;

    private byte[] currentItem;

    private boolean orderComplete = true;

    public void addOrder(byte[][] order) {
        if (orderComplete) {
            if (activeOrder.size() > 0) {
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

        // gotoPos(currentItem[0], currentItem[1]);
    }
}
