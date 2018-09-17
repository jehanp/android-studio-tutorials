package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

public class CrimeActivity extends SingleFragmentActivity {

    private static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id";
    public static final String ADAPTER_POSITION = "com.bignerdranch.android.criminalintent.adapter_position";

    public static Intent newIntent(Context packageContext, UUID crimeId, int adapterPosition){
        Intent intent = new Intent(packageContext, CrimeActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        intent.putExtra(ADAPTER_POSITION, adapterPosition);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        int position = (int) getIntent().getIntExtra(ADAPTER_POSITION, -1);
        return CrimeFragment.newInstance(crimeId, position);
    }
}
