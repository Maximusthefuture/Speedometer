package com.example.speedometer;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {

    SeekBar seekBar;
    SpeedometerCustomView speedometerCustomView;
    ObjectAnimator progressAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        speedometerCustomView = findViewById(R.id.speedometer_view);
        seekBar = findViewById(R.id.seek_bar);
        seekBar.setMax(speedometerCustomView.MAX_SPEED);

        progressAnimator = ObjectAnimator.ofFloat(speedometerCustomView, "speed", 101, 271);
        progressAnimator.setDuration(2000);
        progressAnimator.setRepeatCount(ValueAnimator.INFINITE);
        progressAnimator.setRepeatMode(ValueAnimator.REVERSE);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speedometerCustomView.setSpeed(progress);
                if (progress >= 100) {
//                    progressAnimator.start();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
