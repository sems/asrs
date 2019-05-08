//#include "Arduino.h"
#include "movement.hpp"
#include "AccelStepper.h"
#include "MultiStepper.h"

AccelStepper stepper_A(1, 2, 5); // Left static motor
AccelStepper stepper_B(1, 3, 6); // Right static motor
AccelStepper stepper_Z(1, 4, 7); // Z-axis, the fork thingy that acutally picks items

boolean isOk = false;
boolean homed = false;
boolean isReady = false;

long initial_homing = 1; // Used to Home Stepper at startup

int picked = 0;

int moveXY(int x, int y)
{
}

void pickItem()
{
        if (picked < 4)
    {
        stepper_Z.setMaxSpeed(1200.0); // Set Max Speed of Stepper (Slower to get better accuracy)
        stepper_Z.setAcceleration(2000.0);
        stepper_Z.moveTo(-100); // Set the position to move to
    }
    else
    {
        // send error message
    }
}

void dropItem()
{
    if (picked > 0)
    {
        stepper_Z.setMaxSpeed(1200.0); // Set Max Speed of Stepper (Slower to get better accuracy)
        stepper_Z.setAcceleration(2000.0);
        stepper_Z.moveTo(100); // Set the position to move to
    }
    else
    {
        // send error message
    }
}

void homeZ()
{

    Serial.println("homing Z (depth)");

    stepper_Z.setMaxSpeed(1000.0);     // Set Max Speed of Stepper (Slower to get better accuracy)
    stepper_Z.setAcceleration(2000.0); // Set Acceleration of Stepper

    while (digitalRead(LIMIT_SWITCH_Y))
    {                                     // Make the Stepper move CCW until the switch is activated
        stepper_Z.moveTo(initial_homing); // Set the position to move to
        initial_homing++;                 // Decrease by 1 for next move if needed
        stepper_Z.run();                  // Start moving the stepper
        delay(5);
    }
    Serial.println("Z limit switch activated");

    stepper_Z.setCurrentPosition(0);  // Set the current position as zero for now
    stepper_Z.setMaxSpeed(800.0);     // Set Max Speed of Stepper (Slower to get better accuracy)
    stepper_Z.setAcceleration(800.0); // Set Acceleration of Stepper
    initial_homing = -1;

    while (!digitalRead(LIMIT_SWITCH_Y))
    { // Make the Stepper move CW until the switch is deactivated
        stepper_Z.moveTo(initial_homing);
        stepper_Z.run();
        initial_homing--;
        delay(5);
    }

    stepper_Z.setCurrentPosition(0);
    Serial.println("Z homed");
}

void homeX()
{

    Serial.println("homing X");

    stepper_A.setMaxSpeed(1000.0);     // Set Max Speed of Stepper (Slower to get better accuracy)
    stepper_A.setAcceleration(1000.0); // Set Acceleration of Stepper
    stepper_B.setMaxSpeed(1000.0);
    stepper_B.setAcceleration(1000.0);

    while (digitalRead(LIMIT_SWITCH_Y))
    {                                     // Make the Stepper move CCW until the switch is activated
        stepper_A.moveTo(initial_homing); // Set the position to move to
        stepper_B.moveTo(initial_homing); // Set the position to move to
        initial_homing--;                 // Decrease by 1 for next move if needed
        stepper_A.run();                  // Start moving the stepper
        stepper_B.run();                  // Start moving the stepper
        delay(5);
    }
    Serial.println("X limit switch activated");

    stepper_A.setCurrentPosition(0);  // Set the current position as zero for now
    stepper_A.setMaxSpeed(800.0);     // Set Max Speed of Stepper (Slower to get better accuracy)
    stepper_A.setAcceleration(800.0); // Set Acceleration of Stepper
    stepper_B.setCurrentPosition(0);  // Set the current position as zero for now
    stepper_B.setMaxSpeed(800.0);     // Set Max Speed of Stepper (Slower to get better accuracy)
    stepper_B.setAcceleration(800.0); // Set Acceleration of Stepper
    initial_homing = 1;

    while (!digitalRead(LIMIT_SWITCH_Y))
    {                                     // Make the Stepper move CW until the switch is deactivated
        stepper_A.moveTo(initial_homing); // Set the position to move to
        stepper_B.moveTo(initial_homing);
        stepper_A.run();
        stepper_B.run();
        initial_homing++;
        delay(5);
    }

    stepper_B.setCurrentPosition(0);
    stepper_A.setCurrentPosition(0);
    Serial.println("X homed");
}