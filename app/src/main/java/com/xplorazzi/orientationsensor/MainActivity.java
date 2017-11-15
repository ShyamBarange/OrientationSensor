package com.xplorazzi.orientationsensor;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    TextView Pitch_data, Roll_data, Status_data;
    SensorManager sManager;
    SensorEventListener sListener;

    RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Pitch_data = findViewById(R.id.tv_pitch);
        Roll_data = findViewById(R.id.tv_roll);
        Status_data = findViewById(R.id.tv_status);

        layout = findViewById(R.id.layout);


        sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }


    //when this Activity starts
    @Override
    protected void onResume() {
        super.onResume();
    /*register the sensor listener to listen to the gyroscope sensor, use the
    callbacks defined in this class, and gather the sensor information as quick
    as possible*/

        sManager.registerListener((SensorEventListener) this, sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        sManager.registerListener((SensorEventListener) this, sManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);
    }


    float Rot[] = null; //for gravity rotational data
    //don't use R because android uses that for other stuff
    float I[] = null; //for magnetic rotational data
    float accels[] = new float[3];
    float mags[] = new float[3];
    float[] values = new float[3];

    float azimuth;
    float pitch;
    float roll;

    @Override
    public void onSensorChanged(SensorEvent event) {
        //below commented code - junk - unreliable is never populated
        //if sensor is unreliable, return void
        //if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
        //{
        //    return;
        //}

        switch (event.sensor.getType()) {
            case Sensor.TYPE_MAGNETIC_FIELD:
                mags = event.values.clone();
                break;
            case Sensor.TYPE_ACCELEROMETER:
                accels = event.values.clone();
                break;
        }

        if (mags != null && accels != null) {
            Rot = new float[9];
            I = new float[9];
            SensorManager.getRotationMatrix(Rot, I, accels, mags);
            // Correct if screen is in Landscape

            float[] outR = new float[9];
            SensorManager.remapCoordinateSystem(Rot, SensorManager.AXIS_X, SensorManager.AXIS_Z, outR);
            SensorManager.getOrientation(outR, values);

            azimuth = values[0] * 57.2957795f; //looks like we don't need this one
            pitch = values[1] * 57.2957795f;
            roll = values[2] * 57.2957795f;


            Pitch_data.setText(Float.toString(pitch));
            Roll_data.setText(Float.toString(roll));

            mags = null; //retrigger the loop when things are repopulated
            accels = null; ////retrigger the loop when things are repopulated
            if ((pitch >= -5 && pitch <= 5) && (roll >= -5 && roll <= 5)) {
//                    layout.setBackgroundColor(222);
//                    Toast.makeText(this, "Phone is now Vertical...", Toast.LENGTH_SHORT).show();
                Status_data.setText("Status: " + " Vertical");
                layout.setBackgroundColor(Color.GREEN);


            } else {
                Status_data.setText("Status: " + " Not Vertical");
                layout.setBackgroundColor(Color.WHITE);
            }

        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}