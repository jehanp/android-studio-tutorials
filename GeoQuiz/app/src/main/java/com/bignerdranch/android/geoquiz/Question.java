package com.bignerdranch.android.geoquiz;

public class Question {
    private int mTextResId;
    private boolean mAnswerTrue;
    private boolean mQuestionAnswered;
    private boolean mCheated;

    public Question(int textResId, boolean answerTrue){
        mTextResId = textResId;
        mAnswerTrue = answerTrue;
        mQuestionAnswered = false;
    }

    public int getTextResId() {
        return mTextResId;
    }

    public boolean isAnswerTrue() {
        return mAnswerTrue;
    }

    public boolean isQuestionAnswered() {
        return mQuestionAnswered;
    }

    public boolean isCheated() {
        return mCheated;
    }

    public void setTextResId(int textResId) {
        mTextResId = textResId;
    }

    public void setAnswerTrue(boolean answerTrue) {
        mAnswerTrue = answerTrue;
    }

    public void setQuestionAnswered(boolean questionAnswered) {
        mQuestionAnswered = questionAnswered;
    }

    public void setCheated(boolean cheated) {
        mCheated = cheated;
    }
}
