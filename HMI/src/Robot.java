import com.fazecast.jSerialComm.*;

class Robot implements SerialPortDataListener {
    SerialPort comPort;

    Robot(SerialPort port) {
        comPort = port;
        comPort.openPort();
        comPort.addDataListener(this);
    }

    public static void main(String[] args) {
        SerialPort port = SerialPort.getCommPorts()[0];
        Robot r = new Robot(port);
    }

    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
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

        System.out.println("Size is " + size);
        while (comPort.bytesAvailable() < size) {
            try {
                System.out.println(comPort.bytesAvailable() + "/" + size);
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        comPort.readBytes(payload, size);

        System.out.println("x: " + payload[0] + ", y: " + payload[1] + ", z: " + payload[2]);
        // System.out.print(comPort.bytesAvailable() + " available");
        // byte[] newData = new byte[comPort.bytesAvailable()];
        // int numRead = comPort.readBytes(newData, newData.length);

        // for (int i = 0; i < newData.length; i++)
        // System.out.print(newData[i]);
    }
}