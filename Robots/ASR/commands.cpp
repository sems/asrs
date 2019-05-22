#include "commands.hpp"
#include "packet.hpp"
#include "core.hpp"
#include "communication.hpp"
#include "constants.hpp"
#include "movement.hpp"

#ifndef ASR
#include "Binr.hpp"
#endif // ASR

void statusCommand(Core& core, Communication& communication, Packet& packet)
{
	core.logger.logInfo("running status command");
	communication.sendStatusPacket(STATUS_TX, core.status);
}


void stopCommand(Core& core, Communication& communication, Packet& packet)
{
	core.logger.logInfo("running stop command");
	core.started = false;
	communication.sendErrorPacket(STOP_TX, Success);
}

void startCommand(Core& core, Communication& communication, Packet& packet)
{
	core.logger.logInfo("running start command");
	core.started = true;
	communication.sendErrorPacket(START_TX, Success);
}

#ifdef ASR

void getPositionCommand(Core& core, Communication& communication, Packet& packet)
{
	core.logger.logInfo("running getPos command");
	XY_POSITION_ARRAY xy = core.movement.getXYPos();

	communication.sendPosPacket(GET_POSITION_TX, static_cast<byte>(xy.x), static_cast<byte>(xy.y));
}

// Long running commands
void gotopositionCommand(Core& core, Communication& communication, Packet& packet)
{
	core.logger.logInfo("running gotoPositionCommand");
	if (!core.started)
	{
		core.logger.logError("Not started");
		communication.sendErrorPacket(GOTO_POSITION_TX, ErrorCode::NotStarted);
		return;
	}

	if (core.longRunningCommandInProgress)
	{
		core.logger.logInfo("Long running command already in progress");
		communication.sendErrorPacket(GOTO_POSITION_TX, ErrorCode::LongRunningCommandInProgress);
		return;
	}

	byte x = packet.payload[0];
	byte y = packet.payload[1];

	if (x >= MAX_WIDTH_ASR || y >= MAX_HEIGHT_ASR)
	{
		core.logger.logInfo("Pos out of bound");
		communication.sendErrorPacket(GOTO_POSITION_TX, ErrorCode::PositionOutOfBound);
		return;
	}
	core.longRunningCommandInProgress = true;
	core.logger.logInfo("begin move");
	core.movement.moveXY(x, y);

	int counter = 0;
	while (core.movement.steppers1.run())
	{
		counter++;
		if (counter % 100 == 0)
		{
			core.pollProgramLoop();
			if (!core.started)
			{
				core.logger.logError("Stopped");
				communication.sendErrorPacket(GOTO_POSITION_TX, ErrorCode::NotStarted);
				core.longRunningCommandInProgress = false;
				return;
			}
			counter = 0;
		}
	}
	core.logger.logInfo("done");
	communication.sendErrorPacket(GOTO_POSITION_TX, ErrorCode::Success);
	core.longRunningCommandInProgress = false;
}

const int maxPick = 4;

bool runBoth(AccelStepper a, AccelStepper b) {
	bool b_a = a.run();
	bool b_b = b.run();
	return b_a || b_a;
}

void pickCommand(Core& core, Communication& communication, Packet& packet)
{
	core.logger.logInfo("running pick command");
	if (!core.started)
	{
		communication.sendErrorPacket(PICK_TX, ErrorCode::NotStarted);
		return;
	}

	if (core.longRunningCommandInProgress)
	{
		communication.sendErrorPacket(PICK_TX, ErrorCode::LongRunningCommandInProgress);
		return;
	}

	core.longRunningCommandInProgress = true;

	//Only run steps if there is room on the picker
	if (core.movement.picked < maxPick) {
		int state = 0;
		while (state < 3) {
			// Sends current state and increments it
			core.movement.pick(state++);

			while (core.movement.stepper_Z.run() || core.movement.steppers1.run())
			{
				core.pollProgramLoop();
				if (!core.started) {
					core.logger.logError("Stopped");
					communication.sendErrorPacket(PICK_TX, ErrorCode::NotStarted);
					core.longRunningCommandInProgress = false;
					return;
				}
			}
		}


	}
	else {
		core.logger.logError("Picker Full");
		communication.sendErrorPacket(PICK_TX, ErrorCode::NoMoreLoadingSpace);
	}


	communication.sendErrorPacket(PICK_TX, Success);
	core.longRunningCommandInProgress = false;
}

void unloadCommand(Core& core, Communication& communication, Packet& packet)
{
	core.logger.logInfo("unload command");
	communication.sendErrorPacket(UNLOAD_TX, Success);
}

#else

void binrDrop(Core& core, Communication& communication, Packet& packet) {
	core.logger.logInfo("binr drop");
	if (!core.started)
	{
		communication.sendErrorPacket(DROP_BINR_TX, ErrorCode::NotStarted);
		return;
	}

	if (core.longRunningCommandInProgress)
	{
		communication.sendErrorPacket(DROP_BINR_TX, ErrorCode::LongRunningCommandInProgress);
		return;
	}
	core.longRunningCommandInProgress = true;


	const auto dir = static_cast<Direction>(packet.payload[0]);
	if (dir == Direction::Left) {
		BinR::set_motor_dir_left();
	}
	else {
		BinR::set_motor_dir_right();
	}
	BinR::start_motor();
	while (BinR::is_laser_blocked()) { // wait untill product is on belt
		core.pollProgramLoop();
		delay(100);
	}
	BinR::stop_motor();


	communication.sendErrorPacket(DROP_BINR_TX, ErrorCode::Success);
	core.longRunningCommandInProgress = false;
}

#endif // ASR
