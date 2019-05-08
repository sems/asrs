#ifndef MOVEMENT_HPP
#define MOVEMENT_HPP
#include "arduino.h"

const int LIMIT_SWITCH_X = 9;
const int LIMIT_SWITCH_Z = 10;
const int LIMIT_SWITCH_Y = 11;

int moveXY(int X, int Y);

int pick();

int drop();

void homeZ();


#endif