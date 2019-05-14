#ifndef COMMUNICATION_HPP
#define COMMUNICATION_HPP

#include "arduino.h"

class Packet;

class Communication
{
public:
    void initialize();
    Packet* receivePacket();
    void sendPacket(Packet *packet);
};


#endif