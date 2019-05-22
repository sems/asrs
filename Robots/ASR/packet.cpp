#include "Arduino.h"
#include "packet.hpp"
#include "utils.hpp"
#include "status.hpp"

static const int PAYLOAD_SIZE_OFFSET = 1;
static const int COMMAND_ID_OFFSET = 2;

// CommandId + payload size + CRC8
static const int FIELDS_SIZE = 3;


Packet::Packet(byte* raw)
{
	raw = raw;
	PayloadSize = raw[0];
	CommandId = raw[1];
	payload = raw + COMMAND_ID_OFFSET;
	crc8 = raw[COMMAND_ID_OFFSET + PayloadSize];
}

Packet::Packet(byte commandId, byte payloadLength, byte* payload)
{
	this->CommandId = commandId;
	this->PayloadSize = payloadLength;


	byte* raw = new byte[PayloadSize + FIELDS_SIZE];
	raw[0] = payloadLength;
	raw[1] = commandId;
	for (int i = 0; i < payloadLength; i++)
	{
		raw[COMMAND_ID_OFFSET + i] = payload[i];
	}
	this->crc8 = computeCrc8(raw, PayloadSize + 2);
	raw[COMMAND_ID_OFFSET + PayloadSize] = this->crc8;
	this->payload = raw + 2;
	this->Raw = raw;
}


bool Packet::validateCrc8()
{
	int length = COMMAND_ID_OFFSET + PayloadSize;
	byte crc8 = computeCrc8(this->Raw, length);
	return this->crc8 == crc8;
}

Packet::~Packet()
{
	delete[](this->Raw);
}
