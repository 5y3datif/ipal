/**
 * @file decider.ino
 * @brief Decider of things.
 * @details Contains code pertaining to all decision flow for Chotu's locomotion and instruction handling. 
 * */

/**
 * @brief Handles all decision matters 
 * 
 * @details
 * @li Updates stepsOverall which is the count of steps taken on average by the encoders from the time the robot was started until now
 * @li Breaks the inputString into its constituent commands
 * @li Interprets what the command means and who is to execute it
 * @li If command pertains to locomotion or servo motion, Arduino handles its
 * @li If command pertains to anything else, it is sent as is to the tablet
 * @li When decider() is called, it nullifies any previous command.
 * 
 * @return void
 * */
void decider() {

    //stepsMotion stores the steps taken until now (also discards accidental movement readings) //
    stepsMotion = stepsOverall;

    //Holds the command character. For locomotion commands this is the direction to move in.//
    char Direction;                                              

    //Holds the locomotion parameter which is in units pertaining to movement.//
    int LocomotionParameter;                                     

    //Hold angles for the Yaw and Pitch motors.//
    int YawAngle;                                                
    int PitchAngle;                           

    //Holds the status of the PIR sensor?//                   
    char PIRstatus;                      

    //eg. "F"_0200_090_045 //                        
    Direction = inputString.charAt(0);                         

    //eg. F_"0200"_090_045 //  
    LocomotionParameter = (inputString.substring(2, 6)).toInt(); 

    //eg. F_0200_"090_045 //
    YawAngle = (inputString.substring(7, 10)).toInt();           

    //eg. F_0200_090_"045" //
    PitchAngle = (inputString.substring(11, 14)).toInt(); 

    // Stores the input String for a small check later //
    String inputStringStored = inputString;

    //Empties the inputString //
    inputString = "";

    //movehead(YawAngle, PitchAngle); //commented as no servos currently present in Chotu body

    switch (Direction) {
        case 'F':
            moveForward(LocomotionParameter);
            break;
        case 'B':
            moveBackward(LocomotionParameter);
            break;
        case 'R':
            turnRight(LocomotionParameter);
            break;
        case 'L':
            turnLeft(LocomotionParameter);
            break;
        case 'E':
        case 'G':
        case 'C':
        //Letters that are not locomotion related. Other characters will stop the robot motion. This is a safety feature.//
            if (inputStringStored == "#") /*checks that it isn't sending an empty command*/ {
            } else
                Serial1.print(inputStringStored);
            stopMotion();
            //commented for now as no PIR sensor present in body
            //    case 'P':
            //      pirSensor();
            //      PIRstatus = pirReturn ();
            //      datatosend = "P_";
            //      datatosend += PIRstatus;
            //      datatosend += "_000_000#";
            //      Serial1.println(datatosend);
            //      break;
        default:
            stopMotion();
    }
}

/**
 * @brief     The robot turns left.
 * @param     steps Motion parameter
 * @return void
 * */
void turnLeft(double steps) {
    moveDirection(Left, steps);
}

/**
 * @brief     The robot turns right.
 * @param     steps Motion parameter
 * @return void
 * */void turnRight(double steps) {
    moveDirection(Right, steps);
}

/**
 * @brief     The robot begins to move forward.
 * @param     steps Motion parameter
 * @return void
 * */void moveForward(double steps) {
    moveDirection(Front, steps);
}

/**
 * @brief     The robot begins to move backwards
 * @param     steps Motion parameter
 * @return void
 * */void moveBackward(double steps) {
    moveDirection(Rear, steps);
}
