#ifndef STATUS_HPP
#define STATUS_HPP

#include "Arduino.h"

enum Status : byte {
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