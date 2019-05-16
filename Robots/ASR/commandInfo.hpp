#ifndef COMMAND_INFO_HPP
#define COMMAND_INFO_HPP

#include "Arduino.h"

class Core;
class Communication;
class Packet;

// Pointer type definition to a function that takes a Core and Communication as arguments
// Typedef is used, because we don't want to repeat this every time
typedef void *(*command_ptr)(Core &core, Communication &communication, Packet &packet);

class CommandInfo{
public:
    CommandInfo(int id, command_ptr command);
    // The id of the command
    byte Id;

    // A function pointer to the command
    command_ptr Command;
};

#include "core.hpp"

#endif