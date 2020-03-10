package com.qiang.step

import android.Manifest
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var mStepCounter: Int = 0

    private lateinit var mSensorManager: SensorManager
    private lateinit var mSensorListener: SensorEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            initSensor()
        } else {
            RxPermissions(this)
                .request(Manifest.permission.ACTIVITY_RECOGNITION)
                .subscribeOn(Schedulers.io())
                .subscribe(Consumer { aBoolean ->
                    if (aBoolean) {
                        initSensor()
                    }
                }, Consumer { throwable ->
                    throwable.printStackTrace()
                })
        }
    }

    private fun initSensor() {
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mSensorListener = getSensorEventListener();
    }

    override fun onResume() {
        super.onResume()
        mSensorManager?.let {
            it.registerListener(
                mSensorListener, it.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR),
                SensorManager.SENSOR_DELAY_NORMAL
            );
            it.registerListener(
                mSensorListener, it.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
                SensorManager.SENSOR_DELAY_NORMAL
            );
        }
    }

    override fun onPause() {
        super.onPause()
        mSensorManager?.let {
            it.unregisterListener(mSensorListener);
        }
    }

    private fun getSensorEventListener(): SensorEventListener {
        return object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            }

            override fun onSensorChanged(event: SensorEvent?) {
                if (event!!.sensor.type === Sensor.TYPE_STEP_COUNTER) {
                    mStepCounter = event!!.values[0].toInt()
                }
                tv_step.text = "设备检测到您当前走了$mStepCounter 步"

            }

        }
    }

}