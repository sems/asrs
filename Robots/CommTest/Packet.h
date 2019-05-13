#ifndef H_PACKET
#define H_PACKET

#pragma once
#include "arduino.h"

class Packet
{
private:
	size_t packetSize;
	uint8_t payloadSize;
	uint8_t commandId;
	uint8_t *payload;

	uint8_t *packetPtr;

public:
	Packet(
		uint8_t commandId,
		uint8_t *payload,
		uint8_t size);
	~Packet();

	uint8_t *getPacketPtr();
	size_t getPacketSize();
};

#endif // !H_PACKET