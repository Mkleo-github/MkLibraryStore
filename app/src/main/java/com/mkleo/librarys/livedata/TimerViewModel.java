package com.mkleo.librarys.livedata;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.SystemClock;

import java.util.Timer;
import java.util.TimerTask;

public class TimerViewModel extends ViewModel {

    private static final int ONE_SECOND = 1000;

    private MutableLiveData<Long> mElapsedTime = new MutableLiveData<>();

    private long mInitialTime;

    public TimerViewModel() {
        mInitialTime = SystemClock.elapsedRealtime();
        Timer timer = new Timer();

        // Update the elapsed time every second.
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                final long newValue = (SystemClock.elapsedRealtime() - mInitialTime) / 1000;
                // setValue() cannot be called from a background thread so post to main thread.
                mElapsedTime.postValue(newValue);
            }
        }, ONE_SECOND, ONE_SECOND);

    }


    public LiveData<Long> getElapsedTime() {
        return mElapsedTime;
    }


}
