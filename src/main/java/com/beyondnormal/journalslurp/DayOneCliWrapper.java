package com.beyondnormal.journalslurp;

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

    public String getPathToDayOne() {
        return pathToDayOne;
    }

    public void runCommand() {
        List<String> commandString = new ArrayList<String>();
    }

    public int getExitCode() throws DayOneCliWrapperNoCommandRunException {
        if (exitCode == EXIT_CODE_NO_COMMAND_RUN) {
            throw new DayOneCliWrapperNoCommandRunException();
        }
        return exitCode;
    }

    public void addEntry() throws DayOneCliWrapperNoBinarySetException {
        if (pathToDayOne == null) {
            throw new DayOneCliWrapperNoBinarySetException();
        }
    }
}

class DayOneCliWrapperNoCommandRunException extends Exception {}
class DayOneCliWrapperNoBinarySetException extends Exception {}
