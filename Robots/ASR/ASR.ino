

#include "Arduino.h"
#include "packet.hpp"
#include "communication.hpp"
#include "core.hpp"

Communication communication;
Core core(communication);

void setup()
{
	pinMode(8, OUTPUT);
	digitalWrite(8, LOW);
	communication.initialize();
	core.logger.logInfo("Core Initialized");
}

void loop()
{
	core.pollProgramLoop();
	delay(1000);
}
