package com.beyondnormal.journalslurp;

import java.io.*;
import java.util.List;

/**
 * This code is Copyright 2010 Alvin J. Alexander, http://devdaily.com.
 * You are free to adapt and share this work under the terms of the
 * Creative Commons Attribution-ShareAlike 3.0 Unported License;
 * see http://creativecommons.org/licenses/by-sa/3.0/ for more
 * details.
 *
 * Thanks to Alvin for this great post: http://alvinalexander.com/java/java-exec-processbuilder-process-1
 *
 * Changes made by Michael Hernandez:
 *  - change package to my own
 *  - remove sudo-related STDIN code and comments
 *  - change formatting
 *  - see commit history of repo for details
 */
public class SystemCommandExecutor {
    public static final int EXIT_CODE_DEFAULT = -99;

    private List<String> commandInformation;
    private ThreadedStreamHandler inputStreamHandler;
    private ThreadedStreamHandler errorStreamHandler;

    /**
     * You'll need access to the command's STDIN and STDOUT to work with the command * interactively.
     */
    private OutputStream commandStandardOutput;
    private InputStream commandStandardInputStream;
    private InputStream commandStandardErrorStream;

    /**
     * Give the consumer access to these objects as well.
     */
    private ProcessBuilder processBuilder;
    private Process process;

    /**
     * Pass in the system command you want to run as a List of Strings, as shown here:
     *
     * List<String> commands = new ArrayList<String>();
     * commands.add("/sbin/ping");
     * commands.add("-c");
     * commands.add("5");
     * commands.add("www.google.com");
     * SystemCommandExecutor commandExecutor = new SystemCommandExecutor(commands);
     * commandExecutor.executeCommand();
     *
     * @param commandInformation The command you want to run.
     */
    public SystemCommandExecutor(final List<String> commandInformation) {
        if (commandInformation.isEmpty()) {
            throw new IllegalArgumentException("The commandInformation is required.");
        }
        this.commandInformation = commandInformation;
    }

    public int executeCommand() throws IOException, InterruptedException {
        int exitValue = EXIT_CODE_DEFAULT;

        processBuilder = new ProcessBuilder(commandInformation);

        process = processBuilder.start();

        // These need to run as java threads to get the standard output and error from the command.
        // The inputStreamHandler gets a reference to our stdOutput in case we need to write something to it.
        inputStreamHandler = new ThreadedStreamHandler(process.getInputStream(), process.getOutputStream());
        errorStreamHandler = new ThreadedStreamHandler(process.getErrorStream());

        inputStreamHandler.start();
        errorStreamHandler.start();

        exitValue = process.waitFor();

        inputStreamHandler.interrupt();
        errorStreamHandler.interrupt();

        inputStreamHandler.join();
        errorStreamHandler.join();
        return exitValue;
    }

    /**
     * Get the standard output (stdout) from the command you just exec'd.
     */
    public StringBuilder getStandardOutputFromCommand() {
        return inputStreamHandler.getOutputBuffer();
    }

    /**
     * Get the standard error (STDERR) from the command you just exec'd.
     */
    public StringBuilder getStandardErrorFromCommand() {
        return errorStreamHandler.getOutputBuffer();
    }
}
