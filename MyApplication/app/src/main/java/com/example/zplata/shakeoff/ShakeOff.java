package com.example.zplata.shakeoff;

import android.graphics.Color;
import android.hardware.SensorEventListener;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.app.Activity;
import java.util.Random;
import android.os.Handler;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.media.MediaPlayer;
import android.content.Context;
import android.content.Intent;

import java.util.Random;


public class ShakeOff extends Activity {

    private TextView centerCount;
    private TextView levelCount;
    private TextView totalCount;
    private TextView hiddenBossMsg;
    private TextView timerText;
    private RelativeLayout rLayout;
    private SensorManager sensorMgr;
    private Sensor mAccel;
    private Random random;
    private ProgressBar levelProgressBar;
    private ImageView hiddenBossImg0;
    private ImageView hiddenBossImg1;
    private ImageView hiddenBossImg2;
    private ImageView hiddenBossImg3;
    private int currentProgress;
    private int maxProgress;

    private MediaPlayer mp;

    private ShakeEventManager mShake;

    public int shakes = 0; // like "count" but for in a global scope
    public int totalShakes = 0;
    public int level = 1;
    public int levelRequirement = 5;

    //Boss Values
    private boolean bossFight; // is the boss fight happening
    private int tempShakes = 0;
    private int image = 0;
    private int kShakes;
    private int bossTime; // milliseconds
    private int maxTime = 15000; // 15 seconds to defeat Nick
    private boolean youWin = false;

    //Venmo Auth
    private String auth = "padD3bRMsJtbePcZ3QKd6V3WxYXs8EPa";
    private String amt = "0.10";





    // Timer
    // timerHandler.postDelayed(timerRunnable, 0); to start
    // timerHandler.removeCallbacks(timerRunnable); to pause (not necessary)
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {

            long millis = System.currentTimeMillis();
            timerText.setText("Timer " + millis);

            if(bossFight){
                hiddenBossImg0.setVisibility(View.GONE);
                hiddenBossImg1.setVisibility(View.GONE);
                hiddenBossImg2.setVisibility(View.GONE);
                hiddenBossImg3.setVisibility(View.GONE);
                if(image == 0)
                    hiddenBossImg0.setVisibility(View.VISIBLE);
                if(image == 1)
                    hiddenBossImg1.setVisibility(View.VISIBLE);
                if(image == 2)
                    hiddenBossImg2.setVisibility(View.VISIBLE);
                if(image == 3)
                    hiddenBossImg3.setVisibility(View.VISIBLE);
                image++;
                image%=4;

                if(bossTime >= maxTime) {

                    if(tempShakes >= kShakes) {
                        centerCount.setText("LEVEL UP");
                        centerCount.setVisibility(View.VISIBLE);
                        level++;
                        levelCount.setText("Level " + level);
                        hiddenBossMsg.setVisibility(View.GONE);
                    }
                    else {
                        doVenmo();
                    }
                    bossFight = false;
                }
            }

            timerHandler.postDelayed(this, 100); // calls itself in 10
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake_off);

        // Vars
        centerCount = (TextView) findViewById(R.id.centerCount);
        centerCount.bringToFront();
        levelCount = (TextView) findViewById(R.id.levelCount);
        totalCount = (TextView) findViewById(R.id.totalCount);
        timerText = (TextView) findViewById(R.id.timerText);
        hiddenBossMsg = (TextView) findViewById(R.id.hiddenBossMsg);
        rLayout = (RelativeLayout) findViewById(R.id.rLayout);
        rLayout.setOnClickListener(rLayoutClickListener);
        levelProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        hiddenBossImg0 = (ImageView) findViewById(R.id.hiddenBossImg0);
        hiddenBossImg1 = (ImageView) findViewById(R.id.hiddenBossImg1);
        hiddenBossImg2 = (ImageView) findViewById(R.id.hiddenBossImg2);
        hiddenBossImg3 = (ImageView) findViewById(R.id.hiddenBossImg3);

        mp = MediaPlayer.create(getApplicationContext(), R.raw.tswift);
        mp.start();
        mp.setLooping(true);
        random = new Random();

        levelProgressBar.setProgress(0);
        levelProgressBar.setMax(level * levelRequirement);

        hiddenBossImg0.setVisibility(View.GONE);
        hiddenBossImg1.setVisibility(View.GONE);
        hiddenBossImg2.setVisibility(View.GONE);
        hiddenBossImg3.setVisibility(View.GONE);

        timerHandler.postDelayed(timerRunnable, 0);


        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccel = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShake = new ShakeEventManager();
        mShake.setOnShakeListener(new ShakeEventManager.OnShakeListener() {
            @Override
            public void onShake() {
                handleShakeEvent();
            }

            public void handleShakeEvent() {
                shake();
            }

        });
    }


    public void shake() {

        if(level % 5 == 0 && level != 0) {
            bossShake();
            bossFight = true;
        }

        if(!bossFight) { // because shakes is for leveling
            shakes++;
            levelProgressBar.incrementProgressBy(1);
            if(shakes >= level * levelRequirement) {
                shakes = 0;
                centerCount.setText("LEVEL UP"); //TODO make a different text for this
                level++;
                levelCount.setText("Level " + level);
                updateProgressBar();
            }
        }
        else {
            tempShakes++;
        }
        totalShakes++;
        totalCount.setText("Total " + totalShakes);
        centerCount.setTextSize(222);
        centerCount.setText(shakes + "");

    }

    private void bossShake () {

        tempShakes = 0;
        bossTime = 0;
        kShakes = level * levelRequirement;

        centerCount.setVisibility(View.GONE);
        hiddenBossMsg.setText("ShakeOff w/ Nicholas!");
        hiddenBossMsg.setVisibility(View.VISIBLE);
    }

    private void doVenmo(){
        /*boolean venmoInstalled = VenmoLibrary.isVenmoInstalled(this);
        if(venmoInstalled){
           Intent venmoIntent = VenmoLibrary.openVenmoPayment(auth, "ShakeOff", "145434160922624933",
           amt, "A message to accompany the payment.", "charge");
           startActivityForResult(venmoIntent, 1);
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case 1: {
                if(resultCode == RESULT_OK) {
                    String signedRequest = data.getStringExtra("signedextra");
                    if(signedRequest != null) {
                        VenmoLibrary.VenmoResponse response = (new VenmoLibrary())
                                .validateVenmoPaymentResponse(signedRequest, "secret");
                        if(response.getSuccess().equals("1")) {
                            // Payment Successful
                            String note = response.getNote();
                            String amount = response.getAmount();
                        }
                    }
                    else{
                        String error_message = data.getStringExtra("error_message");
                    }
                }
                else if(resultCode == RESULT_CANCELED){
                    // user cancelled payment
                    Toast.makeText(this,"VENMO CANCELLED PAYMENT", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private void updateProgressBar() {
        levelProgressBar.setProgress(0);
        levelProgressBar.setMax(level * levelRequirement);
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
            shake(); //TODO take this out when it's almost done
        }
    };

}
