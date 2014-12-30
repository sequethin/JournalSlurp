package com.beyondnormal.journalslurp;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

@RunWith(PowerMockRunner.class)
@PrepareForTest( { DayOneCliWrapper.class } )
public class DayOneCliWrapperTest {
    DayOneCliWrapper wrapper;

    @Before
    public void BeforeEach() {
        wrapper = new DayOneCliWrapper();
    }

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Test
    public void tryingToGetExitCodeBeforeCommandRunThrows() throws DayOneCliWrapperNoCommandRunException {
        thrown.expect(DayOneCliWrapperNoCommandRunException.class);
        wrapper.getExitCode();
    }

    @Test
    public void expectedExitValueComesFromProcess() throws IOException, DayOneCliWrapperNoCommandRunException {
        // This sucks because it's assuming knowledge of how we execute the commands
        // Luckily, we do have knowledge of how we execute the commands ;)
        int expectedResult = 0;
        mockStatic(Runtime.class);
        Runtime mockRuntime = mock(Runtime.class);
        Process mockProcess = mock(Process.class);
        when(mockProcess.exitValue()).thenReturn(expectedResult);
        when(mockRuntime.exec("echo 'foo'")).thenReturn(mockProcess);
        Mockito.when(Runtime.getRuntime()).thenReturn(mockRuntime);

        wrapper.runCommand();
        assertEquals(expectedResult, wrapper.getExitCode());
    }

    @Test
    public void setPathToDayOneCliBinary() {
        String expectedPath = "/usr/local/bin/dayone";
        wrapper.setPathToDayOne(expectedPath);
        assertEquals(expectedPath, wrapper.getPathToDayOne());
    }
}
