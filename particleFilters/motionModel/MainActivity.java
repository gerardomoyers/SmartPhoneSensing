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


//                    if ((now - lastDetected) > timeout*1000000){
//                        totalSteps = (int) event.values[0] - reportedSteps - lastSteps;
//                    }
//                    else{
//                        totalSteps= (int) event.values[0] - reportedSteps;
//                    }
//
//                    lastDetected = event.timestamp;

                    // Calculate steps taken based on
                    // first value received.

                    stepsTaken = (int) event.values[0] - reportedSteps;
                    //lastSteps = stepsTaken;
                    //showToast("result" + stepsTaken);

                    // Output the value to the simple GUI
                    //showToast("Cnt: " + stepsTaken);
                    //countText.setText("Cnt: " + stepsTaken);
                    //showToast("Det: " + stepsTaken);
                    //Log.e("here", "totalSteps"+totalSteps);

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
//                    if (event.values.length > 4) {
//                        float[] truncatedRotationVector = new float[4];
//                        System.arraycopy(event.values, 0, truncatedRotationVector, 0, 4);
//                        update(truncatedRotationVector);
//                    } else {
//                        update(event.values);
//                    }


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



public void update(float[] vectors) {

        final int FROM_RADS_TO_DEGS = -57;
        float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, vectors);
        int worldAxisX = SensorManager.AXIS_X;
        int worldAxisZ = SensorManager.AXIS_Z;
        float[] adjustedRotationMatrix = new float[9];
        SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisX, worldAxisZ, adjustedRotationMatrix);
        float[] orientation = new float[3];
        SensorManager.getOrientation(adjustedRotationMatrix, orientation);
        float yaw = orientation[2] * FROM_RADS_TO_DEGS;

        Log.e("here","yawInside"+yaw);
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





