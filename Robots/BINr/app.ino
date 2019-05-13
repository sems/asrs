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

int laserPin = 2;   //Arduino pin for laser detection
boolean check = false;  //

int powPin = 5;     //Arduino pin for power for motor
int dirPin = 4;     //Arduino pin for motor direction

int i = 0;          //counter integer
int amount = 6;     //amount of items BINr need to replace


void setup() {
  // put your setup code here, to run once:
  
  pinMode(powPin, OUTPUT);  // motor power
  pinMode(dirPin, OUTPUT);  // motor direction

  pinMode(laserPin, INPUT); // laser detection

  Serial.begin(9600); // Setup serial connection for print out to console
}

void loop() {
  // put your main code here, to run repeatedly:

  int laser = digitalRead(laserPin);

  
  dir = dirs[i];
  
  while(digitalRead(laserPin) == HIGH){ // wait untill product is on belt
    delay(100);  
  }
  
  digitalWrite(powPin, HIGH);     // turn on motor
  if(dir){                        // check direction of product is to the right
    digitalWrite(dirPin, HIGH);   // put motor direction to the right
  } else {
    digitalWrite(dirPin, LOW);    // put motor direction to the left
  }
  
  while(digitalRead(laserPin) == LOW){ // grant the system time to replace product
    delay(10);
  }
  
  digitalWrite(powPin, LOW);      // turn off motor

  if(i >= 6){
    i = 0;
  } else {
    i++;
  }
}
