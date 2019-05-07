#include "Button.h"
//#include "arduino.h"

Button button(4);

void setup()
{
  Serial.begin(9600);
  pinMode(3, OUTPUT);
}

void loop()
{
  if (Serial.available() > 0)
  {
    // Reads size of payload and commandid
    size_t size = Serial.read();
    uint8_t command = Serial.read();

    Serial.println(size);
    Serial.println(command);

    digitalWrite(3, HIGH);

    int8_t *buffer = (int8_t *)malloc(size);

    // Wait until all data received
    bool allReceived = false;
    while (!allReceived)
    {
      if (Serial.available() >= size + 2)
      {
        allReceived = true;
        //digitalWrite(3, HIGH);
        Serial.println("received all");
      }
      else
      {
        delay(100);
        Serial.println((int)Serial.available());
        Serial.println(" / ");
        Serial.println((int)size + 2);
      }
    }

    // All data received
    for (size_t i = 0; i < size; i++)
    {
      buffer[i] = Serial.read();
    }
  }
}
