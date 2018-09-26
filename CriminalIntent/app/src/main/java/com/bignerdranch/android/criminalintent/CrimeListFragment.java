package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import javax.security.auth.callback.Callback;

/**
 * Base class for a crime list fragment.
 */
public class CrimeListFragment extends Fragment{

    private static final String TAG = "CrimeListFragment";
    private static final int REQUEST_CRIME = 1;
    private static final String ADAPTER_POSITION = "adapter_position_crime_list_fragment";
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private boolean mSubtitleVisible;
    private LinearLayout mAddFirstCrimeLinearLayout;
    private ImageButton mAddFirstCrimeButton;
    private int selectedItemAdapterPosition;
    private Callbacks mCallbacks;

    private ItemTouchHelper mItemTouchHelper;

    public interface Callbacks{
        void onCrimeSelected(Crime crime, int adapterPosition);
        void onCrimeSwipedLeft(int adapterPosition);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);    //Receive menu callbacks
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAddFirstCrimeLinearLayout = (LinearLayout) view.findViewById(R.id.add_first_crime_placeholder);

        mAddFirstCrimeButton = (ImageButton)mAddFirstCrimeLinearLayout.findViewById(R.id.add_first_crime_button);
        mAddFirstCrimeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context activity =  (Context)getActivity();
                Crime crime = new Crime();
                CrimeLab.get(activity).addCrime(crime);

                Intent intent = CrimePagerActivity.newIntent(activity, crime.getId(), 1);
                startActivity(intent);
            }
        });

        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP|ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolderSource, @NonNull RecyclerView.ViewHolder viewHolderTarget) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int adapterPosition) {
                Log.d(TAG, "Item swiped");
                Crime crime = ((CrimeHolder) viewHolder).mCrime;
                CrimeLab.get(getActivity()).deleteCrime(crime); //Delete from CrimeLab
                mAdapter.deleteCrime(crime);                    //Delete from adapter
                mCallbacks.onCrimeSwipedLeft(adapterPosition);  //Delete from detail_fragment_container
            }
        });
        mItemTouchHelper.attachToRecyclerView(mCrimeRecyclerView);

        if(savedInstanceState != null){
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        updateCrimeListUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if(mSubtitleVisible){
            subtitleItem.setTitle(R.string.hide_subtitle);
        }else{
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.new_crime:
                int position = mAdapter.getItemCount()+1;
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);

                mCallbacks.onCrimeSelected(crime, position);
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateCrimeListUI(){
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if(mAdapter == null){
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        }else{
            mAdapter.setCrimes(crimes);
            mAdapter.notifyDataSetChanged();
        }

        if(crimes.size()==0){
            mAddFirstCrimeLinearLayout.setVisibility(View.VISIBLE);
        }else{
            mAddFirstCrimeLinearLayout.setVisibility(View.INVISIBLE);
        }
    }

    private void updateSubtitle(){
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, crimeCount, crimeCount);

        if(!mSubtitleVisible){
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
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

        public void setCrimes(List<Crime> crimes){
            mCrimes = crimes;
        }
        public void deleteCrime(Crime crime){
            if(mCrimes.contains(crime)){
                mCrimes.remove(crime);
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
            mCallbacks.onCrimeSelected(mCrime, getAdapterPosition());
        }
    }
}
