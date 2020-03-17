package com.mkleo.librarys.livedata;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.mkleo.librarys.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LiveDataActivity extends AppCompatActivity {

    @BindView(R.id.btn)
    Button btn;
    private TimerViewModel mTimerViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_data);
        ButterKnife.bind(this);

        mTimerViewModel = ViewModelProviders.of(this).get(TimerViewModel.class);

        //订阅
        Observer<Long> timerObserver = new Observer<Long>() {
            @Override
            public void onChanged(@Nullable Long aLong) {
                btn.setText("" + aLong);
            }
        };

        mTimerViewModel.getElapsedTime().observe(this, timerObserver);

    }

    public void onClicked(View view) {


    }
}
