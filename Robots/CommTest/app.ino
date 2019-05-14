#include "Packet.h"

void setup()
{
    Serial.begin(9600);
    const uint8_t size = 1;
    uint8_t pos[size] = {0};
    uint8_t commandId = 111;

    Packet packet(commandId, pos, size);

    Serial.write(packet.getPacketPtr(), packet.getPacketSize());
}

void loop()
{
}
