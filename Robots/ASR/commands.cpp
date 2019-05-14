#include "commands.hpp"
#include "packet.hpp"
#include "core.hpp"
#include "communication.hpp"

void StatusCommand(Core& core, Communication& communication)
{
	Packet* statusPacket = Packet::createStatusPacket(core.status);
	communication.sendPacket(statusPacket);
	delete statusPacket;
}

