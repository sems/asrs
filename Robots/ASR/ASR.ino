#include "predef.hpp"
#include "Arduino.h"
#include "packet.hpp"
#include "communication.hpp"
#include "core.hpp"

#ifndef ASR
#include "Binr.hpp"
#endif // ASR

Communication communication;
Core core(communication);

void setup()
{
#ifndef ASR
	BinR::initialize();
#else
	pinMode(8, OUTPUT);
	digitalWrite(8, LOW);
#endif // ASR

	communication.initialize();
	core.logger.logInfo("Core Initialized");
}

void loop()
{
	core.pollProgramLoop();
	delay(1000);
}
