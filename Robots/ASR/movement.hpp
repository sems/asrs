#ifndef MOVEMENT_HPP
#define MOVEMENT_HPP
#include "arduino.h"

const int LIMIT_SWITCH_X = 9;
const int LIMIT_SWITCH_Y = 10;
const int LIMIT_SWITCH_Z = 11;

const int max_items = 4; // Maximum number of items the ASR can hold
const int steps_item_width = 400; // Amount of steps that correlates to the width of one item
struct XY_POSITION_ARRAY
{
    float x;
    float y;
};

void initializeMovement();

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

#endif