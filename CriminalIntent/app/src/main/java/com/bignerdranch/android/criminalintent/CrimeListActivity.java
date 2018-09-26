package com.bignerdranch.android.criminalintent;

import android.content.Intent;
import android.support.v4.app.Fragment;

public class CrimeListActivity extends SingleFragmentActivity implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks{

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onCrimeSelected(Crime crime, int adapterPosition) {
        if(findViewById(R.id.detail_fragment_container) == null){
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId(), adapterPosition);
            startActivity(intent);
        }else{
            Fragment newDetail = CrimeFragment.newInstance(crime.getId(), adapterPosition);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

    @Override
    public void onCrimeSwipedLeft(int adapterPosition) {
        deleteCrime();
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        CrimeListFragment listFragment = (CrimeListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        listFragment.updateCrimeListUI();
    }

    @Override
    public void onCrimeDeleted(int adpaterPosition) {
        deleteCrime();
    }

    private void deleteCrime() {
        if(findViewById(R.id.detail_fragment_container) == null){
            CrimeFragment fragment = (CrimeFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_container);
            fragment.getActivity().finish();
        }else{
            //udpate list
            CrimeListFragment listFragment = (CrimeListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_container);
            listFragment.updateCrimeListUI();

            Fragment detailFragment = getSupportFragmentManager().findFragmentById(R.id.detail_fragment_container);
            if(detailFragment!=null){
                getSupportFragmentManager().beginTransaction()
                        .remove(detailFragment)
                        .commit();
            }
        }
    }
}
