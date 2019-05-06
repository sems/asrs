#include "arduino.h"
#include <stdint.h>

class Button
{
private:
    uint8_t inputPin;

public:
    bool isPressed();
    void onPress(void (*)());

    Button(uint8_t inputPin);
};