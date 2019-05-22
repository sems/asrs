package Logic.Communication;

import Logic.CRC8.CRC8;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

/**
 * Wraps the communication to and from the ASR robot.
 */
public class BinrCommunication implements SerialPortDataListener {
    private SerialPort comPort;
    private BINREvent binrEvent;

    public BinrCommunication(SerialPort port) {
        comPort = port;
        comPort.addDataListener(this);
        comPort.setBaudRate(115200);
        comPort.openPort(3000);
        binrEvent = new BINREvent();

        try{
            Thread.sleep(3000);
            start();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void close(){
        stop();
        comPort.closePort();
    }

    public static void main(String[] args) {
        SerialPort port = SerialPort.getCommPorts()[0];
        port.setBaudRate(115200);
        BinrCommunication r = new BinrCommunication(port);

        r.stop();
    }

    /**
     * Send the given packet to the robot.
     *
     * @param packet
     */
    public void sendPacket(Packet packet) {
        byte[] bytes = packet.getBytes();

        comPort.writeBytes(bytes, bytes.length);
    }

    /**
     *  Send the command to let the belt move left
     */

    public void moveLeft(){
        byte[] data = new byte[1];
        data[0] = 0;

        Packet p = new Packet((byte) 20, data);
        sendPacket(p);
    }

    /**
     *  Send the command to let the belt move right
     */

    public void moveRight(){
        byte[] data = new byte[1];
        data[0] = 1;

        Packet p = new Packet((byte) 20, data);
        sendPacket(p);
    }

    /**
     * Subscribe to the events from the ASR.
     *
     * @param listener
     */
    public void subscribeToResponses(BINREventListener listener) {
        binrEvent.addBINRListener(listener);
    }

    /**
     * Send the start command to the robot.
     */
    public void start() {
        Packet p = new Packet((byte) 3, new byte[0]);
        sendPacket(p);
    }

    /**
     * Send the stop command to the robot.
     */
    public void stop() {
        Packet p = new Packet((byte) 2, new byte[0]);
        sendPacket(p);
    }

    private ErrorCode getErrorCode(byte er) {
        if (er == 0) {
            return ErrorCode.SUCCESS;
        }
        if (er == 1) {
            return ErrorCode.UNKNOWN;
        }
        if (er == 2) {
            return ErrorCode.OUTOFBOUND;
        }
        if (er == 3) {
            return ErrorCode.NOSPACE;
        }
        if (er == 4) {
            return ErrorCode.STOPPED;
        }
        if (er == 5) {
            return ErrorCode.CHECKSUM;
        }
        if (er == 6) {
            return ErrorCode.SIZE;
        }

        return ErrorCode.UNKNOWN;
    }

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
    }

    public void serialEvent(SerialPortEvent event) {
        if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
            return;

        while (comPort.bytesAvailable() > 0) {
            System.out.println("--= NEW PACKET =--");
            binrEvent.onLog("--= NEW PACKET =--");

            byte[] sizeBuffer = new byte[1];
            comPort.readBytes(sizeBuffer, 1);
            byte size = sizeBuffer[0];

            byte commandId = 0;
            while (commandId == 0) {
                byte[] commandBuf = new byte[1];
                comPort.readBytes(commandBuf, 1);
                commandId = commandBuf[0];
            }

            System.out.println("Payload size: " + size);
            System.out.println("Command Id: " + commandId);

            binrEvent.onLog("Payload size: " + size + " Command Id: " + commandId);

            byte[] payload = new byte[size];

            int timeout = 100;

            while (comPort.bytesAvailable() < size + 1) {
                try {
                    Thread.sleep(timeout);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            comPort.readBytes(payload, size);

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

            if (packetChecksum == calcChecksum) {
                System.out.println("Packet is valid");
                binrEvent.onLog("Packet is valid");

                if (commandId == 101) {
                    System.out.println("Response to GetStatus (101)");
                    binrEvent.onLog("Response to GetStatus (101)");
                    if (size == 1) {
                        System.out.println("Status " + payload[0]);

                        System.out.println("Response is correct");
                        binrEvent.onLog("Response to GetStatus (101)");
                    } else {
                        System.err.println("Size differs from expected");
                        binrEvent.onLog("Size differs from expected");
                    }
                }

                if (commandId == 102) {
                    System.out.println("Response to Stop (102)");
                    binrEvent.onLog("Response to Stop (102)");
                    if (size == 1) {
                        System.out.println("Response is correct");
                        binrEvent.onLog("Response is correct");
                    }
                }

                if (commandId == 103) {
                    System.out.println("Response to Start (103)");
                    binrEvent.onLog("Response to Start (103)");
                    if (size == 1) {
                        System.out.println("Response is correct");
                        binrEvent.onLog("Response is correct");
                    } else {
                        System.err.println("Size differs from expected");
                        binrEvent.onLog("Size differs from expected");
                    }
                }

                if (commandId == 104) {
                    System.out.println("Message response (104)");
                    binrEvent.onLog("Message response (104)");
                    if (size > 0) {
                        System.out.println("Response is correct");
                        binrEvent.onLog("Message response (104)");

                        StringBuilder output = new StringBuilder("BINr: ");

                        for (byte c : payload) {
                            output.append((char) c);
                        }
                        output.append("\n");

                        System.out.println(output);
                        binrEvent.onLog(output.toString());
                    } else {
                        System.err.println("Size differs from expected");
                        binrEvent.onLog("Size differs from expected");
                    }
                }

                if (commandId == 120){
                    if (size == 1){
                        ErrorCode ec = getErrorCode(payload[0]);
                        binrEvent.onLog("Drop response: " + ec);

                        //TODO: Timon code
                    }
                    else {
                        System.err.println("Unexpected size");
                    }
                }

            } else {
                System.err.println("Packet is invalid");
                binrEvent.onLog("Packet is invalid");
            }

            System.out.println("--= PACKET END =--");
            binrEvent.onLog("--= PACKET END =--");

            //System.out.println("Bytes still available: " + comPort.bytesAvailable());
            //asrEvent.onLog("Bytes still available: " + comPort.bytesAvailable());
        }
    }
}
