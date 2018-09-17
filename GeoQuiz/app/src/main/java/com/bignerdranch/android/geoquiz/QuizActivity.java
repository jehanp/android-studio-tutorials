package com.bignerdranch.android.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private static final String KEY_CHEATED_INDEXES = "key_cheated_indexes";
    private static final String KEY_ANSWERED_INDEXES = "key_answered_indexes";
    private static final String KEY_CURRENT_INDEX = "key_current_index";
    private static final String KEY_SCORE = "key_score";
    private static final String KEY_CHEAT_TOKENS = "key_cheat_tokens";
    private static final int REQUEST_CODE_CHEAT = 0;
    private static final int MAX_CHEAT_TOKENS = 3;

    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mPreviousButton;
    private ImageButton mNextButton;
    private TextView mQuestionTextView;
    private TextView mScoreTextView;
    private Button mCheatButton;
    private TextView mCheatTokensTextView;

    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true)
    };
    private ArrayList mCheatedQuestionIndexes = new ArrayList();
    private ArrayList mAnsweredQuestionIndexes = new ArrayList();

    private int mCurrentIndex;
    private int mAnsweredQuestions;
    private int mScore;
    private static int mCheatTokens = MAX_CHEAT_TOKENS;

    public static int getCheatTokens() {
        return mCheatTokens;
    }

    public static void setCheatTokens(int cheatTokens) {
        mCheatTokens = cheatTokens;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        if(savedInstanceState!=null){
            if(savedInstanceState.containsKey(KEY_CURRENT_INDEX)){
                Log.d(TAG,"Contains current index");
                mCurrentIndex = savedInstanceState.getInt(KEY_CURRENT_INDEX);
            }

            if(savedInstanceState.containsKey(KEY_SCORE)){
                Log.d(TAG, "Contains score");
                mScore = savedInstanceState.getInt(KEY_SCORE);
            }

            if(savedInstanceState.containsKey(KEY_CHEATED_INDEXES)){
                Log.d(TAG, "Contains cheated indexes");
                 mCheatedQuestionIndexes = savedInstanceState.getIntegerArrayList(KEY_CHEATED_INDEXES);
                 for(int i=0; i<mCheatedQuestionIndexes.size(); i++){
                     mQuestionBank[(int)mCheatedQuestionIndexes.get(i)].setCheated(true);
                 }
            }

            if(savedInstanceState.containsKey(KEY_ANSWERED_INDEXES)){
                Log.d(TAG, "Contains answered indexes");
                mAnsweredQuestionIndexes = savedInstanceState.getIntegerArrayList(KEY_ANSWERED_INDEXES);
                for(int i=0; i<mAnsweredQuestionIndexes.size(); i++){
                    mQuestionBank[(int)mAnsweredQuestionIndexes.get(i)].setQuestionAnswered(true);
                }
            }

            if(savedInstanceState.containsKey(KEY_CHEAT_TOKENS)){
                Log.d(TAG, "Contains cheat tokens");
                mCheatTokens = savedInstanceState.getInt(KEY_CHEAT_TOKENS);
            }
        }

        initViewObjects();

        updateQuestion();



    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG, "onStart() called");
        updateScore();
        updateCheatTokens();
        setCheatButtonVisibility();
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState " + mCurrentIndex);

        //Find cheated indexes and answered indexes
        int index = 0;
        for(int i=0; i<mQuestionBank.length; i++){
            if(mQuestionBank[i].isCheated()){
                mCheatedQuestionIndexes.add(i);
            }else if(mQuestionBank[i].isQuestionAnswered()){
                mAnsweredQuestionIndexes.add(i);
            }
        }

        savedInstanceState.putInt(KEY_CURRENT_INDEX, mCurrentIndex);
        savedInstanceState.putInt(KEY_SCORE, mScore);
        savedInstanceState.putIntegerArrayList(KEY_CHEATED_INDEXES, mCheatedQuestionIndexes);
        savedInstanceState.putIntegerArrayList(KEY_ANSWERED_INDEXES, mAnsweredQuestionIndexes);
        savedInstanceState.putInt(KEY_CHEAT_TOKENS, mCheatTokens);
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d(TAG, "onActivityResult() called");
        if(resultCode!= Activity.RESULT_OK){
            return;
        }

        if(requestCode == REQUEST_CODE_CHEAT){
            if(data==null){
                return;
            }
            userCheated(CheatActivity.wasAnswerShown(data));
        }
    }

    private void initViewObjects(){

        mScoreTextView = (TextView) findViewById(R.id.score);

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                try{
                    nextQuestion();
                }catch (GeoQuizException e){
                    Log.e(TAG,"Error proceeding to next question", e);
                }
            }
        });


        mPreviousButton = (ImageButton) findViewById(R.id.previous_button);
        mPreviousButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                try{
                    previousQuestion();
                }catch (GeoQuizException e){
                    Log.e(TAG, "Error proceeding to previous question");
                }
            }
        });

        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                try{
                    nextQuestion();
                }catch (GeoQuizException e){
                    Log.e(TAG, "Error proceeding to next question", e);
                }
            }
        });

        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)  {
                checkAnswer(true);
            }
        });

        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                checkAnswer(false);
            }
        });

        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Start cheat activity
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });

        mCheatTokensTextView = (TextView) findViewById(R.id.cheat_tokens);
        mCheatTokensTextView.setText(String.valueOf(mCheatTokens));
    }

    private void updateQuestion(){
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
        setCurrentAnswerButtonsVisibiilty();
    }

    private void setCurrentAnswerButtonsVisibiilty(){
        if(mQuestionBank[mCurrentIndex].isQuestionAnswered()){
            mTrueButton.setEnabled(false);
            mFalseButton.setEnabled(false);
        }else{
            mTrueButton.setEnabled(true);
            mFalseButton.setEnabled(true);
        }
    }

    private void setCheatButtonVisibility(){
        if(mCheatTokens>0){
            mCheatButton.setEnabled(true);
        }else{
            mCheatButton.setEnabled(false);
        }
    }

    private void nextQuestion() throws GeoQuizException{
        try{
            mCurrentIndex = (mCurrentIndex + 1)% mQuestionBank.length;
        }catch (NullPointerException e){
            throw new GeoQuizException("QuestionBank null", e);
        }finally {
            updateQuestion();
        }
    }

    private void previousQuestion() throws GeoQuizException{
        try{
            mCurrentIndex = mCurrentIndex==0 ? mQuestionBank.length-1 : mCurrentIndex-1;

        }catch (NullPointerException e){
            throw new GeoQuizException("QuestionBank array null", e);
        }catch (ArrayIndexOutOfBoundsException e){
            throw new GeoQuizException("QuestionBank array index out of bounds", e);
        }
        finally {
            updateQuestion();
        }
    }

    private void updateScore(){
        mScoreTextView.setText(String.valueOf(mScore));
    }

    private void updateCheatTokens(){
        mCheatTokensTextView.setText(String.valueOf(mCheatTokens));
    }

    private void checkAnswer(boolean userPressedTrue){
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId;

        if(mQuestionBank[mCurrentIndex].isCheated()){
            messageResId = R.string.judgement_toast;
        }else{
            if(userPressedTrue==answerIsTrue){
                messageResId = R.string.correct_toast;
                mScore++;
            }else{
                messageResId = R.string.incorrect_toast;
            }
        }

        try{
            GeoQuizToast toast = new GeoQuizToast( QuizActivity.this, messageResId);
            toast.Show();
            mQuestionBank[mCurrentIndex].setQuestionAnswered(true);
            mAnsweredQuestions++;
        }catch (GeoQuizException e){
            Log.e(TAG, "Error checking answer", e);
        }catch (NullPointerException e){
            Log.e(TAG, "Incorrect QuestionBank index",e);
        }

        updateScore();
        setCurrentAnswerButtonsVisibiilty();

        if(mAnsweredQuestions==mQuestionBank.length){
            try{
                GeoQuizToast toast = new GeoQuizToast(QuizActivity.this, "Quiz Complete!\nScore: " + String.valueOf((mScore*100/mQuestionBank.length)) + "%", Gravity.CENTER);
                toast.Show();
            }catch (GeoQuizException e){
                Log.e(TAG, "Error displaying Quiz Complete toast",e);
            }
        }
    }

    private void userCheated(boolean cheated){
        mQuestionBank[mCurrentIndex].setCheated(cheated);
    }
}
