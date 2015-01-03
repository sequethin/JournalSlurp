package com.beyondnormal.journalslurp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DayOneCliBinaryValidator {
    // Commands:
    //    new    Adds a new entry using text from stdin
    final static Pattern VALID_COMMAND_REGEX = Pattern.compile(
            ".*Commands:(\\s)+new(\\s)+Adds a new entry using text from stdin.*", Pattern.MULTILINE);

    String pathToDayOne;

    public DayOneCliBinaryValidator (String pathToBinary) throws DayOneCliBinaryValidatorInvalidBinaryException {
       if (pathToBinary.isEmpty()) {
           throw new DayOneCliBinaryValidatorInvalidBinaryException();
       }
        pathToDayOne = pathToBinary;
    }

    public boolean validate() {
        boolean isValid = false;
        File dayOneBinary = new File(pathToDayOne);

        if (dayOneBinary.exists() && dayOneBinary.isFile() && dayOneBinary.canExecute()) {

            List<String> commandString = new ArrayList<String>();
            commandString.add(pathToDayOne);
            commandString.add("-h");

            SystemCommandExecutor executor = new SystemCommandExecutor(commandString);
            try {
                int returnValue = executor.executeCommand();
                Matcher matcher = VALID_COMMAND_REGEX.matcher(executor.getStandardOutputFromCommand().toString());
                if (returnValue == 0 && matcher.find()) {
                    isValid = true;
                }
            } catch (IOException e) {
                // let's just say it's invalid
            } catch (InterruptedException e) {
                // let's just say it's invalid
            }

        }

        return isValid;
    }
}

class DayOneCliBinaryValidatorInvalidBinaryException extends Exception {}
