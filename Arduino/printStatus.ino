/**
 * @file printStatus.ino
 * @brief A debugging option. 
 * @details Runs in loop() and displays the movement status of the robot on all serial communication channels.
 * 
 * */
 
 /**
  * @brief Prints status on all serial communication channels.
  * @details Prints
  * @li countLeft
  * @li countRight
  * @li stepsOverall
  * @li stepsMotion
  * @li pwmLeft
  * @li pwmRight
  * 
  * */
 
 void printStatus() {
    Serial2.print(motionText(currentMotion));
    Serial2.print("; Count; L:");
    Serial2.print(countLeft);
    Serial2.print(", R:");
    Serial2.print(countRight);
    Serial2.print(", Steps; Overall:");
    Serial2.print(stepsOverall);
    Serial2.print(", Steps; Motion:");
    Serial2.print(stepsMotion);
    Serial2.print(", PWM; L:");
    Serial2.print(pwmLeft);
    Serial2.print(", R:");
    Serial2.print(pwmRight);
    Serial2.println();
}


/**
 * @brief Converts the motion integer to text. 
 * @param motionInteger Front, Rear, Left, Right - integers from preprocessor directives
 * @return  String
 * */
String motionText(int motionInteger) {
  switch (motionInteger) {
    case Stop:
      return "Stop";
      break;
    case Front:
      return "Front";
      break;
    case Rear:
      return "Rear";
      break;
    case Left:
      return "Left";
      break;
    case Right:
      return "Right";
      break;
  }
}
