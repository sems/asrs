#include "Arduino.h"
#include "packet.hpp"
#include "communication.hpp"
#include "core.hpp"

Communication communication;
Core core(communication);

void setup()
{
	communication.initialize();
	core.logger.logInfo("Core Initialized");
}

void loop()
{
	core.pollProgramLoop();
}
