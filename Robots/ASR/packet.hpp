#ifndef PACKET_HPP
#define PACKET_HPP

#include "Arduino.h"

// The communication with the HMI relies on this packet format
class Packet {
    public:
    // This constructor is used for incomming packets
    Packet(byte *raw);
    // This constructor is used for outgoing packets
    Packet(byte commandId, byte payloadLength, byte *payload);
    // Each command has a unique ID. IDs < 100 are used for Commands to the ASR, 
    // IDs > 100 stands for Commands from the ASR.
    byte CommandId;
    // The size of the payload can be 0
    byte PayloadSize;
    byte *payload;
    // The CRC16 is a checksum that is used to validate wether the package is corrupted
    short crc16;
    byte *Raw;
    
    // returns false if the crc16 from the packets is invalid
    bool validateCrc16();

    // destructor to cleanup allocated byte array
    ~Packet();

    // Returns an instance of a log packet
    static Packet* createLogPacket(char *message);
};
#endif