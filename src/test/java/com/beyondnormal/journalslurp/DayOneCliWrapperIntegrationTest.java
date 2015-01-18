package com.beyondnormal.journalslurp;

import org.junit.Test;

import java.io.IOException;

public class DayOneCliWrapperIntegrationTest {
    @Test
    public void AddJournalEntry() throws DayOneCliBinaryValidatorInvalidBinaryException, InterruptedException, DayOneCliWrapperNoBinarySetException, IOException {
        DayOneCliWrapper wrapper = new DayOneCliWrapper();
        wrapper.setPathToDayOne("/usr/local/bin/dayone");
        StringBuilder entryText = new StringBuilder();
        entryText.append("I ADDED THIS WITH JAVA");
        // TODO: create a test runner in the IDE or something to avoid having to comment this out
        //wrapper.addEntry(entryText);
    }
}
