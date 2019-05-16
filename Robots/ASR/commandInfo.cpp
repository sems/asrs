#include "commandInfo.hpp"

CommandInfo::CommandInfo(int id, command_ptr command){
        this->Id = id;
        this->Command = command;
}