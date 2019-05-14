#ifndef COMMAND_INFO_HPP
#define COMMAND_INFO_HPP

#include "Arduino.h"

class Core;
class Communication;

// Pointer type definition to a function that takes a Core and Communication as arguments
// Typedef is used, because we don't want to repeat this every time
typedef void *(*command_ptr)(Core &core, Communication &communication);

class CommandInfo{
public:
    CommandInfo(int id, bool longRunning, command_ptr command);
    // The id of the command
    byte Id;

    // Commands that are long running shouldn't run simultaneously
    bool LongRunning;

    // A function pointer to the command
    command_ptr Command;
};

#include "core.hpp"

#endif