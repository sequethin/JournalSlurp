package com.beyondnormal.journalslurp;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;


@RunWith(PowerMockRunner.class)
@PrepareForTest( { DayOneCliBinaryValidator.class } )
public class DayOneCliBinaryValidatorTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void attemptToSetEmptyPath() throws DayOneCliBinaryValidatorInvalidBinaryException {
        thrown.expect(DayOneCliBinaryValidatorInvalidBinaryException.class);
        DayOneCliBinaryValidator validator = new DayOneCliBinaryValidator("");
    }

    @Test
    public void attemptToSetNonExistent() throws Exception {
        // Stub the File classes we use to validate existence and suchlike
        File mockDayOneBinaryFile = mock(File.class);
        whenNew(File.class).withAnyArguments().thenReturn(mockDayOneBinaryFile);
        when(mockDayOneBinaryFile.exists()).thenReturn(false);
        DayOneCliBinaryValidator validator = new DayOneCliBinaryValidator("some path");
        assertFalse(validator.validate());
    }

    @Test
    public void attemptToSetPathToExistingNonFile() throws Exception {
        File mockDayOneBinaryFile = mock(File.class);
        whenNew(File.class).withAnyArguments().thenReturn(mockDayOneBinaryFile);
        when(mockDayOneBinaryFile.exists()).thenReturn(true);
        when(mockDayOneBinaryFile.isFile()).thenReturn(false);
        DayOneCliBinaryValidator validator = new DayOneCliBinaryValidator("some path");
        assertFalse(validator.validate());
    }

    @Test
    public void attemptToSetPathToExistingFileWithNoPermissions() throws Exception {
        File mockDayOneBinaryFile = mock(File.class);
        whenNew(File.class).withAnyArguments().thenReturn(mockDayOneBinaryFile);
        when(mockDayOneBinaryFile.exists()).thenReturn(true);
        when(mockDayOneBinaryFile.isFile()).thenReturn(true);
        when(mockDayOneBinaryFile.canExecute()).thenReturn(false);
        DayOneCliBinaryValidator validator = new DayOneCliBinaryValidator("some path");
        assertFalse(validator.validate());
    }

    @Test
    public void pathIsToABinaryThatIsNotDayOne() throws Exception {
        File mockDayOneBinaryFile = mock(File.class);
        whenNew(File.class).withAnyArguments().thenReturn(mockDayOneBinaryFile);
        when(mockDayOneBinaryFile.exists()).thenReturn(true);
        when(mockDayOneBinaryFile.isFile()).thenReturn(true);
        when(mockDayOneBinaryFile.canExecute()).thenReturn(true);

        StringBuilder commandOutput = new StringBuilder();
        commandOutput.append("The output of a different command");
        SystemCommandExecutor mockExecutor = mock(SystemCommandExecutor.class);
        when(mockExecutor.executeCommand()).thenReturn(0);
        when(mockExecutor.getStandardOutputFromCommand()).thenReturn(commandOutput);

        whenNew(SystemCommandExecutor.class).withAnyArguments().thenReturn(mockExecutor);

        DayOneCliBinaryValidator validator = new DayOneCliBinaryValidator("some path");
        assertFalse(validator.validate());
    }

    @Test
    public void aValidPathToDayOneBinary() throws Exception {
        File mockDayOneBinaryFile = mock(File.class);
        whenNew(File.class).withAnyArguments().thenReturn(mockDayOneBinaryFile);
        when(mockDayOneBinaryFile.exists()).thenReturn(true);
        when(mockDayOneBinaryFile.isFile()).thenReturn(true);
        when(mockDayOneBinaryFile.canExecute()).thenReturn(true);

        StringBuilder commandOutput = new StringBuilder();

        // This is a snippet what the output of dayone -h returns at the time I wrote this
        commandOutput.append("Usage: dayone [-d=<date>] [-s=true|false] [-p=<path>]").append("\n")
                .append("              [-j=<path>] [-h] <command>").append("\n")
                .append("Options:").append("\n")
                .append(" -d, --date=<date>              Creation date of the entry.").append("\n")
                .append(" -s, --starred=true|false       Starred value.").append("\n")
                .append(" -p, --photo-file=<path>        File path to a photo to attach to entry.").append("\n")
                .append(" -j, --journal-file=<path>      Location of Day One Journal file.").append("\n")
                .append(" -h, --help                     Show this information.").append("\n")
                .append("\n")
                .append("Commands:").append("\n")
                .append("   new    Adds a new entry using text from stdin").append("\n");
        SystemCommandExecutor mockExecutor = mock(SystemCommandExecutor.class);
        when(mockExecutor.executeCommand()).thenReturn(0);
        when(mockExecutor.getStandardOutputFromCommand()).thenReturn(commandOutput);

        whenNew(SystemCommandExecutor.class).withAnyArguments().thenReturn(mockExecutor);

        DayOneCliBinaryValidator validator = new DayOneCliBinaryValidator("some path");
        assertTrue(validator.validate());
    }
}
