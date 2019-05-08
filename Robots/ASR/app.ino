/*
FOR NOW, ALL THE LIMITSWITHCES ARE WIRED TO LIMIT_SWITCH_Z, PIN 11, "Z-" ON THE CNC-SHIELD
*/

#include "Arduino.h"
#include "packet.hpp"
#include "core.hpp"
#include "movement.hpp"
#include "AccelStepper.h"
#include "MultiStepper.h"
Core core;

void setup()
{
    core = Core();
    Serial.begin(9600);

    pinMode(LIMIT_SWITCH_X, INPUT_PULLUP);
    pinMode(LIMIT_SWITCH_Y, INPUT_PULLUP);
    pinMode(LIMIT_SWITCH_Z, INPUT_PULLUP);

    pinMode(8, OUTPUT); //enable pin for the stepper drivers
    digitalWrite(8, LOW);
}

void loop()
{
    core.pollProgramLoop();
}
