#ifndef CORE_HPP
#define CORE_HPP

#include "predef.hpp"
#include "Arduino.h";
#include "commandInfo.hpp"
#include "status.hpp"
#include "logger.hpp"
#include "commands.hpp"
#include "packet.hpp"
#include "movement.hpp"

class Communication;

#ifdef ASR
#define COMMAND_COUNT 7
#else
#define COMMAND_COUNT 4
#endif // ASR


// This is the class that actually controls the ASR
class Core
{
private:
    

    // This array contains the commands. once a packet is received, the appropriate command will be looked up here
    CommandInfo commands[COMMAND_COUNT] = {
        CommandInfo(STATUS_RX, statusCommand),
        CommandInfo(STOP_RX, stopCommand),
        CommandInfo(START_RX, startCommand),

#ifdef ASR
        CommandInfo(GET_POSITION_RX, getPositionCommand),
        CommandInfo(GOTO_POSITION_RX, gotopositionCommand),
        CommandInfo(PICK_RX, pickCommand),
        CommandInfo(UNLOAD_RX, unloadCommand)
#else
		CommandInfo(DROP_BINR_RX, binrDrop),

#endif // ASR

    };
public:
	Movement movement;
	// Commands should check and update this variable
	// and should send an error if a long running command is already in progress
	bool longRunningCommandInProgress;
    Communication &communication;
    Core(Communication &communcation);
	bool started;
	Status status;
    Logger logger;
    // This function acts like the main program loop.
    // It's invoked by the program loop and by long running commands
    void pollProgramLoop();
};

#endif