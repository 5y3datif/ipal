/**
 * @file PID_Initial.ino
 * @brief Sets up the PID algorithm.
 * @details Contains one function. Initializes pins and interrupt events from encoder needed for the PID algorithm.
 * */

/**
 * @brief sets up PID
 * @details 
 * @li Sets the Arduino pins connected to ENA, ENB, IN1, IN2, IN3 and IN4 to output.
 * @li Sets the Arduino pins connected to encoders to input.
 * @li Sets up interrupts on the encoder pins (these can only be set to specific pins on the Board). The interrupt is triggered on the RISING edge of the signal. Functions are called on this interrupt event.
 * @li Sets the AutoPID objects leftPID and rightPID to have a time step in milliseconds for the PID calculation.
 * 
 * @return void
 * */
void PID_initial() {
    pinMode(motor_ENA, OUTPUT);
    pinMode(motor_ENB, OUTPUT);
    pinMode(motor_IN1, OUTPUT);
    pinMode(motor_IN2, OUTPUT);
    pinMode(motor_IN3, OUTPUT);
    pinMode(motor_IN4, OUTPUT);

    pinMode(encoder_L_C1, INPUT);
    pinMode(encoder_R_C1, INPUT);

    attachInterrupt(digitalPinToInterrupt(encoder_L_C1), encoderLeft, RISING);
    attachInterrupt(digitalPinToInterrupt(encoder_R_C1), encoderRight, RISING);

    leftPID.setTimeStep(4);
    rightPID.setTimeStep(4);
}
