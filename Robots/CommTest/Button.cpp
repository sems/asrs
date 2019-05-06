#include "Button.h"

Button::Button(uint8_t pin)
{
    inputPin = pin;
    pinMode(pin, INPUT);
}

bool Button::isPressed()
{
    if (digitalRead(inputPin))
    {
        return true;
    }
    else
    {
        return false;
    }
}