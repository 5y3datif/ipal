/**
 * @file move_Direction.ino
 * @brief Responsible for motor control.
 * @details Contains code pertaining to movement and how much motion needs to happen.
 * */

/** 
 * @brief Handles the motor PWM and directions
 * @details 
 * Based on what the direction is, it decides which motor needs to be going CW (clockwise) and which CCW (counterclockwise).
 * 
 * @param Direction Front, Rear, Left, Right
 * @param steps Motion Parameter
 * 
 * @return void
 * */
void moveDirection(int Direction, double steps) {

  // Uncomment for debugging //
  //Serial.print(motionText(currentMotion));
  //Serial.println(motionText(Direction));

  // stepsOverall increases until the difference between stepsMotion (steps taken while moving until now) and it becomes greater than the steps the robot needs to take //
  while (stepsOverall - stepsMotion < steps && !Serial2.available()) {  
    
    // Uncomment for debugging //
    //Serial2.println("stepsOverall - stepsMotion < steps");
    //Serial2.print(pwmRight); Serial2.print(" "); Serial2.print(pwmLeft); Serial2.print(" | "); Serial2.print(countRight); Serial2.print(" "); Serial2.print(countLeft); Serial2.print(" "); Serial2.print(stepsOverall); Serial2.println(" | ");
    
    if (currentMotion == Direction) { 
      // Check to save from applying PID while changing direction. //
      
      // runs PID and updates pwmLeft and pwmRight// 
      rightPID.run();
      leftPID.run();

      switch (Direction) {
        // Applying updated PWM values //
        case Front:
          motor(Right, pwmRight, CCW);
          motor(Left, pwmLeft, CW);
          break;

        case Rear:
          motor(Right, pwmRight, CW);
          motor(Left, pwmLeft, CCW);
          break;

        case Right:
          motor(Right, pwmRight, CW);
          motor(Left, pwmLeft, CW);
          break;

        case Left:
          motor(Right, pwmRight, CCW);
          motor(Left, pwmLeft, CCW);
          break;
      }
    } else {
      
      // changes direction //
      currentMotion = Direction;
      // saves steps so far to stepsMotion setting difference to zero //
      stepsMotion = stepsOverall;
      
      // Uncomment for debugging //
      //Serial2.println("currentMotion = Direction; stepsMotion = stepsOverall;");
    }
  }
  stopMotion();
  //Serial2.println("stepsOverall - stepsMotion >= steps  stopMotion(); stepsMotion = stepsOverall;");
}



/**
 * @brief     Controls both motors' angular direction and their speed
 * @param     LeftRight   Defines which motor
 * @param     pwm         PWM signal to control motor speed
 * @param     CWCCW       CW=ClockWise, CCW = CounterclockWise. The direction of the motor when looking at it down the shaft from the wheel's end.
 * @return void             
 * */
void motor(int LeftRight, int pwm, int CWCCW) {
  switch (LeftRight) {
    case Right:
      switch (CWCCW) {
        case CW:
          digitalWrite(motor_IN1, HIGH);
          digitalWrite(motor_IN2, LOW);
          break;
        case CCW:
          digitalWrite(motor_IN1, LOW);
          digitalWrite(motor_IN2, HIGH);
          break;
      }
      
      // Commented to disable PID //
      // digitalWrite(motor_ENA,HIGH);
      // analogWrite(motor_ENA, pwm);
      
      // The following code replaces PID. Only works because both motors are mechanically similar. //
      if (pwm > 0)
        analogWrite(motor_ENA, 255);
      else
        analogWrite(motor_ENA, 0);
      break;

    case Left:
      switch (CWCCW) {
        case CW:
          digitalWrite(motor_IN3, HIGH);
          digitalWrite(motor_IN4, LOW);
          break;
        case CCW:
          digitalWrite(motor_IN3, LOW);
          digitalWrite(motor_IN4, HIGH);
          break;
      }
      
      // Commented to disable PID //
      // analogWrite(motor_ENB, pwm);

      // The following code replaces PID. Only works because both motors are mechanically similar. //
      if (pwm > 0)
        analogWrite(motor_ENB, 255);
      else
        analogWrite(motor_ENB, 0);
      break;
  }
}
