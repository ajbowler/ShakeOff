package com.example.zplata.shakeoff;

import android.graphics.Color;
import android.hardware.SensorEventListener;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.app.Activity;
import java.util.Random;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class ShakeOff extends Activity implements SensorEventListener {

    private TextView centerCount;
    private RelativeLayout rLayout;
    private SensorManager sensorMgr;
    private Sensor mAccel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake_off);

        // Vars
        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        centerCount = (TextView) findViewById(R.id.centerCount);
        mAccel = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        rLayout = (RelativeLayout) findViewById(R.id.rLayout);

        rLayout.setOnClickListener(rLayoutClickListener);



    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorMgr.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() ==  Sensor.TYPE_ACCELEROMETER) {
            float value[] = event.values;
            float x = value[0];
            float y = value[1];
            float z = value[2];

            // use of gravity
            float asr = (x*x + y*y + z*z) / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
            if(asr >= 2) {
                Random r = new Random();
                int i = r.nextInt(10);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shake_off, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private OnClickListener rLayoutClickListener = new OnClickListener() {
        public void onClick(View v) {
            v.setBackgroundColor(Color.GREEN);
        }
    };
}
