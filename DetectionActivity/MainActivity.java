package com.example.example2;

import android.app.Activity;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import android.os.*;
import android.widget.Toast;

/**
 * Smart Phone Sensing Example 2. Working with sensors.
 */
public class MainActivity extends Activity implements SensorEventListener {

    /**
     * The sensor manager object.
     */
    private SensorManager sensorManager;
    /**
     * The accelerometer.
     */
    private Sensor accelerometer;
    /**
     * The wifi manager.
     */
    private WifiManager wifiManager;
    /**
     * The wifi info.
     */
    private WifiInfo wifiInfo;
    /**
     * Accelerometer x value
     */
    private float aX = 0, maxaX=0, minaX=0;
    /**
     * Accelerometer y value
     */
    private float aY = 0, maxaY=0, minaY=0;
    /**
     * Accelerometer z value
     */
    private float aZ = 0, maxaZ=0, minaZ=0;

    /**
     * Text fields to show the sensor values.
     */
    private TextView currentX, currentY, currentZ, titleAcc, textRssi, mincurrentX, mincurrentY, mincurrentZ;

    Button buttonRssi;
    private float axMAX_global;
    private float axMIN_global;
    private float ayMAX_global;
    private float ayMIN_global;
    private int counter =0;
    private int numResults =0;
    private final float[] trainingMax= new float[]{0,0,0,0,0,0,0,0,0,0}; //I start w x axis
    private final float[] trainingMin= new float[]{0,0,0,0,0,0,0,0,0,0}; //I start w x axis
    private final float[] trainingMaxY= new float[]{0,0,0,0,0,0,0,0,0,0}; //I start w x axis
    private final float[] trainingMinY= new float[]{0,0,0,0,0,0,0,0,0,0}; //I start w x axis
    private boolean finished = false;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the text views.
        currentX = (TextView) findViewById(R.id.currentX);
        currentY = (TextView) findViewById(R.id.currentY);
        currentZ = (TextView) findViewById(R.id.currentZ);
        mincurrentX = (TextView) findViewById(R.id.currentX2);
        mincurrentY = (TextView) findViewById(R.id.currentY2);
        mincurrentZ = (TextView) findViewById(R.id.currentZ2);
        titleAcc = (TextView) findViewById(R.id.titleAcc);
        textRssi = (TextView) findViewById(R.id.textRSSI);

        // Create the button
        buttonRssi = (Button) findViewById(R.id.buttonRSSI);

        // Set the sensor manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // if the default accelerometer exists
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // set accelerometer
            accelerometer = sensorManager
                    .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            // register 'this' as a listener that updates values. Each time a sensor value changes,
            // the method 'onSensorChanged()' is called.
            sensorManager.registerListener(this, accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            // No accelerometer!
        }

        // Set the wifi manager
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        // Create a click listener for our button.
        buttonRssi.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the wifi info.
                wifiInfo = wifiManager.getConnectionInfo();
                // update the text.
                textRssi.setText("\n\tSSID = " + wifiInfo.getSSID()
                        + "\n\tRSSI = " + wifiInfo.getRssi()
                        + "\n\tLocal Time = " + System.currentTimeMillis());

