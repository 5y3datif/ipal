/**
 * @file encoderFunctions.ino
 * @brief Interrupt functions
 * @details This file contains the functions that get called when the interrupt event occurs because of a rising signal from the encoders in the motors. 
 * */

/**
 * @brief Interrupt function
 * @details This function gets called when a rising pulse is received from the encoder on the left motor.
 * @return void
* */
void encoderLeft() {
  countLeft++;

  // Average of countLeft and countRight //
  stepsOverall = (countLeft + countRight) / 2;
}

/**
 * @brief Interrupt function
 * @details This function gets called when a rising pulse is received from the encoder on the right motor.
* */
void encoderRight() {
  countRight++;

  // Average of countLeft and countRight //
  stepsOverall = (countLeft + countRight) / 2;
}
