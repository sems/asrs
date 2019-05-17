//#include "Arduino.h"
#include "movement.hpp"
#include "AccelStepper.h"
#include "MultiStepper.h"

Movement::Movement() {
	this->isOk = false;
	this->homed = false;
	this->isReady = false;
	this->initial_homing_A = 1;
	this->initial_homing_B = 1;
	this->initial_homing_Z = 1;
	this->xpos = 0;
	this->ypos = 0;
	this->picked = 0;

	this->stepper_A = AccelStepper(1, 2, 5);
	this->stepper_B = AccelStepper(1, 3, 6);
	this->stepper_Z = AccelStepper(1, 4, 7);
	stepper_A.setMaxSpeed(2000.0);
	stepper_B.setMaxSpeed(2000.0);
	this->steppers1.addStepper(stepper_A);
	this->steppers1.addStepper(stepper_B);
}

const int steps_per_mm_0 = 40; //40 for 1/8th microstepping, 80 for 1/16th microstepping
const float unit_lenght_in_mm = 74.8;
const float unit_height_in_mm = 54.8;
const float steps_per_unit_lenght = steps_per_mm_0 * unit_lenght_in_mm;
const float steps_per_unit_height = steps_per_mm_0 * unit_height_in_mm;

const int steps_item_width = 400;

//sets the steps and direction for the motors to move to.
// Doesn't actually move the ASR
void Movement::moveXY(int x, int y)
{
	y = y * -1;
    long positions[2];
    positions[0] = ccts_a(x, y);
    positions[1] = ccts_b(x, y);

	stepper_A.setMaxSpeed(2000.0f);
	stepper_B.setMaxSpeed(2000.0f);

    steppers1.moveTo(positions);
}

/*
there currently is a problem with ccts_a and ccts_b it seems, where the Y-axis gets flipped.
this might help:
https://cloud.githubusercontent.com/assets/13655997/9081538/fe9085e0-3b97-11e5-968b-090be116c675.png
*/
int Movement::ccts_a(int x, int y) //Convert Coordinate to Steps for motor a
{
    return ((x * steps_per_unit_lenght) - xpos) + ((y * steps_per_unit_height) - ypos);
}
int Movement::ccts_b(int x, int y) //Convert Coordinate to Steps for motor b
{
    return ((x * steps_per_unit_lenght) - xpos) - ((y * steps_per_unit_height) - ypos);
}

// Picks one item.
void Movement::pick(int state)
{
	switch (state)
	{
	case 0:
		stepper_Z.setMaxSpeed(1200.0); // Set Max Speed of Stepper
		stepper_Z.setAcceleration(2000.0);
		stepper_Z.move(-steps_item_width);
		break;
	case 1:
		long positions[2];
		positions[0] = stepper_A.currentPosition() - 300;
		positions[1] = stepper_B.currentPosition() + 300;

		stepper_A.setMaxSpeed(700.0f);
		stepper_B.setMaxSpeed(700.0f);

		steppers1.moveTo(positions);

		break;
	case 2:
		stepper_Z.setMaxSpeed(1200.0); // Set Max Speed of Stepper
		stepper_Z.setAcceleration(2000.0);
		stepper_Z.move(steps_item_width);
		picked++;
		break;
	default:
		break;
	}
}

// Drops 1 item
// Does not move to usual drop spot.
void Movement::drop()
{
    if (picked > 0)
    {
        stepper_Z.setMaxSpeed(1200.0); // Set Max Speed of Stepper
        stepper_Z.setAcceleration(2000.0);
        stepper_Z.moveTo(steps_item_width);
        picked--;
    }
    else
    {
        // send error message
    }
}

//sets the steppers to move to the usual drop spot
void Movement::prepareDrop()
{
    moveXY(-1, 2);
}

XY_POSITION_ARRAY Movement::getXYPos()
{
    xyPos.x = 0.5 * (stepper_A.currentPosition() + stepper_B.currentPosition()) / steps_per_unit_lenght;
    xyPos.y = 0.5 * (stepper_A.currentPosition() - stepper_B.currentPosition()) / steps_per_unit_height;
    return xyPos;
}

