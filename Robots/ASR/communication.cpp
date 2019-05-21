#include "communication.hpp"
#include "utils.hpp"
#include "packet.hpp"

Packet* Communication::receivePacket()
{
	if (Serial.available() > 0)
	{
		byte payloadSizeBuffer[1];
		Serial.readBytes(payloadSizeBuffer, 1);
		byte* packetBuffer = new byte[payloadSizeBuffer[0] + 4];
		packetBuffer[0] = payloadSizeBuffer[0];
		Serial.readBytes(
			packetBuffer + 1, //Skip payloadSize byte
			payloadSizeBuffer[0] + 2
		);
		return new Packet(packetBuffer);
	}
	return nullptr;
}

void Communication::sendLogPacket(char* message)
{
	Packet* packet = new Packet(INFO_TX, strlen(message), reinterpret_cast<byte*>(message));
	this->sendPacket(packet);
	delete packet;
}

void Communication::sendStatusPacket(byte commandId, Status status)
{
	Packet* packet = new Packet(commandId, 1, reinterpret_cast<byte*>(&status));
	this->sendPacket(packet);
	delete packet;
}

void Communication::sendPosPacket(byte commandId, byte x, byte y)
{
	byte vector2[2] = {x, y};
	Packet* packet = new Packet(commandId, 2, vector2);
	this->sendPacket(packet);
	delete packet;
}

void Communication::sendErrorPacket(byte commandId, ErrorCode errorCode)
{
	Packet* packet = new Packet(commandId, 1, reinterpret_cast<byte*>(&errorCode));
	this->sendPacket(packet);
	delete packet;
}

void Communication::sendPacket(Packet* packet)
{
	Serial.write(packet->Raw, packet->PayloadSize + 3);
}

void Communication::initialize()
{
	Serial.begin(115200);
}
