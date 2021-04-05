/**
 * @file serialEvent.ino
 * 
 * @brief Handles serial communication.
 * @details Handles 3 serial communication trigger events. They get called whenever there is activity on any channel.
 * */


/**
 * @brief  Gets called when there is an event on serial channel 2
 * @details 
 * @li runs until there is a communication line established
 * @li stores incoming data into a character inChar2
 * @li appends inChar2 to inputString
 * @li if the terminator '#' is received, the command is considered complete and decider() is called
 * @li a confirmation of reception is given by sending the received command back to the sender
 * @return void
 * */
void serialEvent2() {                                         
  while (Serial2.available()) {
    char inChar2 = (char)Serial2.read();
    inputString += inChar2;                     
    if (inChar2 == '#') {
      //Serial2.println(inputString);
      Serial2.println(inputString);
      decider();
    }
  }
}

/**
 * @brief  Gets called when there is an event on serial channel 0
 * @details 
 * @li runs until there is a communication line established
 * @li stores incoming data into a character inChar
 * @li appends inChar to inputString
 * @li if the terminator '#' is received, the command is considered complete and decider() is called
 * @li a confirmation of reception is given by sending the received command back to the sender
 * @return void
 * */
void serialEvent() {
  while (Serial.available()) {
    char inChar = (char)Serial.read();
    inputString += inChar;                     
    if (inChar == '#') {
      //Serial2.println(inputString);
      Serial.println(inputString);
      decider();
    }
  }
}