                //reset values of max min...

            }
        });

        useHandler();
        /**
        if (finished) {
            //use distances to check majority of values comparing max to zero
            for (int i =0; i < trainingMax.length; i++) {
                if (trainingMax[i] > 1){
                    numResults = numResults+1;
                }
            }
            //I take from granted that 70% of coincidences means walking
            if (numResults >= 7){
                Toast.makeText(MainActivity.this, "walking", Toast.LENGTH_SHORT).show();
            }
        }
         **/
    }

    Handler mHandler;
    public void useHandler() {
        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, 50);
    }

    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            Log.e("Handlers", "Calls");
            /** Do something **/
            trainingMax[counter] = axMAX_global;
            //trainingMin[counter] = axMIN_global;
            trainingMaxY[counter] = ayMAX_global;
            //trainingMinY[counter] = ayMIN_global;
            counter = counter+1;
            float maxValueX = 0;
            float minValueX = 0;
            float maxValueY = 0; //negative values
            float minValueY = 0;


            if (counter == 10){
                finished = true;
                counter = 0;
                if (axMAX_global > 0) {
                    minValueX = axMAX_global; // cause it can be positive
                }
                else{
                    maxValueX = axMAX_global;
                }
                if (ayMAX_global > 0) {
                    minValueY = ayMAX_global; // cause it can be negative
                }
                else{
                    minValueY = ayMAX_global;
                }

                for (int i = 0 ; i<trainingMax.length; i++) {
                    Log.e("ArrayFinitooMAX", String.valueOf(trainingMax[i]));
                    //Log.e("ArrayFinitooMIN", String.valueOf(trainingMin[i]));
                    Log.e("ArrayFinitooMAXY", String.valueOf(trainingMaxY[i]));
                    //Log.e("ArrayFinitooMINY", String.valueOf(trainingMinY[i]));
                }


                for (int i =0; i < trainingMax.length; i++) {
                    if (trainingMax[i] > maxValueX){
                        maxValueX = trainingMax[i];
                    }
                    if (trainingMax[i] < minValueX){
                        minValueX = trainingMax[i];
                    }
                    if (trainingMaxY[i] < maxValueY){
                        maxValueY = trainingMaxY[i];
                    }
                    if (trainingMaxY[i] < minValueY){
                        minValueY = trainingMaxY[i];
                    }
                }
                /*
                //I take from granted that 70% of coincidences means walking
                if (numResults >= 7){
                    Toast.makeText(MainActivity.this, "walking", Toast.LENGTH_SHORT).show();
                }
                */
                if (maxValueX >= 10 && maxValueY <-1.5){
                    Toast.makeText(MainActivity.this, "walking", Toast.LENGTH_SHORT).show();
                }
                numResults = 0;
                maxValueX = 0;
                maxValueY = 0;
            }
            mHandler.postDelayed(mRunnable, 50);
        }
    };

    // onResume() registers the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    // onPause() unregisters the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing.
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //currentX.setText("0.0");
        //currentY.setText("0.0");
        //currentZ.setText("0.0");

        // get the the x,y,z values of the accelerometer
        aX = event.values[0];
        aY = event.values[1];
        aZ = event.values[2];


        if(aX > maxaX){
            currentX.setText(Float.toString(aX));
            maxaX=aX;
        }if (aX < minaX) {
            mincurrentX.setText(Float.toString(aX));
            minaX=aX;
        }
        if(aY > maxaY){
            currentY.setText(Float.toString(aY));
            maxaY=aY;
        }if (aY < minaY) {
            mincurrentY.setText(Float.toString(aY));
            minaY=aY;
        }
        if(aZ > maxaZ){
            currentZ.setText(Float.toString(aZ));
            maxaZ=aZ;
        }if (aZ < minaZ) {
            mincurrentZ.setText(Float.toString(aZ));
            minaZ=aZ;
        }

        if (counter >=9){
            Log.e("hey", "imhere");
            Log.e("hey", String.valueOf(aX));
            maxaX = 0;
            minaX = 0;
            maxaY = 0;
            minaY = 0;
        }

        axMAX_global = aX;
        //axMIN_global = ;
        ayMAX_global = aY;
        //ayMIN_global = minaY;
        //azMAX_global = aX;
        //azMIN_global = aX;






        if ((Math.abs(aX) > Math.abs(aY)) && (Math.abs(aX) > Math.abs(aZ))) {
            titleAcc.setTextColor(Color.RED);
        }
        if ((Math.abs(aY) > Math.abs(aX)) && (Math.abs(aY) > Math.abs(aZ))) {
            titleAcc.setTextColor(Color.BLUE);
        }
        if ((Math.abs(aZ) > Math.abs(aY)) && (Math.abs(aZ) > Math.abs(aX))) {
            titleAcc.setTextColor(Color.GREEN);
        }
    }

    public boolean isExternalStorageWritable() {

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}