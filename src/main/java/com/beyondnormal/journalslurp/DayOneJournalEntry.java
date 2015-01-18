package com.beyondnormal.journalslurp;

import java.util.Date;

public class DayOneJournalEntry {
    private Date entryDate;
    private String entryContents;

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    public String getEntryContents() {
        return entryContents;
    }

    public void setEntryContents(String entryContents) {
        this.entryContents = entryContents;
    }
}
