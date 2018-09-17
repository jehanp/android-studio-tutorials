package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Base class for a crime list fragment.
 */
public class CrimeListFragment extends Fragment{

    private static final String TAG = "CrimeListFragment";
    private static final int REQUEST_CRIME = 1;
    private static final String ADAPTER_POSITION = "adapter_position_crime_list_fragment";

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private int selectedItemAdapterPosition;

    public static Intent newIntent(Context packageContext, int adapterPosition){
        Intent intent = new Intent(packageContext, CrimeActivity.class);
        intent.putExtra(ADAPTER_POSITION, adapterPosition);
        return intent;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateCrimeListUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //If single activity hosting a single crime fragment is used
        //updateCrimeItemUI(selectedItemAdapterPosition);

        //If a ViewPager is used
        updateCrimeListUI();
    }

    private void updateCrimeListUI(){
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if(mAdapter == null){
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        }else{
            mAdapter.notifyDataSetChanged();
        }
    }

    private void updateCrimeItemUI(int position){
        if(position>=0){
            mAdapter.notifyItemChanged(position);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CRIME){
            if(resultCode == Activity.RESULT_OK){
                selectedItemAdapterPosition = data.getIntExtra(ADAPTER_POSITION, -1);
                Log.d(TAG, "selectedItemAdapterPosition: " + selectedItemAdapterPosition);
            }
        }
    }

    /**
     * Adapter class. Creates necessary ViewHolders and binds data from the model layer
     */
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>{
        private static final String TAG = "CrimeAdaptor";

        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes){
            mCrimes = crimes;
        }

        /**
         * Called by RecyclerView when it needs a new ViewHolder to display an item with.
         * Creates a new LayoutInflator and uses it to construct a new CrimeHolder
         * @param parent container
         * @param viewType
         * @return
         */
        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new CrimeHolder(layoutInflater, parent, viewType);
        }

        /**
         *
         * @param crimeHolder
         * @param position
         */
        @Override
        public void onBindViewHolder(@NonNull CrimeHolder crimeHolder, int position) {
            Crime crime = mCrimes.get(position);
            crimeHolder.bind(crime);
    }

        /**
         * Returns the count of the items in the list
         * @return
         */
        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        @Override
        public int getItemViewType(int position) {
            if(!mCrimes.get(position).isSolved() && mCrimes.get(position).isPoliceRequired()){
                return R.layout.list_item_crime_serious;
            }else{
                return R.layout.list_item_crime;
            }
        }
    }

    /**
     * ViewHolder class. Inflates the layout.
     */
    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private static final String TAG = "CrimeHolder";

        private Crime mCrime;
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mSolvedImageView;
        private ImageView mContactPoliceImageView;

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent, int viewType){
            super(inflater.inflate(viewType, parent, false));

            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);

            if(viewType == R.layout.list_item_crime_serious){
                mContactPoliceImageView = (ImageView) itemView.findViewById(R.id.crime_contact_police);
                mContactPoliceImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(
                                getActivity(),
                                "Contacting Police...",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
            }else{
                mSolvedImageView = (ImageView) itemView.findViewById(R.id.crime_solved);
            }
        }

        public void bind(Crime crime){
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.dateToString());
            if(mSolvedImageView!=null){
                mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
            }
            if(mContactPoliceImageView!=null){
                mContactPoliceImageView.setVisibility(crime.isPoliceRequired() ? View.VISIBLE : View.GONE);
            }
        }

        @Override
        public void onClick(View view) {
            //Intent intent = CrimeActivity.newIntent(getActivity(), mCrime.getId(), getAdapterPosition());
            Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId(), getAdapterPosition());
            startActivityForResult(intent, REQUEST_CRIME);
        }
    }
}
