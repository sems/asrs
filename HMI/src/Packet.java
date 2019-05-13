import CRC8.CRC8;

public class Packet {
    private byte size;
    private byte commandId;
    private byte[] payload;
    private byte checkSum;

    public Packet(byte commandId, byte[] payload){
        size = (byte)payload.length;
        this.commandId = commandId;

        byte[] checkBuffer = new byte[size + 2];

        CRC8 checkSumObj = new CRC8();
        checkSumObj.update(checkBuffer, 0, checkBuffer.length);
        checkSum = (byte)checkSumObj.getValue();
    }

    public byte[] getBytes(){
        byte[] packet = new byte[size + 3];

        packet[0] = size;
        packet[1] = commandId;

        System.arraycopy(payload, 0, packet, 2, size);

        packet[packet.length - 1] = checkSum;

        return packet;
    }
}
