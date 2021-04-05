  /**
   * @file stopMotion.ino
   * @brief Stops robot
   * @details Stops all motion of the robot. Overwrites everything.
   * */
  
  
  /**
   * @brief Commands both motors to stop spinning.
   * @return void
   * */
void stopMotion() {
  motor(Left, Stop, Stop);
  motor(Right, Stop, Stop);
  currentMotion = Stop;

  // Clear the enocder counters. //
  countLeft = 0;  
  countRight = 0;  
}
