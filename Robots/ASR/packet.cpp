#include "Arduino.h"
#include "packet.hpp"
#include "utils.hpp"

static const int PAYLOAD_SIZE_OFFSET = 1;
static const int COMMAND_ID_OFFSET = 2;

// CommandId + payload size + CRC16
static const int FIELDS_SIZE = 4;

static const byte INFO_TX_COMMAND_ID = 104;

Packet::Packet(byte *raw)
{
    raw = raw;
    PayloadSize = *raw;
    CommandId = *(raw+PAYLOAD_SIZE_OFFSET);
    payload = raw+COMMAND_ID_OFFSET;
    crc16 = COMMAND_ID_OFFSET + PayloadSize;
}

Packet::Packet(byte commandId, byte payloadLength, byte *payload)
{
    CommandId = commandId;
    PayloadSize = payloadLength;
    
    byte *raw = new byte[PayloadSize + FIELDS_SIZE];
    *raw = payloadLength;
    *(raw+1) = commandId;

    for(int i = 0; i < payloadLength; i++)
    {
        *(raw + COMMAND_ID_OFFSET + i) = payload[i];
    }
}

Packet* Packet::createLogPacket(char *message){
    // because payload size is known, string null terminator is not needed
    // So it is removed to save 1 byte of precious memory
    int stringLength = strlen(message);
    unsigned char buffer[stringLength];
    memcpy(buffer, message, stringLength);
    // Buffer is copied in Packet constructor so don't worry about dangling pointer

    return new Packet(INFO_TX_COMMAND_ID, stringLength, buffer);
}

bool Packet::validateCrc16()
{
    int length = COMMAND_ID_OFFSET + PayloadSize;
    auto crc16 = computeCrc16(this->Raw, length);
    return this->crc16 == crc16;
}

Packet::~Packet()
{
    delete[](this->Raw);
}
