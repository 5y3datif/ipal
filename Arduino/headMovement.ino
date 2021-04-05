/**
 * @file headMovement.ino
 * 
 * @brief Controls the head
 * @details Writes angles to the servos in the neck.
 * */

/**
 * @brief Tasked with moving both servos
 * 
 * @param yaw The yaw angle in degrees extracted in decider()
 * @param pitch The pitch angle in degrees extracted in decider()
 * 
 * @return void
 * */
void movehead(int yaw, int pitch) {
  if (yaw > maxyawlimit)
    yaw = maxyawlimit;
  else if (yaw < minyawlimit)
    yaw = minyawlimit;

  if (pitch > maxpitchlimit)
    pitch = maxpitchlimit;
  else if (pitch < minpitchlimit)
    pitch = minpitchlimit;

  // Writes the angle to the servo at a "100" speed //

  pitchservo.write(pitch, 100/*, 255, true*/); //pitch = yes, nodding motion
  yawservo.write(yaw, 100/*,255, true*/); //yaw = no, shaking motion
}


/**
 * @brief Predefined head movements (nodding and shaking for some time)
 * @param    Direction    Nodding ('Y') or shaking ('N')
 * @param    Speed        Speed with which to move the head
 * @param    Duration     Time in seconds for which to move the head
 * @param    Empty        Empty parameter. Exists to keep with the format of the inputString.
 * @return void
 * 
 * */
void headMotion_predefined(char Direction, int Speed, int Duration, int Empty) {
  unsigned long previousMillis;
  const long interval = Duration * 1000;
  unsigned long currentMillis = millis();
  previousMillis = currentMillis;

  // loop based on millis() passed //
  while (abs(currentMillis - previousMillis) <= interval) {
    
    currentMillis = millis();

    switch (Direction) {

      case 'Y':
        // nodding //
        yawservo.write(servoYawCenter, 255, true); // Takes head to center first //
        pitchservo.write(servoPitchCenter + 25, Speed/*100 works best*/, true);
        delay(500);
        yawservo.write(servoYawCenter, 255, true);
        pitchservo.write(servoPitchCenter - 25 /*This number can be changed*/, Speed, true);
        delay(500);
        break;

      case 'N':
        //head shaking
        pitchservo.write(servoPitchCenter, 255, true);
        yawservo.write(servoYawCenter + 15, Speed /*40 works best*/, true);
        delay(500);
        pitchservo.write(servoPitchCenter, 255, true);
        yawservo.write(servoYawCenter - 15  /*This number can be changed*/, Speed, true);
        delay(500);
        break;
    }

    currentMillis = millis();
  }

  // Setting it back to center facing //
  pitchservo.write(servoPitchCenter, 255, true); //pitch = yes, nodding motion
  yawservo.write(servoYawCenter, 255, true); //yaw = no, shaking motion
}
