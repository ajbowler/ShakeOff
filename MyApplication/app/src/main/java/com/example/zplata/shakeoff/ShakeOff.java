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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.media.MediaPlayer;

import java.util.Random;


public class ShakeOff extends Activity {

    private TextView centerCount;
    private RelativeLayout rLayout;
    private SensorManager sensorMgr;
    private Sensor mAccel;
    private Random random;
    private ProgressBar levelProgressBar;
    private int currentProgress;
    private int maxProgress;

    private MediaPlayer mp;

    private ShakeEventManager mShake;

    public int shakes = 0; // like "count" but for in a global scope
    public int totalShakes = 0;
    public int level = 1;
    public int levelRequirement = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake_off);

        // Vars
        centerCount = (TextView) findViewById(R.id.centerCount);
        rLayout = (RelativeLayout) findViewById(R.id.rLayout);
        rLayout.setOnClickListener(rLayoutClickListener);

        mp = MediaPlayer.create(getApplicationContext(), R.raw.tswift);
        mp.start();
        mp.setLooping(true);
        random = new Random();


        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccel = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShake = new ShakeEventManager();
        mShake.setOnShakeListener(new ShakeEventManager.OnShakeListener() {
            @Override
            public void onShake(int count, int currentProgress) {
                handleShakeEvent(count, currentProgress);
            }

            public void handleShakeEvent(int count) { totalShakes = count; shake(); }


            public void handleShakeEvent(int count, int currentProgress) {
                centerCount.setText(count + "");
            }

        });
    }


    public void shake() {
        shakes++;
        centerCount.setText(shakes + "");
        if(shakes >= level * levelRequirement) {
            shakes = 0;
            level++;

        }

    }



    @Override
    protected void onResume() {
        super.onResume();
        mp.start();
        sensorMgr.registerListener(mShake, mAccel, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mp.pause();
        sensorMgr.unregisterListener(mShake);
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
            v.setBackgroundColor(random.nextInt());
        }
    };
}
