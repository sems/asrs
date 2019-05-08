#ifndef COMMUNICATION_HPP
#define COMMUNICATION_HPP
#include "arduino.h"
#include "packet.hpp"

class Communication
{
private:
    
public:
    void initialize();
    Packet* receivePacket();
    void sendPacket(Packet &packet);
};

Packet* Communication::receivePacket(){
    if (Serial.available() > 0) 
    {
        
        byte payloadSizeBuffer[1];
        Serial.readBytes(payloadSizeBuffer, 1);
        byte *packetBuffer = new byte[payloadSizeBuffer[0] + 4];
        packetBuffer[0] = payloadSizeBuffer[0];
        Serial.readBytes(
            packetBuffer+1, //Skip payloadSize byte
            payloadSizeBuffer[0] + 3
        );
        return new Packet(packetBuffer);
    }
    return nullptr;
}

void Communication::sendPacket(Packet &packet){
    Serial.write(packet.Raw, packet.PayloadSize + 4);
}
#endif