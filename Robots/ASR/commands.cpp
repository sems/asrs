#include "commands.hpp"
#include "packet.hpp"
#include "core.hpp"
#include "communication.hpp"
#include "constants.hpp"

//This define is used to disable code generation for logs

#ifdef LOGGING
#define LOG_INFO(MESSAGE) core.logger.logInfo(MESSAGE)
#else
#define LOG_INFO(MESSAGE)
#endif // LOGGING


void statusCommand(Core& core, Communication& communication, Packet& packet)
{
	LOG_INFO("running status command");
	communication.sendStatusPacket(STATUS_TX,core.status);
}

void stopCommand(Core& core, Communication& communication,Packet& packet)
{
	LOG_INFO("running stop command");
	core.started = false;
	communication.sendErrorPacket(STOP_TX, ErrorCode::Success);
}

void startCommand(Core& core, Communication& communication, Packet& packet)
{
	LOG_INFO("running start command");
	core.started = true;
	communication.sendErrorPacket(START_TX, ErrorCode::Success);
}

void getPositionCommand(Core& core, Communication& communication, Packet& packet)
{
	LOG_INFO("running getPos command");

	communication.sendPosPacket(GET_POSITION_TX, 44, 44);
}

// Long running commands
void gotopositionCommand(Core& core, Communication& communication, Packet& packet)
{
	LOG_INFO("running gotoPositionCommand");
	if (!core.started)
	{
		communication.sendErrorPacket(GOTO_POSITION_TX, ErrorCode::NotStarted);
		return;
	}

	if (core.longRunningCommandInProgress)
	{
		communication.sendErrorPacket(GOTO_POSITION_TX, ErrorCode::LongRunningCommandInProgress);
		return;
	}
	
	byte x = packet.payload[0];
	byte y = packet.payload[1];

	if (x >= MAX_WIDTH_ASR || y >= MAX_HEIGHT_ASR)
	{
		communication.sendErrorPacket(GOTO_POSITION_TX, ErrorCode::PositionOutOfBound);
		return;
	}
	core.longRunningCommandInProgress = true;
	
	//TODO Jim doe je ding

	communication.sendErrorPacket(GOTO_POSITION_TX, ErrorCode::Success);
	core.longRunningCommandInProgress = false;
}

void pickCommand(Core& core, Communication& communication, Packet& packet)
{
	LOG_INFO("running pick command");
	if (!core.started)
	{
		communication.sendErrorPacket(GOTO_POSITION_TX, ErrorCode::NotStarted);
		return;
	}

	if (core.longRunningCommandInProgress)
	{
		communication.sendErrorPacket(GOTO_POSITION_TX, ErrorCode::LongRunningCommandInProgress);
		return;
	}
	core.longRunningCommandInProgress = true;

	// TODO Jim doe je ding

	communication.sendErrorPacket(PICK_TX, ErrorCode::Success);
	core.longRunningCommandInProgress = false;
}

void unloadCommand(Core& core, Communication& communication, Packet& packet)
{
	LOG_INFO("unload command");
	communication.sendErrorPacket(UNLOAD_TX, ErrorCode::Success);
}
