#ifndef MOVEMENT_HPP
#define MOVEMENT_HPP
#include "arduino.h"

const int LIMIT_SWITCH_X = 9;
const int LIMIT_SWITCH_Y = 10;
const int LIMIT_SWITCH_Z = 11;

struct XY_POSITION_ARRAY
{
    float x;
    float y;
};

void initializeMovement();

void moveXY(int X, int Y);

void runXY();

int ccts_a(int x, int y); //Convert Coordinate to Steps for motor a

int ccts_b(int x, int y); //Convert Coordinate to Steps for motor b

void pick();

void drop();

XY_POSITION_ARRAY getXYPos();

void homeZ();

void homeX();

void homeY();

#endif