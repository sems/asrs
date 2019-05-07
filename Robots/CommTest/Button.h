#include "arduino.h"
#include <stdint.h>

#ifndef h_button
#define h_button

class Button
{
private:
    uint8_t inputPin;

public:
    bool isPressed();
    void onPress(void (*)());

    Button(uint8_t inputPin);
};

#endif