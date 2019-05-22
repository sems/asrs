// The file contains the command that are registered by the core.

#ifndef COMMAND_STATUS_HPP
#define COMMAND_STATUS_HPP

#include "predef.hpp"

class Core;
class Communication;
class Packet;

// The status code gives insight in what the ASR or BINr are doing
void statusCommand(Core& core, Communication& communication, Packet& packet);

// The stop command causes the ARS or BINr to stop with the current action.
// A start command is required to start the ASR or BINr.
void stopCommand(Core& core, Communication& communication, Packet& packet);

// Only needed if the ARS or BINr are stopped
void startCommand(Core& core, Communication& communication, Packet& packet);

<<<<<<< HEAD
// Returns the position of the ASR picker. position of 255 means unknown.
void getPositionCommand(Core& core, Communication& communication, Packet& packet);

=======
>>>>>>> master
// Long running commands
void gotopositionCommand(Core& core, Communication& communication, Packet& packet);
void pickCommand(Core& core, Communication& communication, Packet& packet);
void unloadCommand(Core& core, Communication& communication, Packet& packet);
void binrDrop(Core& core, Communication& communication, Packet& packet);

#endif
