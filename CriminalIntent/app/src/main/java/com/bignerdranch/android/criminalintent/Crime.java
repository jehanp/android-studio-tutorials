package com.bignerdranch.android.criminalintent;

import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

public class Crime {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private boolean mPoliceRequired;

    public Crime(){
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public Date getDate() {
        return mDate;
    }

    public boolean isPoliceRequired() {
        return mPoliceRequired;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public void setPoliceRequired(boolean policeRequired) {
        mPoliceRequired = policeRequired;
    }

    public String dateToString(){
        return DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(mDate) + " " + DateFormat.getDateInstance().format(mDate);
    }
}
