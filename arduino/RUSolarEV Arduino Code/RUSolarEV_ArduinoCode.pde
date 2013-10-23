/*
  Sends sensor data to Android, receives commands from Android
  (needs RUSolarEV and Amarino app installed and running on Android)
  last modified on 12-17-2012
  Authors:Anton Krivosheyev, Kevin Sung
*/
 
#include <MeetAndroid.h>

MeetAndroid meetAndroid;
int sensor1 = 0;
int sensor2 = 1;
int sensor3 = 2;
int sensor4 = 3;
int sensor5 = 4;
int rightfront = 2;
int leftfront = 3;
int headlight = 4;
boolean rightblink = false;
boolean leftblink = false;
boolean headlightOn = false;
boolean hazardOn = false;
int rfState = LOW;
int lfState = LOW;
int hfState = LOW;
long rightPreviousMillis = 0;
long leftPreviousMillis = 0;
long interval = 500;

void setup()  
{
  // use the baud rate your bluetooth module is configured to 
  // not all baud rates are working well, i.e. ATMEGA168 works best with 57600
  Serial.begin(115200); //57600
 
  // we initialize pin 5 as an input pin
  pinMode(sensor1, INPUT);
  pinMode(sensor2, INPUT);
  pinMode(sensor3, INPUT);
  pinMode(sensor4, INPUT);
  pinMode(sensor5, INPUT);
  
  // register functions for blinking lights
  meetAndroid.registerFunction(blinkRight, 'r');
  meetAndroid.registerFunction(blinkLeft, 'l');  
  meetAndroid.registerFunction(turnOnHeadlight, 'h');
  meetAndroid.registerFunction(turnOnHazard, 'z');    
  
  //set lights as output pins
  pinMode(rightfront, OUTPUT);
  pinMode(leftfront, OUTPUT);
  pinMode(headlight, OUTPUT);
}

void loop()
{
  meetAndroid.receive(); // you need to keep this in your loop() to receive events
  
  // read input pin and send result to Android
  meetAndroid.send(analogRead(sensor1));
  delay(10);
  meetAndroid.send(analogRead(sensor2)+1024);
  delay(10);
  meetAndroid.send(analogRead(sensor3)+1024*2);
  delay(10);
  meetAndroid.send(analogRead(sensor4)+1024*3);
  delay(10);
  meetAndroid.send(analogRead(sensor5)+1024*4);
  delay(10);
  
  if (rightblink) {
    unsigned long currentMillis = millis();
    if(currentMillis - rightPreviousMillis > interval) {
      rightPreviousMillis = currentMillis;   
      
      if (rfState == LOW)
        rfState = HIGH;
      else
        rfState = LOW;
        
      digitalWrite(rightfront, rfState);
    }
  }
  else {
	rfState = LOW;
	digitalWrite(rightfront, rfState);
  }
  
  if (leftblink) {
    unsigned long currentMillis = millis();
    if(currentMillis - leftPreviousMillis > interval) {
      leftPreviousMillis = currentMillis;   

      if (lfState == LOW)
        lfState = HIGH;
      else
        lfState = LOW;

      digitalWrite(leftfront, lfState);
    }
  }
  else {
	lfState = LOW;
    digitalWrite(leftfront, lfState);
  }
  // add a little delay otherwise the phone is pretty busy
  delay(10);
  
  
  if (headlightOn) {
    digitalWrite(headlight, HIGH);
  }
  else {
    digitalWrite(headlight, LOW);
  }
  // add a little delay otherwise the phone is pretty busy
  delay(10);
  
  /*
    if (hazardOn) {
    unsigned long currentMillis = millis();
    if(currentMillis - leftPreviousMillis > interval) {
      leftPreviousMillis = currentMillis;   

      if (lfState == LOW && rfState == LOW){
        lfState = HIGH;
        rfState = HIGH;
        }
      else{
        lfState = LOW;
		rfState = LOW;
		}
      digitalWrite(leftfront, lfState);
      digitalWrite(rightfront, rfState);
    }
  }
  else {
	lfState = LOW;
	rfState = LOW;
    digitalWrite(leftfront, lfState);
    digitalWrite(rightfront, rfState);
  }
  // add a little delay otherwise the phone is pretty busy
  delay(10);
  */
  
}

//When the right turn button is pressed this funct. is called
void blinkRight(byte flag, byte numOfValues)
{
  if (meetAndroid.getInt() == 1)
    rightblink = true;
  else
    rightblink = false;
}

//When the left turn button is pressed this funct. is called
void blinkLeft(byte flag, byte numOfValues)
{
  if (meetAndroid.getInt() == 1)
    leftblink = true;
  else
    leftblink = false;
}

//When the headlight button is pressed this funct. is called
void turnOnHeadlight(byte flag, byte numOfValues)
{
  if (meetAndroid.getInt() == 1)
    headlightOn = true;
  else
    headlightOn = false;
}

//When the headlight button is pressed this funct. is called
void turnOnHazard(byte flag, byte numOfValues)
{
  if (meetAndroid.getInt() == 1){
    leftblink = true;
    rightblink = true;
    }
  else
    {
    leftblink = false;
    rightblink = false;
    }
}