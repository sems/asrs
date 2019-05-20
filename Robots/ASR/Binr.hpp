#ifndef BINR_HPP
#define BINR_HPP

#include "Arduino.h"

//Arduino pin for laser detection
#define laserPin 2

//Arduino pin for power for motor
#define powPin 5

//Arduino pin for motor direction
#define dirPin 4

inline void binRInitialize() {
	pinMode(powPin, OUTPUT);  // motor power
	pinMode(dirPin, OUTPUT);  // motor direction
	pinMode(laserPin, INPUT); // laser detection
}

inline void binRSetMotorDirLeft() {
	digitalWrite(dirPin, LOW);
}

inline void binRSetMotorDirRight() {
	digitalWrite(dirPin, HIGH);
}

inline bool binRIsLaserBlocked() {
	return digitalRead(laserPin) == LOW;
}

inline void binRStopMotor() {
	digitalWrite(powPin, LOW);
}

inline void binRStartMotor() {
	digitalWrite(powPin, HIGH);
}

#endif