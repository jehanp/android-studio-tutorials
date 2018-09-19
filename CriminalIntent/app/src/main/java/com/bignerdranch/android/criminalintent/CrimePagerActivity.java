package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity {

    private static final String TAG = "CrimePagerActivity";

    private static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id";
    private static final String ADAPTER_POSITION = "com.bignerdranch.android.criminalintent.adapter_position";

    private ViewPager mViewPager;
    private List<Crime> mCrimes;

    private Button mJumpToFirst;
    private Button mJumpToLast;

    public static Intent newIntent(Context packageContext, UUID crimeId, int adapterPosition){
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        intent.putExtra(ADAPTER_POSITION, adapterPosition);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        mCrimes = CrimeLab.get(this).getCrimes();

        mViewPager = (ViewPager) findViewById(R.id.crime_view_pager);
        mJumpToFirst = (Button) findViewById(R.id.jump_to_first);
        mJumpToFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(0);
            }
        });

        mJumpToLast = (Button) findViewById(R.id.jump_to_last);
        mJumpToLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(mCrimes.size() - 1);
            }
        });

        if(mCrimes.size()<=2){
            mJumpToFirst.setVisibility(View.INVISIBLE);
            mJumpToLast.setVisibility(View.INVISIBLE);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                setButtonVisibility();
                return CrimeFragment.newInstance(crime.getId(), position);
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                Log.d(TAG,"isViewFromObject(View, Object) called ");
                boolean isViewFromCurrentObject = super.isViewFromObject(view, object);
                if(isViewFromCurrentObject){
                    setButtonVisibility();
                }
                return isViewFromCurrentObject;
            }
        });

        if(getIntent().getSerializableExtra(ADAPTER_POSITION) != null){
            int adapterPosition = (int) getIntent().getSerializableExtra(ADAPTER_POSITION);
            mViewPager.setCurrentItem(adapterPosition);
        }
    }

    private void setButtonVisibility(){
        Log.d(TAG, "setButtonVisibility() called");
        if(mViewPager.getCurrentItem()==0){
            mJumpToFirst.setEnabled(false);
            mJumpToLast.setEnabled(true);
        }else if(mViewPager.getCurrentItem()==mCrimes.size()-1){
            mJumpToFirst.setEnabled(true);
            mJumpToLast.setEnabled(false);
        }else{
            mJumpToFirst.setEnabled(true);
            mJumpToLast.setEnabled(true);
        }
    }
}
