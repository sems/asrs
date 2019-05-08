#include "Arduino.h"
#include "packet.hpp"

static const int PAYLOAD_SIZE_OFFSET = 1;
static const int COMMAND_ID_OFFSET = 2;


Packet::Packet(byte *raw){
    raw = raw;
    PayloadSize = *raw;
    CommandId = *(raw+PAYLOAD_SIZE_OFFSET);
    payload = raw+COMMAND_ID_OFFSET;
    crc16 = COMMAND_ID_OFFSET + PayloadSize;
}

bool Packet::Validate(){
    int length = COMMAND_ID_OFFSET + PayloadSize;
    byte *rawPtr = Raw;
    unsigned char x;
    unsigned short crc = 0xFFFF;

    while (length--){
        x = crc >> 8 ^ *rawPtr++;
        x ^= x>>4;
        crc = (crc << 8) ^ ((unsigned short)(x << 12)) ^ ((unsigned short)(x <<5)) ^ ((unsigned short)x);
    }
    return crc == crc16;
}

Packet::~Packet(){
    delete[](this->Raw);
}
