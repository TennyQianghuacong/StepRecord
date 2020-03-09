package com.qiang.step;

import android.Manifest;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.Scheduler;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author qhc
 * @time 2020/3/9
 */
public class MainActivity extends AppCompatActivity {

    private TextView mStepTV;
    private SensorManager mSensorManager;
    private MySensorEventListener mListener;
    private int mStepDetector = 0;  // 自应用运行以来STEP_DETECTOR检测到的步数
    private int mStepCounter = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mStepTV = (TextView)findViewById(R.id.tv_step);

        if (Build.VERSION.SDK_INT <= 29) {
            initSensor();
        }else {
            new RxPermissions(this)
                    .request(Manifest.permission.ACTIVITY_RECOGNITION)
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            if (aBoolean) {
                                initSensor();
                            }
                        }
                    });
        }




    }

    private void initSensor(){
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mListener = new MySensorEventListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSensorManager != null){
            mSensorManager.registerListener(mListener, mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR),
                    SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(mListener, mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSensorManager != null){
            mSensorManager.unregisterListener(mListener);
        }
    }

    class MySensorEventListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {
            System.out.println("@@@:"+event.sensor.getType()+"--"+Sensor.TYPE_STEP_DETECTOR+"--"+Sensor.TYPE_STEP_COUNTER);
            if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
                if (event.values[0] == 1.0f) {
                    mStepDetector++;
                }
            } else if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                mStepCounter = (int) event.values[0];
            }

            String desc = String.format("设备检测到您当前走了%d步，自开机以来总数为%d步", mStepDetector, mStepCounter);
            mStepTV.setText(desc);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }

}
