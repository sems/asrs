#ifndef MOVEMENT_HPP
#define MOVEMENT_HPP

#include "arduino.h"
#include "AccelStepper.h"
#include "MultiStepper.h"

#define LIMIT_SWITCH_X 9
#define LIMIT_SWITCH_Y 10
#define LIMIT_SWITCH_Z 11

struct XY_POSITION_ARRAY
{
	float x;
	float y;
};

extern const int max_items;
extern const int steps_item_width;

class Movement
{
public:
	AccelStepper stepper_A;
	AccelStepper stepper_B;
	AccelStepper stepper_Z;
	MultiStepper steppers1;
	XY_POSITION_ARRAY xyPos;
	boolean isOk;
	boolean homed;
	boolean isReady;

	long initial_homing_Z; // Used to Home Stepper at startup
	long initial_homing_A;
	long initial_homing_B;
	int xpos; //current X position
	int ypos; //current X position
	int picked; //number of items currently on the Z-axis (fork thingy)


	Movement();


	//sets the steps and direction for the motors to move to, doesn't actually move the ASR
	void moveXY(int X, int Y);

	//Convert Coordinate to Steps for motor a
	int ccts_a(int x, int y);

	//Convert Coordinate to Steps for motor b
	int ccts_b(int x, int y);

	//picks up an item with the Z-axis (fork thingy)
	void pick();

	//drops an item with the Z-axis (fork thingy)
	void drop();

	//sets the steppers to move to the usual drop spot
	void prepareDrop();

	//returns the current XY position as floats in a struct
	XY_POSITION_ARRAY getXYPos();


	/*
	homing might need some significant changes as right now it seems to be blocking, rather than non-blocking
	*/
	void homeZ();

	void homeX();

	void homeY();
};



#endif