package processbuilder;

import java.io.*;
import java.util.List;

/**
 * This code is Copyright 2010 Alvin J. Alexander, http://devdaily.com.
 * You are free to adapt and share this work under the terms of the
 * Creative Commons Attribution-ShareAlike 3.0 Unported License;
 * see http://creativecommons.org/licenses/by-sa/3.0/ for more
 * details.
 */
public class SystemCommandExecutor
{
    private List<String> commandInformation;
    private String adminPassword;
    private ThreadedStreamHandler inputStreamHandler;
    private ThreadedStreamHandler errorStreamHandler;

    /**
     * You'll need access to the command's STDIN and STDOUT to work with the command
     * interactively.
     * (such as when invoking the 'sudo' command, and it prompts you for a password).
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
     * Note: I've removed the other constructor that was here to support executing
     *       the sudo command. I'll add that back in when I get the sudo command
     *       working to the point where it won't hang when the given password is
     *       wrong.
     *
     * @param commandInformation The command you want to run.
     */
    public SystemCommandExecutor(final List<String> commandInformation)
    {
        if (commandInformation==null) throw new NullPointerException("The commandInformation is required.");
        this.commandInformation = commandInformation;
        this.adminPassword = null;
    }

    /**
     * Use this constructor when you want to run the given command with sudo and a supplied
     * password.
     *
     * @param commandInformation The command you want to run.
     * @param adminPassword The admin or root password for the current system.
     */
    public SystemCommandExecutor(final List<String> commandInformation, final String adminPassword)
    {
        // TODO is this the right exception to throw?
        if (commandInformation==null || adminPassword==null) throw new IllegalStateException("The commandInformation and password are both required.");
        this.commandInformation = commandInformation;
        this.adminPassword = adminPassword;
    }

    public int executeCommand()
            throws IOException, InterruptedException
    {
        int exitValue = -99;

        try
        {
            processBuilder = new ProcessBuilder(commandInformation);
            process = processBuilder.start();

            commandStandardOutput = process.getOutputStream();

            // i'm currently doing these on a separate line here in case i need to set them to null
            // to get the threads to stop.
            // see http://java.sun.com/j2se/1.5.0/docs/guide/misc/threadPrimitiveDeprecation.html
            commandStandardInputStream = process.getInputStream();
            commandStandardErrorStream = process.getErrorStream();

            // these need to run as java threads to get the standard output and error from the command.
            // the inputstream handler gets a reference to our stdOutput in case we need to write
            // something to it, such as with the sudo command
            inputStreamHandler = new ThreadedStreamHandler(commandStandardInputStream, commandStandardOutput, adminPassword);
            errorStreamHandler = new ThreadedStreamHandler(commandStandardErrorStream);

            // TODO the inputStreamHandler has a nasty side-effect of hanging if the given password is wrong; fix it
            inputStreamHandler.start();
            errorStreamHandler.start();

            // TODO a better way to do this?
            exitValue = process.waitFor();

            // TODO a better way to do this?
            inputStreamHandler.interrupt();
            errorStreamHandler.interrupt();
            inputStreamHandler.join();
            errorStreamHandler.join();
        }
        catch (IOException e)
        {
            // TODO deal with this here, or just throw it?
            throw e;
        }
        catch (InterruptedException e)
        {
            // generated by process.waitFor() call
            // TODO deal with this here, or just throw it?
            throw e;
        }
        finally
        {
            return exitValue;
        }
    }

    /**
     * Get the standard output (stdout) from the command you just exec'd.
     */
    public StringBuilder getStandardOutputFromCommand()
    {
        return inputStreamHandler.getOutputBuffer();
    }

    /**
     * Get the standard error (stderr) from the command you just exec'd.
     */
    public StringBuilder getStandardErrorFromCommand()
    {
        return errorStreamHandler.getOutputBuffer();
    }


    public InputStream getCommandStandardInputStream()
    {
        return commandStandardInputStream;
    }

    public OutputStream getCommandStandardOutput()
    {
        return commandStandardOutput;
    }

    public InputStream getCommandStandardErrorStream()
    {
        return commandStandardErrorStream;
    }

    public ProcessBuilder getProcessBuilder()
    {
        return processBuilder;
    }

    public Process getProcess()
    {
        return process;
    }

}








