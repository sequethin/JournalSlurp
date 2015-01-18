package com.beyondnormal.journalslurp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DayOneCliWrapper {
    public static final int EXIT_CODE_NO_COMMAND_RUN = 9999;

    private int exitCode = EXIT_CODE_NO_COMMAND_RUN;
    private String pathToDayOne;

    public void setPathToDayOne(String pathToDayOne) throws DayOneCliBinaryValidatorInvalidBinaryException {
        if (!validateDayOneBinary(pathToDayOne)) {
            throw new DayOneCliBinaryValidatorInvalidBinaryException();
        }

        this.pathToDayOne = pathToDayOne;
    }

    private boolean validateDayOneBinary(String pathToDayOne) throws DayOneCliBinaryValidatorInvalidBinaryException {
        DayOneCliBinaryValidator validator = new DayOneCliBinaryValidator(pathToDayOne);
        return validator.validate();
    }

    public int getExitCode() throws DayOneCliWrapperNoCommandRunException {
        if (exitCode == EXIT_CODE_NO_COMMAND_RUN) {
            throw new DayOneCliWrapperNoCommandRunException();
        }
        return exitCode;
    }

    public void addEntry(StringBuilder entryText) throws DayOneCliWrapperNoBinarySetException, IOException, InterruptedException {
        if (pathToDayOne == null) {
            throw new DayOneCliWrapperNoBinarySetException();
        }

        List<String> commandString = new ArrayList<String>();
        commandString.add(pathToDayOne);
        commandString.add("-d=\"Today 5:30PM\" ");
        commandString.add("new");
        // TODO use real date from entry
        // TODO make a dayone entry class that has a date + text (maybe a photo one day)

        SystemCommandExecutor executor = new SystemCommandExecutor(commandString);

        executor.executeCommand(entryText);
        System.out.println(executor.getStandardOutputFromCommand());
    }
}

class DayOneCliWrapperNoCommandRunException extends Exception {}
class DayOneCliWrapperNoBinarySetException extends Exception {}
