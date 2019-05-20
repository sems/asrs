#ifndef COMMAND_STATUS_HPP
#define COMMAND_STATUS_HPP

class Core;
class Communication;
class Packet;

void statusCommand(Core& core, Communication& communication, Packet& packet);

void stopCommand(Core& core, Communication& communication, Packet& packet);
void startCommand(Core& core, Communication& communication, Packet& packet);
void getPositionCommand(Core& core, Communication& communication, Packet& packet);


// Long running commands
void gotopositionCommand(Core& core, Communication& communication, Packet& packet);
void pickCommand(Core& core, Communication& communication, Packet& packet);
void unloadCommand(Core& core, Communication& communication, Packet& packet);
void homeCommand(Core& core, Communication& communication, Packet& packet);

#endif