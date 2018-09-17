package com.bignerdranch.android.geoquiz;

public class GeoQuizException extends Exception {

    /***
     * Constructs an exception.
     * @param message detailed message
     * @param cause cause of exception
     */
    public GeoQuizException(String message, Throwable cause){
        super(message, cause);
    }

    /**
     * Constructs an exception
     * @param message detailed message
     */
    public GeoQuizException(String message){
        super(message);
    }
}
