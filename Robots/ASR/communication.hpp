#ifndef COMMUNICATION_HPP
#define COMMUNICATION_HPP

#include "arduino.h"
#include "packet.hpp"
#include "utils.hpp"

class Communication
{
private:
    
public:
    void initialize();
    Packet* receivePacket();
    void sendPacket(Packet *packet);
};


#endif