/*
    //start

    public interface StepListener {
        public void onStep();
        public void passValue();
    }





    final SensorEventListener thiss = new SensorEventListener() {

//        private final static String TAG = "StepDetector";
//        private float   mLimit = 10;
//        private float   mLastValues[] = new float[3*2];
//        private float   mScale[] = new float[2];
//        private float   mYOffset;
//
//        private float   mLastDirections[] = new float[3*2];
//        private float   mLastExtremes[][] = { new float[3*2], new float[3*2] };
//        private float   mLastDiff[] = new float[3*2];
//        private int     mLastMatch = -1;

        private ArrayList<StepListener> mStepListeners = new ArrayList<StepListener>();



//        public void thiss() {
//            int h = 480; // TODO: remove this constant
//            mYOffset = h * 0.5f;
//            mScale[0] = - (h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
//            mScale[1] = - (h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
//        }


//        public void setSensitivity(float sensitivity) {
//            mLimit = sensitivity; // 1.97  2.96  4.44  6.66  10.00  15.00  22.50  33.75  50.62
//        }

        public void addStepListener(StepListener sl) {
            mStepListeners.add(sl);
        }

        @Override
        //public void onSensorChanged(int sensor, float[] values) {
        public void onSensorChanged(SensorEvent event) {
            Sensor sensor = event.sensor;

            //final static String TAG = "StepDetector";
            float   mLimit = (float) 1.97;
            float   mLastValues[] = new float[3*2];
            float   mScale[] = new float[2];
            float   mYOffset;

            float   mLastDirections[] = new float[3*2];
            float   mLastExtremes[][] = { new float[3*2], new float[3*2] };
            float   mLastDiff[] = new float[3*2];
            int     mLastMatch = -1;





            int h = 480; // TODO: remove this constant
            mYOffset = h * 0.5f;
            mScale[0] = - (h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
            mScale[1] = - (h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));

            synchronized (this) {
                if (sensor.getType() == Sensor.TYPE_ORIENTATION) {
                }
                else {
                    int j = (sensor.getType() == Sensor.TYPE_ACCELEROMETER) ? 1 : 0;
                    if (j == 1) {
                        float vSum = 0;
                        for (int i=0 ; i<3 ; i++) {
                            final float v = mYOffset + event.values[i] * mScale[j];
                            vSum += v;
                        }
                        int k = 0;
                        float v = vSum / 3;

                        float direction = (v > mLastValues[k] ? 1 : (v < mLastValues[k] ? -1 : 0));
                        if (direction == - mLastDirections[k]) {
                            // Direction changed
                            int extType = (direction > 0 ? 0 : 1); // minumum or maximum?
                            mLastExtremes[extType][k] = mLastValues[k];
                            float diff = Math.abs(mLastExtremes[extType][k] - mLastExtremes[1 - extType][k]);

                            if (diff > mLimit) {

                                boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k]*2/3);
                                boolean isPreviousLargeEnough = mLastDiff[k] > (diff/3);
                                boolean isNotContra = (mLastMatch != 1 - extType);

                                if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
                                    Log.i("here", "step");
                                    for (StepListener stepListener : mStepListeners) {
                                        stepListener.onStep();
                                    }
                                    mLastMatch = extType;
                                }
                                else {
                                    mLastMatch = -1;
                                }
                            }
                            mLastDiff[k] = diff;
                        }
                        mLastDirections[k] = direction;
                        mLastValues[k] = v;
                    }
                }
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub
        }

    };

    //end

*/

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
        // This happens when you click any of the four buttons.
        // For each of the buttons, when it is clicked we change:
        // - The text in the center of the buttons
        // - The margins
        // - The text that shows the margin
        float startLat = 0;
        float startLon = 0;
        int altitude = 0;
        int time = 0;
        float maybe = 0;
        double floatBearing = 0;

        if (SensorManager.getRotationMatrix(rMat, iMat, gData, mData)) {

            Float azz = SensorManager.getOrientation(rMat, orientation)[0];

            mAzimuth = (float) ((Math.toDegrees(azz)+ 360) % 360.0);



            Log.e("this", ""+mAzimuth);

            /*
            GeomagneticField geo = new GeomagneticField((float) startLat, (float) startLon, altitude, time);
            float decl = geo.getDeclination();

            if (orientation[0] >= 0) {
                // Azimuth-Calculation (rad in degree) + difference to true north (decl)
                maybe = (orientation[0] * 57.29577951f + decl);
            } else {
                // Azimuth-Calculation (rad in degree) +360 + difference to true north (decl)
                maybe = (orientation[0] * 57.29577951f + 360 + decl);
            }


            floatBearing = orientation[0];
            floatBearing = Math.toDegrees(floatBearing);
            if (geo != null)
                floatBearing += geo.getDeclination();
            if (floatBearing < 0)
                floatBearing += 360;
        context = this;





        }

        //showToast("hihi");
        Log.e("hihi", "hey");
        Log.e("hihi", "hey"+mAzimuth);
        Log.e("second trial", "h"+maybe);
        Log.e("third", "j"+floatBearing);

        //showToast("Here"+mAzimuth);
*/
            //calculate distance
            floatBearing = 0;


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

//        showToast("angle: " +floatBearing);
//        showToast("two"+ mAzimuth);
//        showToast("tre"+maybe);


//        switch (v.getId()) {
//            // UP BUTTON
//            case R.id.button1: {
//                Toast.makeText(getApplication(), "UP", Toast.LENGTH_SHORT).show();
//                Rect r = drawable.getBounds();
//                drawable.setBounds(r.left,r.top-20,r.right,r.bottom-20);
//                textView.setText("\n\tMove Up" + "\n\tTop Margin = "
//                        + drawable.getBounds().top);
//                break;
//            }
//            // DOWN BUTTON
//            case R.id.button4: {
//                Toast.makeText(getApplication(), "DOWN", Toast.LENGTH_SHORT).show();
//                Rect r = drawable.getBounds();
//                drawable.setBounds(r.left,r.top+20,r.right,r.bottom+20);
//                textView.setText("\n\tMove Down" + "\n\tTop Margin = "
//                        + drawable.getBounds().top);
//                break;
//            }
//            // LEFT BUTTON
//            case R.id.button2: {
//                Toast.makeText(getApplication(), "LEFT", Toast.LENGTH_SHORT).show();
//                Rect r = drawable.getBounds();
//                drawable.setBounds(r.left-20,r.top,r.right-20,r.bottom);
//                textView.setText("\n\tMove Left" + "\n\tLeft Margin = "
//                        + drawable.getBounds().left);
//                break;
//            }
//            // RIGHT BUTTON
//            case R.id.button3: {
//                Toast.makeText(getApplication(), "RIGHT", Toast.LENGTH_SHORT).show();
//                Rect r = drawable.getBounds();
//                drawable.setBounds(r.left+20,r.top,r.right+20,r.bottom);
//                textView.setText("\n\tMove Right" + "\n\tLeft Margin = "
//                        + drawable.getBounds().left);
//                break;
//            }
//        }

//        // redrawing of the object
//        canvas.drawColor(Color.WHITE);
//        drawable.draw(canvas);
//        for(ShapeDrawable wall : walls)
//            wall.draw(canvas);
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