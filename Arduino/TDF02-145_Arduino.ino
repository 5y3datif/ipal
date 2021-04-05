/**
 * @file TDF02-145_Arduino.ino
 * @brief Sets everything up.
 * @details Main file. Contains setup() and loop(). Has all library inclusions, macros, and variables. 
 * */

/**
 * @class AutoPID 
 * @brief Library by R. Downing. Used to quickly run the PID algorithm.
 * */
#include <AutoPID.h>

/**
 * @def robot_speed_min
 * Minimum PWM for speed. It can range from 0 to 255.
 * 
 * @def robot_speed_max
 * Maximum PWM for speed. It can range from 0 to 255.
 * */
#define robot_speed_min 200
#define robot_speed_max 255

/**
 * @def KP
 * Proportional constant for PID
 * 
 * * @def KI
 * Integral constant for PID
 * 
 * @def KD
 * Derivative constant for PID
 * */
#define KP 0.012
#define KI 0.03
#define KD 0.0001

/**
 * @def motor_ENA
 * ENA pin of L298N motor driver is connected to Arduino pin 8
 * 
 * @def motor_ENB
 * ENB of L298N motor driver is connected to Arduino pin 9
 * 
 * @def motor_IN1
 * IN1 of L298N motor driver is connected to Arduino pin 11
 * 
 * @def motor_IN2
 * IN2 of L298N motor driver is connected to Arduino pin 10
 * 
 * @def motor_IN3
 * IN3 of L298N motor driver is connected to Arduino pin 12
 * 
 * @def motor_IN4
 * IN4 of L298N motor driver is connected to Arduino pin 13
 * */
#define motor_ENA 8
#define motor_ENB 9
#define motor_IN1 11
#define motor_IN2 10
#define motor_IN3 12
#define motor_IN4 13

/**
 * @def encoder_L_C1
 * Encoder pin from left motor is connected to Arduino pin 3
 * 
 * @def encoder_R_C1
 * Encoder pin from right motor is connected to Arduino pin 2
 * */
#define encoder_L_C1 3
#define encoder_R_C1 2

/**
 * @def Front
 * An integer used in the decision flow for locomotion
 * 
 * @def Rear
 * An integer used in the decision flow for locomotion
 * 
 * @def Right
 * An integer used in the decision flow for locomotion
 * 
 * @def Left
 * An integer used in the decision flow for locomotion
 * 
 * @def Stop
 * An integer used in the decision flow for locomotion
 * 
 * @def Start
 * An integer used in the decision flow for locomotion
 * 
 * @def CW
 * Clockwise. An integer used in the decision flow for locomotion.
 * 
 * @def CCW
 * Counterclockwise. An integer used in the decision flow for locomotion.
 * */
#define Front 1
#define Rear 2
#define Right 3
#define Left 4
#define Stop 0
#define Start 1
#define CW 2
#define CCW 3

/**
 * @brief Integer to keep count of encoder signals from the left motor
 * */
double countLeft = 0;

/**
 * @brief Integer to keep count of encoder signals from the right motor
 * */
double countRight = 0;

/**
 * @brief Integer to keep an average count of encoder signals from the both motors
 * */
double stepsOverall = 0;

/**
 * @brief Don't remember
 * */
double stepsMotion = 0;

/**
 * @brief PWM output for left motor
 * */
double pwmLeft;

/**
 * @brief PWM output for right motor
 * */
double pwmRight;

/**
 * @brief Integer to contain the present state of locomotion
 * */
int currentMotion = 0;

/**
 * @brief Constructor creates an instance of AutoPID named leftPID. Applies PID algorithm to left motor.
 * @param countLeft        Process Variable
 * @param countRight       Set Point
 * @param pwmLeft          Manipulated Value
 * @param robot_speed_min  The minimum value
 * @param robot_speed_max  The maximum value
 * */
AutoPID leftPID = AutoPID(&countLeft, &countRight, &pwmLeft, robot_speed_min, robot_speed_max, KP, KI, KD);

/** 
  * @brief Constructor creates an instance of AutoPID named rightPID. Applies PID algorithm to right motor.
 *  @param countRight        Process Variable
  * @param countLeft         Set Point
  * @param pwmRight          Manipulated Value
  * @param robot_speed_min   The minimum value
  * @param robot_speed_max   The maximum value
  * */
AutoPID rightPID = AutoPID(&countRight, &countLeft, &pwmRight, robot_speed_min, robot_speed_max, KP, KI, KD);

/**
 * @brief Carries command instruction string. Starts out empty.
 * */
String inputString = "";

/**
 * @def servoPitchPin
 * Servo for pitch movement (nodding of head - like when you indicate yes) of head is connected to Arduino pin 6  
 * 
 * @def servoYawPin
 * Servo for yaw movement (shaking of head - like when you indicate no) of head is connected to Arduino pin 5 
 * */
#define servoPitchPin 6
#define servoYawPin 5

/**
 * @def servoPitchCenter
 * Central angle in degrees
 * @def servoYawCenter
 * Central angle in degrees
 * 
 * @def maxpitchlimit
 * Maximum angle allowed for servo
 * @def maxyawlimit
 * Maximum angle allowed for servo
 * 
 * @def minpitchlimit
 * Minimum angle allowed for servo
 * @def minyawlimit
 * Minimum angle allowed for servo
 * */
#define servoPitchCenter 70
#define servoYawCenter 80
#define maxpitchlimit 180
#define minpitchlimit 55
#define maxyawlimit 180
#define minyawlimit 00

/**
 * @class VarSpeedServo 
 * @brief Library by netlabtoolkit on Github. Used for variable speed control of servo motors.
 * */
#include <VarSpeedServo.h>

/**
 * @brief Pitch servo motor defined as a VarSpeedServo object
 * */
VarSpeedServo pitchservo;  //yes

/**
 * @brief Yaw servo motor defined as a VarSpeedServo object
 * */
VarSpeedServo yawservo;  //no

/**
 * @brief Runs only once when the robot starts up. Sets up everything.
 * 
 * @details 
 * Sets up the PID algorithm.
 * Attaches both VarSpeedServo objects to their respective servo motor signal pins.
 * Writes initial angles to the servo motors.
 * Begins serial communication for channles 0, 1, and 2. 
 * 
 * */

void setup() {
    PID_initial();

    pitchservo.attach(servoPitchPin);
    yawservo.attach(servoYawPin);

    pitchservo.write(45, 100);
    yawservo.write(45, 100);

    Serial.begin(9600);
    Serial1.begin(9600);
    Serial2.begin(9600);
}

/**
 * @brief Loops constantly
 * @details Runs constantly. Nothing is put here because all instructions that Chotu receives are executed at once. There is no need for looping. In addition, when a new command is sent to Chotu, it is through the SerialEvent function, which works as an interrupt. 
 * 
 * Loop can be used during diagnosis or tuning by printing out the locomotion or other statuses. 
 * */
void loop() {
    //printStatus();
}
