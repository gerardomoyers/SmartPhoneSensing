package com.example.example6;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.hardware.GeomagneticField;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;



/**
 * Smart Phone Sensing Example 6. Object movement and interaction on canvas.
 */
public class MainActivity extends Activity implements OnClickListener {

    /**
     * The buttons.
     */
    private Button up, left, right, down;
    /**
     * The text view.
     */
    private TextView textView;
    /**
     * The shape.
     */
    private ShapeDrawable drawable;
    /**
     * The canvas.
     */
    private Canvas canvas;
    /**
     * The walls.
     */
    private List<ShapeDrawable> walls;

    private Context context;

    private SensorManager sensorManager;

    private Sensor senAccelerometer;
    private Sensor senStepCounter;
    private Sensor senStepDetector;
    private Sensor senRotation;
    private Sensor mag;
    private Sensor mRotationSensor;

    int stepsTaken;
    int reportedSteps;
    int stepDetector;
    float[] angle;
    int totalSteps;
    int lastSteps;
    long lastDetected;
    long now;
    long timeout = 1000000;

    private TextView countText;
    private TextView detectText;
    private TextView accelText;

    float[] rMat = new float[9];
    float[] iMat = new float[9];
    float[] gData = new float[3]; // accelerometer
    float[] mData = new float[3]; // magnetometer
    private float mAzimuth = 0; // degree
    private float mAzimuthInitial = 0;
    float[] orientation = new float[3];
    double offset;
    double realAngle = 0;

    @Nullable
    private Rotation calibrationDirection;
    @Nullable
    private Rotation latestDirection;


    List<Double> x = new ArrayList<Double>();
    List<Double> y = new ArrayList<Double>();

    Double[] xUpdated = new Double[]{(double) 0, (double) 0, (double) 0, (double) 0, (double) 0};
    Double[] yUpdated = new Double[]{(double) 0, (double) 0, (double) 0, (double) 0, (double) 0};




    final SensorEventListener thiss = new SensorEventListener() {




        @Override
        public void onSensorChanged(SensorEvent event) {

            Sensor sensor = event.sensor;
            int numm = 1000;

            // Perform differing functionality depending upon
            // the sensor type (caller)

            switch (event.sensor.getType()) {

                case Sensor.TYPE_STEP_COUNTER:
                    now = event.timestamp;

                    if (reportedSteps < 1) {

                        // Log the initial value

                        reportedSteps = (int) event.values[0];
                    }


                    stepsTaken = (int) event.values[0] - reportedSteps;

                    break;

                case Sensor.TYPE_STEP_DETECTOR:

                    new CountDownTimer(20000, 1000) {

                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            stepDetector = 0;
                        }
                    }.start();


                    // Increment the step detector count


                    stepDetector++;
                    //showToast("Det: " + stepDetector);
                    // Output the value to the simple GUI


                    // detectText.setText("Det: " + stepDetector);

                    break;


                case Sensor.TYPE_ACCELEROMETER:

                    // Get the accelerometer values and set them to a string with 2dp

                    String x = String.format("%.02f", event.values[0]);
                    String y = String.format("%.02f", event.values[1]);
                    String z = String.format("%.02f", event.values[2]);
                    gData = event.values.clone();
                    // Output the string to the GUI

                    //showToast("Acc:" + x + "," + y + "," + z);
                    //accelText.setText("Acc:" + x + "," + y + "," + z);

                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    mData = event.values.clone();
                    break;


                case Sensor.TYPE_ROTATION_VECTOR:
                    Rotation rotation = new Rotation(
                            (double) event.values[3], // quaternion scalar
                            (double) event.values[0], // quaternion x
                            (double) event.values[1], // quaternion y
                            (double) event.values[2], // quaternion z
                            false); // no need to normalise

                    if (calibrationDirection == null) {
                        // Save the first sensor value obtained as the calibration value
                        calibrationDirection = rotation;
                        if (SensorManager.getRotationMatrix(rMat, iMat, gData, mData)) {

                            Float azz = SensorManager.getOrientation(rMat, orientation)[0];

                            mAzimuthInitial = (float) ((Math.toDegrees(azz) + 360) % 360.0);

                            Log.e("this", "" + mAzimuth);
                        }

                    } else {
                        // Apply the reverse of the calibration direction to the newly
                        //  obtained direction to obtain the direction the user is facing
                        //  relative to his/her original direction
                        latestDirection = calibrationDirection.applyInverseTo(rotation);
                        double value = latestDirection.getAngles(RotationOrder.XYX)[1] * 360;

                        if (value > 1040) {
                            value = 1040;
                        }
                        if (value < 40) {
                            value = 40;
                        }
                        double resize = (value - 40) * (360 - 0) / (double) (1040 - 40) + 0.0;

                        if (SensorManager.getRotationMatrix(rMat, iMat, gData, mData)) {

                            Float azz = SensorManager.getOrientation(rMat, orientation)[0];

                            mAzimuth = (float) ((Math.toDegrees(azz) + 360) % 360.0);

                        }



                        if (mAzimuth > mAzimuthInitial || mAzimuth <90) {
                            realAngle = -resize / 2.0; //rotating CW
                        } else if (mAzimuth < mAzimuthInitial) {
                            realAngle = resize / 2.0;
                        }
                        Log.e("here", ""+mAzimuth);
                        //realAngle = mAzimuth-mAzimuthInitial;
                        Log.e("trial", "" + realAngle);
                    }

                    break;


            }
        }


