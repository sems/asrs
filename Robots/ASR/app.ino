#include "Arduino.h"
#include "packet.hpp"
#include "core.hpp"

Core core;

void setup()
{
    core = Core();
    Serial.begin(9600);
}

void loop()
{
	core.pollProgramLoop();
}
