#ifndef PACKET_HPP
#define PACKET_HPP

#include "predef.hpp"
#include "Arduino.h"
#include "status.hpp"


#define STATUS_RX 1
#define STATUS_TX 101

#define STOP_RX 2
#define STOP_TX 102

#define START_RX 3
#define START_TX 103

#define INFO_TX 104

#define DROP_BINR_RX 20
#define DROP_BINR_TX 120

#define GET_POSITION_RX 10
#define GET_POSITION_TX 110

#define GOTO_POSITION_RX 11
#define GOTO_POSITION_TX 111

#define PICK_RX 13
#define PICK_TX 113

#define UNLOAD_RX 14
#define UNLOAD_TX 114



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
    byte crc8;
    byte *Raw;
    
    // returns false if the crc16 from the packets is invalid
    bool validateCrc8();

    // destructor to cleanup allocated byte array
    ~Packet();

    // Returns an instance of a log packet
    static Packet* createLogPacket(char *message);

	// Returns an instance of a status packet
	static Packet* createStatusPacket(byte commandId,Status status);

	static Packet* createPosPacket(byte commandId, byte x, byte y);
	
	static Packet* createErrorPacket(byte commandId, ErrorCode errorCode);
	
	// BIN'r
	//static Packet* createDirectionPacket(Direction direction);
};
#endif