        @Override
        public void onAccuracyChanged(Sensor sensorEvent, int accuracy) {

        }


    };



    public void StdGaussian() {

            double r, x, y;

            // find a uniform random point (x, y) inside unit circle
            do {
                x = 2.0 * Math.random() - 1.0;
                y = 2.0 * Math.random() - 1.0;
                r = x*x + y*y;
            } while (r > 1 || r == 0);    // loop executed 4 / pi = 1.273.. times on average
            // http://en.wikipedia.org/wiki/Box-Muller_transformshow


            // apply the Box-Muller formula to get standard Gaussian z
            offset = x * Math.sqrt(-2.0 * Math.log(r) / r);

            // print it to standard output

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Sensor mStepCounterSensor;
        SensorManager mSensorManager;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final int SENSOR_DELAY = 500 * 1000; // 500ms



        // set the buttons
        up = (Button) findViewById(R.id.button1);
        left = (Button) findViewById(R.id.button2);
        right = (Button) findViewById(R.id.button3);
        down = (Button) findViewById(R.id.button4);

        // set the text view
        textView = (TextView) findViewById(R.id.textView1);

        // set listeners
        up.setOnClickListener(this);
        down.setOnClickListener(this);
        left.setOnClickListener(this);
        right.setOnClickListener(this);




        //MOTION MODEL
        context = this;




        // Properties to store step data

        stepsTaken = 0;
        reportedSteps = 0;
        stepDetector = 0;


        // GUI Components to display data

        TextView countText;
        TextView detectText;
        TextView accelText;

        // Reference/Assign the sensor manager

        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);

        // Reference/Assign the sensors

        senAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senStepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        senStepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        senRotation = sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
        mag = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mRotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        // Register the sensors for event callback

        showToast("Registering sensors!");

        // Register the listeners. Used for receiving notifications from
        // the SensorManager when sensor values have changed.

        //sensorManager.registerListener(thiss, senStepCounter, SENSOR_DELAY);
        sensorManager.registerListener(thiss, senStepDetector, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(thiss, senAccelerometer, SENSOR_DELAY);
        sensorManager.registerListener(thiss, senRotation, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(thiss, mag, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(thiss, mRotationSensor, SensorManager.SENSOR_DELAY_NORMAL);

        for (int i=0 ; i<5 ; i++) {
            x.add((double) 0);
            y.add((double) 0);
            xUpdated[i] = (double) 0;
            yUpdated[i] = (double) 0;
        }


    }




    @Override
    public void onClick(View v) {

            //calculate distance
            Double[] distance = new Double[]{(double) 0, (double) 0, (double) 0, (double) 0, (double) 0};

            //where was I? use of x and y
            //gauss = noise

            for (int j = 0; j < 5; j++) {
                StdGaussian();
                double div = offset / 10.0;
                distance[j] = stepDetector * (1.65 * 0.4 + div);
                showToast("I moved in magnitude"+ distance[j]);
            }

            for (int i = 0; i < 5; i++) {
                xUpdated[i] = xUpdated[i] + distance[i] * Math.cos(Math.toRadians(realAngle));
                yUpdated[i] = yUpdated[i] + distance[i] * Math.sin(Math.toRadians(realAngle));

                showToast("x: "+ xUpdated[i]);
                showToast("y: "+ yUpdated[i]);

            }

    }


    @Override
    public void onResume()
    {
        super.onResume();

        // Register the sensors

        sensorManager.registerListener(thiss, senStepCounter, SensorManager.SENSOR_DELAY_NORMAL);
    }


    // Simple function that can be used to display toasts
    public void showToast(final String message)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}