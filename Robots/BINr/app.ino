/* BINr code
 * Author: Youri Sleurink
 * 2019
 */

//variables

bool dir = false; //direction of conveyor belt
                //false is left, true is right

bool dirs[] = {true, true, false, true, false, false};
              // multiple directions in an array

bool occ = false;   //product occupies belt

int BinrTime = 1000; //amount of miliseconds the conveyor belt
                     //takes to replace a product
int AsrTime = 1000;  //amount of miliseconds the ASR
                     //takes to replace a product

int powPin = 5;     //Arduino pin for power for motor
int dirPin = 4;     //Arduino pin for motor direction

int amount = 6;     //amount of items BINr need to replace


void setup() {
  // put your setup code here, to run once:
  
  pinMode(powPin, OUTPUT);  // motor power
  pinMode(dirPin, OUTPUT);  // motor direction
}

void loop() {
  // put your main code here, to run repeatedly:
  
  for(int a = 0; a < amount; a++){  // for each product coming in
    dir = dirs[a];
    
    digitalWrite(powPin, HIGH);     // turn on motor
    if(dir){                        // check direction of product is to the right
      digitalWrite(dirPin, HIGH);   // put motor direction to the right
    } else {
      digitalWrite(dirPin, LOW);    // put motor direction to the left
    }
    
    delay(BinrTime);                 // grant the system time to process
    digitalWrite(powPin, LOW);      // turn off motor
    delay(AsrTime);                  // grant time to put in new product

  }
}