#include "commands.hpp"
#include "packet.hpp"
#include "core.hpp"
#include "communication.hpp"
#include "constants.hpp"
#include "movement.hpp"

#define LOGGING;

//This define is used to disable code generation for logs

#ifdef LOGGING
#define LOG_INFO(MESSAGE) core.logger.logInfo(MESSAGE)
#define LOG_ERROR(MESSAGE) core.logger.logError(MESSAGE)
#else
#define LOG_INFO(MESSAGE)
#define LOG_ERROR(MESSAGE)
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
	XY_POSITION_ARRAY xy = core.movement.getXYPos();
	
	communication.sendPosPacket(GET_POSITION_TX,static_cast<byte>(xy.x), static_cast<byte>(xy.y));
}

// Long running commands
void gotopositionCommand(Core& core, Communication& communication, Packet& packet)
{
	LOG_INFO("running gotoPositionCommand");
	if (!core.started)
	{
		LOG_ERROR("Not started");
		communication.sendErrorPacket(GOTO_POSITION_TX, ErrorCode::NotStarted);
		return;
	}

	if (core.longRunningCommandInProgress)
	{
		LOG_ERROR("Long running command already in progress");
		communication.sendErrorPacket(GOTO_POSITION_TX, ErrorCode::LongRunningCommandInProgress);
		return;
	}
	
	byte x = packet.payload[0];
	byte y = packet.payload[1];

	if (x >= MAX_WIDTH_ASR || y >= MAX_HEIGHT_ASR)
	{
		LOG_ERROR("Pos out of bound");
		communication.sendErrorPacket(GOTO_POSITION_TX, ErrorCode::PositionOutOfBound);
		return;
	}
	core.longRunningCommandInProgress = true;
	LOG_INFO("begin move");
	core.movement.moveXY(x, y);

	int counter = 0;
	while (core.movement.steppers1.run())
	{
		counter++;
		if (counter % 100 == 0) {
			core.pollProgramLoop();
			if (!core.started)
			{
				LOG_ERROR("Stopped");
				communication.sendErrorPacket(GOTO_POSITION_TX, ErrorCode::NotStarted);
				core.longRunningCommandInProgress = false;
				return;
			}
			counter = 0;
		}
	}
	LOG_INFO("done");
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
