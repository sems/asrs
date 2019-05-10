#include "commandInfo.hpp"

CommandInfo::CommandInfo(int id, bool longRunning, command_ptr command){
        this->Id = id;
        this->LongRunning = longRunning;
        this->Command = command;
}