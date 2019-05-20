#ifndef COMMUNICATION_HPP
#define COMMUNICATION_HPP

#include "predef.hpp"
#include "status.hpp"
#include "arduino.h"

class Packet;

class Communication
{
public:
    void initialize();
    Packet* receivePacket();
	void sendStatusPacket(byte commandId, Status status);
	void sendLogPacket(char* message);
	void sendPosPacket(byte commandId, byte x, byte y);
	void sendErrorPacket(byte commandId, ErrorCode errorCode);
    void sendPacket(Packet *packet);
};

#endif