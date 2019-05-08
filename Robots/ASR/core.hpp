#ifndef CORE_HPP
#define CORE_HPP
#include "packet.hpp"
#include "Arduino.h"
#include "communication.hpp"

class Core;

typedef void *(*command_ptr)(Core &core, Communication &communication);

struct CommandInfo{
public:
    CommandInfo(int id, bool longRunning, command_ptr command){
        this->Id = id;
        this->LongRunning = longRunning;
        this->Command = command;
    }

    byte Id;
    bool LongRunning;
    command_ptr Command;
};

class Core
{

private:
    static const int COMMAND_COUNT = 1;
    Communication communication;
    CommandInfo commands[COMMAND_COUNT] = {
        CommandInfo(1,false, nullptr)
    };
    bool longRunningCommandInProgress;
public:
    // Used by long-running commands and program loop
    // to contineu excecution of vital tasks.
    void pollProgramLoop();
};

void Core::pollProgramLoop(){
    Packet *packet = this->communication.receivePacket();
    if(packet != nullptr){
        for(int i = 0; i * sizeof(CommandInfo) < COMMAND_COUNT * sizeof(CommandInfo); i++){
            CommandInfo commandInfo = this->commands[i];
            if(commandInfo.Id == packet->CommandId)
            {
                commandInfo.Command(*this, this->communication);
            }
        }
    }
}

#endif