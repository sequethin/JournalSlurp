package com.beyondnormal.journalslurp;

import java.io.IOException;

public class DayOneCliWrapper {
    public static final int EXIT_CODE_NO_COMMAND_RUN = 9999;

    private int exitCode = EXIT_CODE_NO_COMMAND_RUN;
    private String pathToDayOne;

    public void setPathToDayOne(String pathToDayOne) {
        this.pathToDayOne = pathToDayOne;
    }

    public String getPathToDayOne() {
        return pathToDayOne;
    }

    public void runCommand() {
        try {
            // TODO: this is bad news, we should just return exit value
            Process p = Runtime.getRuntime().exec("echo 'foo'");
            exitCode = p.exitValue();
        } catch (IOException e) {
            // TODO: should we be catching any exceptions?
            e.printStackTrace();
        }
    }

    public int getExitCode() throws DayOneCliWrapperNoCommandRunException {
        if (exitCode == EXIT_CODE_NO_COMMAND_RUN) {
            throw new DayOneCliWrapperNoCommandRunException();
        }
        return exitCode;
    }
}

class DayOneCliWrapperNoCommandRunException extends Exception {}
