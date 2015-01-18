package com.beyondnormal.journalslurp;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * Note: This is probably a horrible idea.
 * These tests absolutely rely on knowledge of the internals of the class,
 * and furthermore I'm stubbing classes that I don't control.
 * However, I wanted to have some unit test coverage to demonstrate the behavior of
 * the class, and I found PowerMockito and wanted to try it out ;)
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest( { SystemCommandExecutor.class } )
public class SystemCommandExecutorTest {
    private final int EXPECTED_EXIT_VALUE = 1;

    private SystemCommandExecutor executor;
    private List<String> echoCommandInformation;
    private StringBuilder mockOutputBuffer;
    private StringBuilder mockErrorBuffer;
    private int mockExitValue;

    @Before
    public void beforeEach() throws Exception {
        // A simple but valid command to execute
        echoCommandInformation = new ArrayList<String>();
        echoCommandInformation.add("echo");
        echoCommandInformation.add("5");

        // The SystemCommandExecutor uses these system classes to get the work done
        // We need to use PowerMockito for certain final system classes
        ProcessBuilder mockProcessBuilder = PowerMockito.mock(ProcessBuilder.class);
        Process mockProcess = Mockito.mock(Process.class);

        when(mockProcessBuilder.start()).thenReturn(mockProcess);
        whenNew(ProcessBuilder.class).withArguments(echoCommandInformation).thenReturn(mockProcessBuilder);

        // Set up the streams and handlers to mimic what happens in the SystemCommandExecutor
        InputStream mockInputStream = PowerMockito.mock(InputStream.class);
        when(mockProcess.getInputStream()).thenReturn(mockInputStream);

        when(mockProcess.waitFor()).thenReturn(EXPECTED_EXIT_VALUE);

        InputStream mockErrorStream = PowerMockito.mock(InputStream.class);
        when(mockProcess.getErrorStream()).thenReturn(mockErrorStream);

        ThreadedStreamHandler mockInputStreamHandler = mock(ThreadedStreamHandler.class);

        mockOutputBuffer = PowerMockito.mock(StringBuilder.class);
        mockErrorBuffer = PowerMockito.mock(StringBuilder.class);

        when(mockInputStreamHandler.getOutputBuffer()).thenReturn(mockOutputBuffer);
        whenNew(ThreadedStreamHandler.class)
                .withArguments(mockInputStream).thenReturn(mockInputStreamHandler);

        ThreadedStreamHandler mockErrorStreamHandler = mock(ThreadedStreamHandler.class);
        when(mockErrorStreamHandler.getOutputBuffer()).thenReturn(mockErrorBuffer);
        whenNew(ThreadedStreamHandler.class)
                .withArguments(mockErrorStream).thenReturn(mockErrorStreamHandler);

        whenNew(ThreadedStreamHandler.class)
                .withArguments(mockInputStream).thenReturn(mockInputStreamHandler);

        when(mockErrorStreamHandler.getOutputBuffer()).thenReturn(mockErrorBuffer);
        whenNew(ThreadedStreamHandler.class)
                .withArguments(mockErrorStream).thenReturn(mockErrorStreamHandler);

        executor = new SystemCommandExecutor(echoCommandInformation);
        mockExitValue = executor.executeCommand();
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void constructorRequiresCommandInformation() {
        thrown.expect(IllegalArgumentException.class);
        List<String> emptyCommandInformation = new ArrayList<String>();
        SystemCommandExecutor executor = new SystemCommandExecutor(emptyCommandInformation);
    }

    @Test
    public void newProcessBuilderIsCreatedWithCommandArguments() throws Exception {
        // Proof that the process builder is created with the arguments we pass to the constructor
        verifyNew(ProcessBuilder.class).withArguments(echoCommandInformation);
    }

    @Test
    public void exitValueComesFromProcess() throws Exception {
        ThreadedStreamHandler mockStreamHandler = mock(ThreadedStreamHandler.class);
        whenNew(ThreadedStreamHandler.class).withAnyArguments().thenReturn(mockStreamHandler);
        assertEquals(EXPECTED_EXIT_VALUE, mockExitValue);
    }

    @Test
    public void standardOutputComesFromInputStream() throws Exception {
        assertEquals(mockOutputBuffer, executor.getStandardOutputFromCommand());
    }

    // TODO demonstrate writing to stdin

    @Test
    public void standardErrorComesFromInputStream() throws Exception {
        assertEquals(mockErrorBuffer, executor.getStandardErrorFromCommand());
    }
}
