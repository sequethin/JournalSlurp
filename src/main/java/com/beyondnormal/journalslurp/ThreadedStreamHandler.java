package com.beyondnormal.journalslurp;

import java.io.*;

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
class ThreadedStreamHandler extends Thread {
    InputStream inputStream;
    OutputStream outputStream;
    PrintWriter printWriter;
    StringBuilder outputBuffer = new StringBuilder();

    /**
     * This constructor will just run the command you provide
     *
     * @param inputStream an InputStream for working with STDIN / STDOUT
     */
    ThreadedStreamHandler(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * Use this constructor when you want to provide input for STDIN
     * The outputStream must not be null. If it is, you'll regret it. :)
     *
     * @param inputStream
     * @param outputStream
     */
    ThreadedStreamHandler(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        // To write to STDIN use write to the outputStream during run
        // e.g. printWriter.println("Some Input"); printWriter.flush();
        this.printWriter = new PrintWriter(outputStream);
    }

    public void run() {
        // To write to STDIN use write to the outputStream during run
        // e.g. printWriter.println("Some Input"); printWriter.flush();

        // TODO: we need to make sure that if there was input supplied for STDIN that we add it before the rest of run()
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                outputBuffer.append(line).append("\n");
            }
        } catch (IOException ioe) {
            // TODO handle this better
            ioe.printStackTrace();
        } catch (Throwable t) {
            // TODO handle this better
            t.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                // ignore this one
            }
        }
    }

    // TODO if we don't plan on sleeping, we don't need this at all
    private void doSleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    public StringBuilder getOutputBuffer() {
        return outputBuffer;
    }
}
