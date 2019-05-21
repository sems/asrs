#ifndef BINR_HPP
#define BINR_HPP

#include "Arduino.h"

namespace BinR 
{
	//Arduino pin for laser detection
#define LASER_PIN 2

//Arduino pin for power for motor
#define POWER_PIN 5

//Arduino pin for motor direction
#define DIR_PIN 4

	inline void initialize()
	{
		pinMode(POWER_PIN, OUTPUT); // motor power
		pinMode(DIR_PIN, OUTPUT); // motor direction
		pinMode(LASER_PIN, INPUT); // laser detection
	}

	inline void set_motor_dir_left()
	{
		digitalWrite(DIR_PIN, LOW);
	}

	inline void set_motor_dir_right()
	{
		digitalWrite(DIR_PIN, HIGH);
	}

	inline bool is_laser_blocked()
	{
		return digitalRead(LASER_PIN) == LOW;
	}

	inline void stop_motor()
	{
		digitalWrite(POWER_PIN, LOW);
	}

	inline void start_motor()
	{
		digitalWrite(POWER_PIN, HIGH);
	}
}
#endif
