#ifndef PACKET_HPP
#define PACKET_HPP
#include "Arduino.h"

class Packet {
    public:
    Packet(byte *raw);
    
    byte CommandId;
    byte PayloadSize;
    byte *payload;
    short crc16;
    byte *Raw;
    bool Validate();
    ~Packet();
};
#endif