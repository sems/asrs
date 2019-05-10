#ifndef CORE_HPP
#define CORE_HPP

#include "packet.hpp"
#include "Arduino.h"
#include "communication.hpp"
#include "logger.hpp"
#include "commandInfo.hpp"

// This is the class that actually controls the ASR
class Core
{
private:
    static const int COMMAND_COUNT = 1;

    // This array contains the commands. once a packet is received, the appropriate command will be looked up here
    CommandInfo commands[COMMAND_COUNT] = {
        CommandInfo(1,false, nullptr)
    };
    // Commands should check and update this variable
    // and should send an error if a long running command is already in progress
    bool longRunningCommandInProgress;
public:
    Communication &communication;
    Core(Communication &communcation);
    
    Logger logger;
    // This function acts like the main program loop.
    // It's invoked by the program loop and by long running commands
    void pollProgramLoop();
};

#endif