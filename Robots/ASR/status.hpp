#ifndef STATUS_HPP
#define STATUS_HPP
#include "predef.hpp"
#include "Arduino.h"

enum Direction : byte
{
	Left = 0,
	Right = 1
};

enum ErrorCode : byte
{
	Success = 0,
	Negative = 1,
	PositionOutOfBound = 2,
	NoMoreLoadingSpace = 3,
	NotStarted = 4,
	LongRunningCommandInProgress = 5
};

enum Status : byte
{
	Stopped = 0,
	Idle = 1,
	Moving = 2,
	Loading = 3,
	Unloading = 4,
	Calibrating = 5,
	Spinning_right = 6,
	Spinning_left = 7
};
#endif
