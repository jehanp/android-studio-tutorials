package com.bignerdranch.android.geoquiz;

import android.content.Context;
import android.widget.Toast;

class GeoQuizToast{

    private Toast toast;

    public GeoQuizToast(Context context, int message){
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
    }

    public GeoQuizToast(Context context, String message){
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
    }

    public GeoQuizToast(Context context, int message, int gravity){
        this(context, message);
        toast.setGravity(gravity,0,0);
    }

    public GeoQuizToast(Context context, String message, int gravity){
        this(context, message);
        toast.setGravity(gravity,0,0);
    }

    public void Show() throws GeoQuizException{
        if(toast!=null){
            toast.show();
        }else{
            throw new GeoQuizException("GeoQuizToast not instantiated");
        }
    }
}
