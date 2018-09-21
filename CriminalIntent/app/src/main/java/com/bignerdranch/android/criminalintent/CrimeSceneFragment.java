package com.bignerdranch.android.criminalintent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

public class CrimeSceneFragment extends DialogFragment {

    private static final String PHOTO_PATH = "photo_path";

    private ImageView mCrimeScene;

    public static CrimeSceneFragment newInstance(String path){
        Bundle args = new Bundle();
        args.putSerializable(PHOTO_PATH, path);

        CrimeSceneFragment fragment = new CrimeSceneFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_crime_scene, null);

        String path = (String)getArguments().getSerializable(PHOTO_PATH);

        mCrimeScene = (ImageView) v.findViewById(R.id.crime_scene_zoom);

        Bitmap bitmap = PictureUtils.getScaledBitmap(path, getActivity());
        mCrimeScene.setImageBitmap(bitmap);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.crime_scene_title)
                .create();
    }
}
