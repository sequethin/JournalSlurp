package com.beyondnormal.journalslurp;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest( { DayOneCliWrapper.class } )
public class DayOneCliWrapperTest {
    DayOneCliWrapper wrapper;

    @Before
    public void BeforeEach() {
        wrapper = new DayOneCliWrapper();
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void tryingToGetExitCodeBeforeCommandRunThrows() throws DayOneCliWrapperNoCommandRunException {
        thrown.expect(DayOneCliWrapperNoCommandRunException.class);
        wrapper.getExitCode();
    }

    @Test
    public void attemptToExecuteWithoutSettingPath() throws DayOneCliWrapperNoBinarySetException {
        thrown.expect(DayOneCliWrapperNoBinarySetException.class);
        // Note: call to addEntry without path set
        wrapper.addEntry();
    }

    @Test
    public void wrapperThrowsExceptionIfYouTryToSetAnInvalidPath() throws Exception {
        // Set up stubbing and injection of validator
        DayOneCliBinaryValidator mockValidator = mock(DayOneCliBinaryValidator.class);
        when(mockValidator.validate()).thenReturn(false);
        whenNew(DayOneCliBinaryValidator.class).withAnyArguments().thenReturn(mockValidator);

        thrown.expect(DayOneCliBinaryValidatorInvalidBinaryException.class);
        wrapper.setPathToDayOne("path entered here doesn't matter for this test");
    }

}
