package com.bignerdranch.android.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFragment extends Fragment {

    public static final String EXTRA_DATE = "com.bignerdranch.android.criminalintent.date";

    private static final String ARG_DATE_YEAR = "date_year";
    private static final String ARG_DATE_MONTH = "date_month";
    private static final String ARG_DATE_DAY = "date_day";

    private static Calendar mCalendar;

    private DatePicker mDatePicker;
    private Button mOkayButton;

    public static DatePickerFragment newInstance(Date date){
        Bundle args = new Bundle();
        mCalendar = Calendar.getInstance();
        mCalendar.setTime(date);
        args.putSerializable(ARG_DATE_YEAR, mCalendar.get(Calendar.YEAR));
        args.putSerializable(ARG_DATE_MONTH, mCalendar.get(Calendar.MONTH));
        args.putSerializable(ARG_DATE_DAY, mCalendar.get(Calendar.DAY_OF_MONTH));

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        int year = (int) getArguments().getSerializable(ARG_DATE_YEAR);
        int month = (int) getArguments().getSerializable(ARG_DATE_MONTH);
        int day = (int) getArguments().getSerializable(ARG_DATE_DAY);

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_date, null);

        mDatePicker = (DatePicker) v.findViewById(R.id.dialog_date_picker);
        mDatePicker.init(year, month, day, null);

        mOkayButton = (Button) v.findViewById(R.id.okay_button);
        mOkayButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Date date= new GregorianCalendar(
                        mDatePicker.getYear(),
                        mDatePicker.getMonth(),
                        mDatePicker.getDayOfMonth(),
                        mCalendar.get(Calendar.HOUR_OF_DAY),
                        mCalendar.get(Calendar.MINUTE)
                ).getTime();
                sendResult(getActivity().RESULT_OK, date);

                getActivity().finish();
            }
        });

        return v;
    }

    private void sendResult(int resultCode, Date date){
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);

        getActivity().setResult(resultCode, intent);
    }
}
