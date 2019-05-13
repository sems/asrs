#include "Packet.h"

uint8_t CRC8(const uint8_t *data, size_t size)
{
	uint8_t crc = 0x00;
	uint8_t extract;
	uint8_t sum;
	for (int i = 0; i < size; i++)
	{
		extract = *data;
		for (uint8_t tempI = 8; tempI; tempI--)
		{
			sum = (crc ^ extract) & 0x01;
			crc >>= 1;
			if (sum)
				crc ^= 0x8C;
			extract >>= 1;
		}
		data++;
	}
	return crc;
}

Packet::Packet(uint8_t cid, uint8_t *payload, uint8_t size) : commandId(cid),
															  payload(payload),
															  payloadSize(size)
{
	packetSize = payloadSize + 3;

	packetPtr = (uint8_t *)malloc(packetSize);

	packetPtr[0] = payloadSize;
	packetPtr[1] = commandId;

	memcpy(packetPtr + 2, this->payload, payloadSize);

	packetPtr[packetSize - 1] = CRC8(packetPtr, packetSize - 1);
}

Packet::~Packet()
{
	free(packetPtr);
}

uint8_t *Packet::getPacketPtr()
{
	return packetPtr;
}

size_t Packet::getPacketSize()
{
	return packetSize;
}