void Movement::homeZ()
{

    //Serial.println("homing Z (depth)");

    stepper_Z.setMaxSpeed(1000.0);     // Set Max Speed of Stepper (Slower to get better accuracy)
    stepper_Z.setAcceleration(2000.0); // Set Acceleration of Stepper

    while (digitalRead(LIMIT_SWITCH_Y))
    {                                       // Make the Stepper move CCW until the switch is activated
        stepper_Z.moveTo(initial_homing_Z); // Set the position to move to
        initial_homing_Z++;                 // Decrease by 1 for next move if needed
        stepper_Z.run();                    // Start moving the stepper
        delay(5);
    }
    //Serial.println("Z limit switch activated");

    stepper_Z.setCurrentPosition(0);  // Set the current position as zero for now
    stepper_Z.setMaxSpeed(800.0);     // Set Max Speed of Stepper (Slower to get better accuracy)
    stepper_Z.setAcceleration(800.0); // Set Acceleration of Stepper
    initial_homing_Z = -1;

    while (!digitalRead(LIMIT_SWITCH_Y))
    { // Make the Stepper move CW until the switch is deactivated
        stepper_Z.moveTo(initial_homing_Z);
        stepper_Z.run();
        initial_homing_Z--;
        delay(5);
    }

    stepper_Z.setCurrentPosition(0);
    //Serial.println("Z homed");
}

void Movement::homeX()
{

    //Serial.println("homing X");

    stepper_A.setMaxSpeed(1000.0);     // Set Max Speed of Stepper (Slower to get better accuracy)
    stepper_A.setAcceleration(1000.0); // Set Acceleration of Stepper
    stepper_B.setMaxSpeed(1000.0);
    stepper_B.setAcceleration(1000.0);

    while (digitalRead(LIMIT_SWITCH_Y))
    {                                       // Make the Stepper move CCW until the switch is activated
        stepper_A.moveTo(initial_homing_A); // Set the position to move to
        stepper_B.moveTo(initial_homing_B); // Set the position to move to
        initial_homing_A--;                 // Decrease by 1 for next move if needed
        initial_homing_B--;
        stepper_A.run(); // Start moving the stepper
        stepper_B.run(); // Start moving the stepper
        delay(5);
    }
    //Serial.println("X limit switch activated");

    stepper_A.setCurrentPosition(0);  // Set the current position as zero for now
    stepper_A.setMaxSpeed(800.0);     // Set Max Speed of Stepper (Slower to get better accuracy)
    stepper_A.setAcceleration(800.0); // Set Acceleration of Stepper
    stepper_B.setCurrentPosition(0);  // Set the current position as zero for now
    stepper_B.setMaxSpeed(800.0);     // Set Max Speed of Stepper (Slower to get better accuracy)
    stepper_B.setAcceleration(800.0); // Set Acceleration of Stepper
    initial_homing_A = 1;
    initial_homing_B = 1;

    while (!digitalRead(LIMIT_SWITCH_Y))
    {                                       // Make the Stepper move CW until the switch is deactivated
        stepper_A.moveTo(initial_homing_A); // Set the position to move to
        stepper_B.moveTo(initial_homing_B);
        stepper_A.run();
        stepper_B.run();
        initial_homing_A++;
        initial_homing_B++;
        delay(5);
    }

    stepper_B.setCurrentPosition(0);
    stepper_A.setCurrentPosition(0);
    //Serial.println("X homed");
}

void Movement::homeY()
{
    initial_homing_A = -1;
    initial_homing_B = 1;

    stepper_A.setMaxSpeed(1000.0);     // Set Max Speed of Stepper (Slower to get better accuracy)
    stepper_A.setAcceleration(1000.0); // Set Acceleration of Stepper
    stepper_B.setMaxSpeed(1000.0);
    stepper_B.setAcceleration(1000.0);

    while (digitalRead(LIMIT_SWITCH_Y))
    {                                       // Make the Stepper move CCW until the switch is activated
        stepper_A.moveTo(initial_homing_A); // Set the position to move to
        stepper_B.moveTo(initial_homing_B); // Set the position to move to
        initial_homing_A++;                 // Decrease by 1 for next move if needed
        initial_homing_B--;
        stepper_A.run(); // Start moving the stepper
        stepper_B.run(); // Start moving the stepper
        delay(5);
    }
    

    stepper_A.setCurrentPosition(0);  // Set the current position as zero for now
    stepper_A.setMaxSpeed(800.0);     // Set Max Speed of Stepper (Slower to get better accuracy)
    stepper_A.setAcceleration(800.0); // Set Acceleration of Stepper
    stepper_B.setCurrentPosition(0);  // Set the current position as zero for now
    stepper_B.setMaxSpeed(800.0);     // Set Max Speed of Stepper (Slower to get better accuracy)
    stepper_B.setAcceleration(800.0); // Set Acceleration of Stepper
    initial_homing_A = 1;
    initial_homing_B = -1;

    while (!digitalRead(LIMIT_SWITCH_Y))
    {                                       // Make the Stepper move CW until the switch is deactivated
        stepper_A.moveTo(initial_homing_A); // Set the position to move to
        stepper_B.moveTo(initial_homing_B);
        stepper_A.run();
        stepper_B.run();
        initial_homing_A--;
        initial_homing_B++;
        delay(5);
    }

    stepper_B.setCurrentPosition(0);
    stepper_A.setCurrentPosition(0);
}