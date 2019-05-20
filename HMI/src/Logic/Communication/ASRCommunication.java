package Logic.Communication;

import Logic.CRC8.CRC8;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

/**
 * Wraps the communication to and from the ASR robot.
 */
public class ASRCommunication implements SerialPortDataListener {
    private SerialPort comPort;
    private ASREvent asrEvent;

    public ASRCommunication(SerialPort port) {
        comPort = port;
        comPort.addDataListener(this);
        comPort.setBaudRate(115200);
        comPort.openPort(3000);
        asrEvent = new ASREvent();

        start();
    }

    public void close(){
        stop();
        comPort.closePort();
    }

    public static void main(String[] args) {
        SerialPort port = SerialPort.getCommPorts()[0];
        port.setBaudRate(115200);
        ASRCommunication r = new ASRCommunication(port);

        r.pick();
        r.gotoPos((byte) 3, (byte) 2);
        r.stop();
        r.unload();
    }

    /**
     * Subscribe to the events from the ASR.
     * 
     * @param listener
     */
    public void subscribeToResponses(ASREventListener listener) {
        asrEvent.addASRListener(listener);
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
     * Move the robot to a certain x,y position.
     * 
     * @param x
     * @param y
     */
    public void gotoPos(int x, int y) {
        byte[] payload = { (byte) x, (byte) y };
        Packet packet = new Packet((byte) 11, payload);

        sendPacket(packet);
    }

    /**
     * Pick the order at a certain position
     */
    public void pick() {
        Packet p = new Packet((byte) 13, new byte[0]);
        sendPacket(p);
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

    /**
     * Get the current position of the robot
     */
    public void getPos() {
        Packet p = new Packet((byte) 10, new byte[0]);
        sendPacket(p);
    }

    public void home() {
        Packet p = new Packet((byte) 15, new byte[0]);
        sendPacket(p);
    }

    /**
     * Unload the picked products.
     */
    public void unload() {
        Packet p = new Packet((byte) 14, new byte[0]);
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

            asrEvent.onLog("Payload size: " + size + " Command Id: " + commandId);

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
                asrEvent.onLog("Packet is valid");

                if (commandId == 101) {
                    System.out.println("Response to GetStatus (101)");
                    asrEvent.onLog("Response to GetStatus (101)");
                    if (size == 1) {
                        System.out.println("Status " + payload[0]);

                        System.out.println("Response is correct");
                        asrEvent.onLog("Response to GetStatus (101)");
                    } else {
                        System.err.println("Size differs from expected");
                        asrEvent.onLog("Size differs from expected");
                    }
                }

                if (commandId == 102) {
                    System.out.println("Response to Stop (102)");
                    asrEvent.onLog("Response to Stop (102)");
                    if (size == 1) {
                        System.out.println("Response is correct");
                        asrEvent.onLog("Response is correct");
                    }
                }

                if (commandId == 103) {
                    System.out.println("Response to Start (103)");
                    asrEvent.onLog("Response to Start (103)");
                    if (size == 1) {
                        System.out.println("Response is correct");
                        asrEvent.onLog("Response is correct");
                    } else {
                        System.err.println("Size differs from expected");
                        asrEvent.onLog("Size differs from expected");
                    }
                }

                if (commandId == 104) {
                    System.out.println("Message response (104)");
                    asrEvent.onLog("Message response (104)");
                    if (size > 0) {
                        System.out.println("Response is correct");
                        asrEvent.onLog("Message response (104)");

                        StringBuilder output = new StringBuilder("ASR: ");

                        for (byte c : payload) {
                            output.append((char) c);
                        }
                        output.append("\n");

                        System.out.println(output);
                        asrEvent.onLog(output.toString());
                    } else {
                        System.err.println("Size differs from expected");
                        asrEvent.onLog("Size differs from expected");
                    }
                }

                // getPos response 110
                if (commandId == 110) {
                    System.out.println("Response to getPos (110)");
                    asrEvent.onLog("Response to getPos (110)");
                    if (size == 2) {
                        System.out.println("Asr is at position x: " + payload[0] + ", y: " + payload[1]);
                        asrEvent.onLog("Asr is at position x: " + payload[0] + ", y: " + payload[1]);
                        asrEvent.onGetPositionReceived(payload[0], payload[1]);
                    } else {
                        System.err.println("Size differs from expected");
                        asrEvent.onLog("Size differs from expected");
                    }
                }

                // gotoPos response 111
                if (commandId == 111) {
                    if (size == 1) {
                        ErrorCode errorCode = getErrorCode(payload[0]);
                        if (errorCode == ErrorCode.SUCCESS) {
                            System.out.println("GotoPos success");
                            asrEvent.onLog("GotoPos success");
//                            asrEvent.onPositionResponseReceived(ErrorCode.SUCCESS);
                        } else {
                            System.out.println("GotoPos went wrong");
                            asrEvent.onLog("GotoPos went wrong");
//                            asrEvent.onPositionResponseReceived(errorCode);
                        }
                    } else {
                        System.err.println("size differs from expected");
                        asrEvent.onLog("Size differs from expected");
                    }
                }

                if (commandId == 113) {
                    if (size == 1) {
                        ErrorCode ec = getErrorCode(payload[0]);
                        asrEvent.onLog("Pick response: " + ec);
                        if (ec == ErrorCode.SUCCESS) {
                            // TODO: Add application call
                        } else {
                            // TODO: Something went wrong
                        }
                    } else {
                        System.err.println("size differs from expected");
                        asrEvent.onLog("size differs from expected");
                    }
                }

                if (commandId == 114) {
                    System.out.println("Response to unload (114)");
                    asrEvent.onLog("Response to unload (114)");
                    if (size == 1) {
                        ErrorCode ec = getErrorCode(payload[0]);

                        System.out.println("Unload Response: " + ec);

                        if (ec == ErrorCode.SUCCESS) {
                            asrEvent.onUnloadResponseReceived();
                            asrEvent.onLog("Response is correct");
                        }
                    } else {
                        System.err.println("size differs from expected");
                        asrEvent.onLog("size differs from expected");
                    }
                }

                if (commandId == 115) {
                    asrEvent.onLog("Response to Homing (115) ");
                    if (size == 0){
                        ErrorCode ec = getErrorCode(payload[0]);

                        asrEvent.onLog("Homing response: " + ec);

                        if (ec == ErrorCode.SUCCESS) {
                            // ASR EVENT
                        }
                    }
                    else {
                        asrEvent.onLog("Unexpected size");
                    }
                }

            } else {
                System.err.println("Packet is invalid");
                asrEvent.onLog("Packet is invalid");
            }

            System.out.println("--= PACKET END =--");
            asrEvent.onLog("--= PACKET END =--");

            //System.out.println("Bytes still available: " + comPort.bytesAvailable());
            //asrEvent.onLog("Bytes still available: " + comPort.bytesAvailable());
        }
    }
}
