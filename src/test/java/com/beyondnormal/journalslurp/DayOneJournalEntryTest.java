package com.beyondnormal.journalslurp;

import org.junit.Test;

import java.util.Date;

public class DayOneJournalEntryTest {
    @Test
    public void addDate() {
        DayOneJournalEntry entry = new DayOneJournalEntry();
        entry.setEntryDate(new Date());
    }
}
