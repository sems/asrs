#include "Packet.h"

void setup()
{
    Serial.begin(9600);
    const uint8_t size = 2;
    uint8_t pos[size] = {8, 9};
    uint8_t commandId = 110;

    Packet packet(commandId, pos, size);

    Serial.write(packet.getPacketPtr(), packet.getPacketSize());
}

void loop()
{
}
