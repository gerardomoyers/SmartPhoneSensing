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
import android.os.Environment;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
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
import java.util.Random;

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
    private ArrayList<ArrayList<ShapeDrawable>> drawable, drawablebefore;
    private ShapeDrawable me;
    /**
     * The canvas.
     */
    private Canvas canvas;

    /**
     * probability
     */
    private ArrayList<ArrayList<Integer>> probability ;

    private int resampling, maxprob;
    private float convergence;
    /**
     * The walls.
     */
    private List<ShapeDrawable> walls;


    //start Teresa code
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
                    //now = event.timestamp;

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

                        if (mAzimuth > mAzimuthInitial || mAzimuth < 90) {
                            realAngle = -resize / 2.0; //rotating CW
                        } else if (mAzimuth < mAzimuthInitial) {
                            realAngle = resize / 2.0;
                        }
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
            r = x * x + y * y;
        } while (r > 1 || r == 0);    // loop executed 4 / pi = 1.273.. times on average
        // http://en.wikipedia.org/wiki/Box-Muller_transformshow


        // apply the Box-Muller formula to get standard Gaussian z
        offset = x * Math.sqrt(-2.0 * Math.log(r) / r);

        // print it to standard output

    }

    ;


    //end Teresa code


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        // get the screen dimensions
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        int ch = (int)  (height * 0.276) + 1;
        int cw = (int)  (width * 0.276) + 1;
        int roomwidth = (int) ((width - cw) * 0.42666); //
        int roomheight = (int) ((height - ch)  * 0.05555);  //
        ch-=20;
        cw/=2;


        // create a drawable object
        me = new ShapeDrawable(new OvalShape());
        me.getPaint().setColor(Color.RED);
        me.setBounds(width/2-10, height/2-10, width/2+10, height/2+10);
        drawable = new ArrayList<ArrayList<ShapeDrawable>>();
        drawablebefore = new ArrayList<ArrayList<ShapeDrawable>>();

        probability = new ArrayList<ArrayList<Integer>>();
        resampling = 0;
        maxprob = 1;
        convergence = (float) 0.15;

        for (int fy = 40; fy < roomheight * 18 ; fy += 15 ){
            //      for (int fy = 600; fy < roomheight * 16 + 20; fy += 20 ){
            ArrayList<ShapeDrawable> dotx = new ArrayList<>();
            ArrayList<ShapeDrawable> dotxb = new ArrayList<>();
            ArrayList<Integer> probx = new ArrayList<>();
//            for (int fx = roomwidth/2; fx < width - roomwidth/2; fx += 20){

            for (int fx = cw; fx < width-cw; fx += 15){
                ShapeDrawable dot = new ShapeDrawable(new OvalShape());
                ShapeDrawable dotb = new ShapeDrawable(new OvalShape());
                dot.getPaint().setColor(Color.BLUE);
                dot.setBounds(fx-4, fy-4, fx+4, fy+4);
                dotx.add(dot);
                dotb.getPaint().setColor(Color.BLUE);
                dotb.setBounds(fx-4, fy-4, fx+4, fy+4);
                dotxb.add(dotb);
                probx.add(1);
            }
            probability.add(probx);
            drawable.add(dotx);
            drawablebefore.add(dotxb);
        }
        walls = new ArrayList<>();
        //bottom rooms
        for (int i = 0; i < 17; i++){
            ShapeDrawable wall_bottom = new ShapeDrawable(new RectShape());
            if (i >= 15 || i == 11 || i == 10){
                if (i == 16){
                    ShapeDrawable wall_bottom1 = new ShapeDrawable(new RectShape());
                    ShapeDrawable wall_bottom2 = new ShapeDrawable(new RectShape());
                    ShapeDrawable wall_bottom3 = new ShapeDrawable(new RectShape());
                    wall_bottom1.setBounds(cw+roomwidth/2, height - i*roomheight - ch, cw+roomwidth, height - ch - 50 - (i-1)* roomheight);
                    walls.add(wall_bottom1);
                    wall_bottom2.setBounds(cw, 20, cw+roomwidth, height - ch - i* roomheight);
                    walls.add(wall_bottom2);
                    wall_bottom3.setBounds(cw, 0-20, width-cw, 20);
                    walls.add(wall_bottom3);

                }

                wall_bottom.setBounds(cw,height - i* roomheight - ch - 10, cw+roomwidth, height - ch - i* roomheight);
            }else {
                wall_bottom.setBounds(cw,height - i* roomheight - 510 - 5, cw+roomwidth, height - ch - i* roomheight);
            }

            if (i < 12 || i > 14 ) {
                walls.add(wall_bottom);
            }


        }
        // top rooms
        for (int i = 0; i <= 16; i++){
            ShapeDrawable wall_top = new ShapeDrawable(new RectShape());
            if (i >= 14){
                wall_top.setBounds(width-cw - roomwidth, i* roomheight + 20 - 10, width-cw, i* roomheight + 20 );
            }else{
                wall_top.setBounds(width-cw - roomwidth, i* roomheight + 20 - 5, width-cw, i* roomheight + 20 );
            }
            if (i == 16){
                ShapeDrawable wall_top1 = new ShapeDrawable(new RectShape());
                wall_top1.setBounds(cw, height-520, width-cw, height-490 );
                walls.add(wall_top1);
                wall_top.setBounds(width-cw - roomwidth, (i-1)* roomheight + 20, width-cw, height-510 );
            }

            walls.add(wall_top);
        }
        ShapeDrawable wallside = new ShapeDrawable(new RectShape());
        wallside.setBounds(cw-10,0,cw+5,height - 490);
        ShapeDrawable wallside2 = new ShapeDrawable(new RectShape());
        wallside2.setBounds(width-cw-5,0,width-cw+10,  height - 490);
        walls.add(wallside);
        walls.add(wallside2);



        // create a canvas
        ImageView canvasView = (ImageView) findViewById(R.id.canvas);
        Bitmap blankBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(blankBitmap);
        canvasView.setImageBitmap(blankBitmap);

        // draw the objects
        me.draw(canvas);
        for (int fy = 0; fy < drawable.size(); fy ++){
            for(ShapeDrawable dots : drawable.get(fy))
                dots.draw(canvas);
        }

        for(ShapeDrawable wall : walls)
            wall.draw(canvas);

        //start teresa sensor

        Sensor mStepCounterSensor;
        SensorManager mSensorManager;

        final int SENSOR_DELAY = 500 * 1000; // 500ms

        stepsTaken = 0;
        reportedSteps = 0;
        stepDetector = 0;


        //MOTION MODEL
        context = this;

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


        // Register the listeners. Used for receiving notifications from
        // the SensorManager when sensor values have changed.

        //sensorManager.registerListener(thiss, senStepCounter, SENSOR_DELAY);
        sensorManager.registerListener(thiss, senStepDetector, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(thiss, senAccelerometer, SENSOR_DELAY);
        sensorManager.registerListener(thiss, senRotation, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(thiss, mag, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(thiss, mRotationSensor, SensorManager.SENSOR_DELAY_NORMAL);

        for (int i = 0; i < 5; i++) {
            x.add((double) 0);
            y.add((double) 0);
            xUpdated[i] = (double) 0;
            yUpdated[i] = (double) 0;
        }

        //end Teresa sensor


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        // This happens when you click any of the four buttons.
        // For each of the buttons, when it is clicked we change:
        // - The text in the center of the buttons
        // - The margins
        // - The text that shows the margin


        switch (v.getId()) {
            // UP BUTTON
            case R.id.button1: {

                //calculate distance
                Double[] distance = new Double[]{(double) 0, (double) 0, (double) 0, (double) 0, (double) 0};

                //where was I? use of x and y
                //gauss = noise

                for (int j = 0; j < 5; j++) {
                    StdGaussian();
                    double div = offset / 10.0;
                    distance[j] = stepDetector * (1.65 * 0.4 + div);
                    showToast("I moved in magnitude" + distance[j]);
                }

                for (int i = 0; i < 5; i++) {
                    xUpdated[i] = xUpdated[i] + distance[i] * Math.cos(Math.toRadians(realAngle));
                    yUpdated[i] = yUpdated[i] + distance[i] * Math.sin(Math.toRadians(realAngle));

                    showToast("x: " + xUpdated[i]);
                    showToast("y: " + yUpdated[i]);

                }




                for (int fy = 0; fy < drawable.size(); fy++) {
                    for (int fx = 0; fx < drawable.get(0).size(); fx++) {
                        Rect r = new Rect(drawable.get(fy).get(fx).getBounds());
                        drawable.get(fy).get(fx).setBounds(r.left - yUpdated[0].intValue()*20, r.top - xUpdated[0].intValue()*20, r.right - yUpdated[0].intValue()*20, r.bottom - xUpdated[0].intValue()*20);
///                        drawable.get(fy).get(fx).setBounds(r.left,r.top-20,r.right,r.bottom-20);
                        drawablebefore.get(fy).get(fx).setBounds(r.left, r.top, r.right, r.bottom);
                        textView.setText("\n\tMove Up" + "\n\tTop Margin = "
                                + drawable.get(fy).get(fx).getBounds().top);
                    }
                }

                break;
            }
            // DOWN BUTTON
            case R.id.button4: {
                for (int fy = 0; fy < drawable.size(); fy++) {
                    for (int fx = 0; fx < drawable.get(0).size(); fx++) {
                        Rect r = new Rect(drawable.get(fy).get(fx).getBounds());
//                        drawable.get(fy).get(fx).setBounds(r.left,r.top+20,r.right,r.bottom+20);
                        drawable.get(fy).get(fx).setBounds(r.left, r.top + 38, r.right, r.bottom + 38);
                        drawablebefore.get(fy).get(fx).setBounds(r.left, r.top, r.right, r.bottom);
                        textView.setText("\n\tMove Down" + "\n\tTop Margin = "
                                + drawable.get(fy).get(fx).getBounds().top);
                    }
                }
                break;
            }
            // LEFT BUTTON
            case R.id.button2: {
                for (int fy = 0; fy < drawable.size(); fy++) {
                    for (int fx = 0; fx < drawable.get(0).size(); fx++) {
                        Rect r = new Rect(drawable.get(fy).get(fx).getBounds());
                        drawable.get(fy).get(fx).setBounds(r.left - 20, r.top, r.right - 20, r.bottom);
                        drawablebefore.get(fy).get(fx).setBounds(r.left, r.top, r.right, r.bottom);
                        textView.setText("\n\tMove Left" + "\n\tTop Margin = "
                                + drawable.get(fy).get(fx).getBounds().top);
                    }
                }

                break;
            }
            // RIGHT BUTTON
            case R.id.button3: {
                for (int fy = 0; fy < drawable.size(); fy++) {
                    for (int fx = 0; fx < drawable.get(0).size(); fx++) {
                        Rect r = new Rect(drawable.get(fy).get(fx).getBounds());
                        drawable.get(fy).get(fx).setBounds(r.left + 20, r.top, r.right + 20, r.bottom);
                        drawablebefore.get(fy).get(fx).setBounds(r.left, r.top, r.right, r.bottom);
                        textView.setText("\n\tMove Right" + "\n\tTop Margin = "
                                + drawable.get(fy).get(fx).getBounds().top);
                    }
                }
                break;
            }
        }
        // if there is a collision between the dot and any of the walls
        // reset dot to center of canvas
        if (isCollision()){
            int countersafe=0;
            for(int fy=0; fy < drawable.size(); fy++) {
                int maximumfx = 0;
                for (int fx = 0; fx < drawable.get(fy).size(); fx++) {
                    if (maximumfx < drawable.get(fy).size()) maximumfx=drawable.get(fy).size();
                    for(ShapeDrawable wall : walls) {
                        if (isCollision(wall, drawable.get(fy).get(fx),drawablebefore.get(fy).get(fx))){
                            int fxr = 0;
                            int fyr =0;
                            probability.get(fy).set(fx,0);
                            boolean reshape = true;

                            while(reshape == true){

                                fyr = new Random().nextInt(drawable.size());
                                fxr = new Random().nextInt(drawable.get(fyr).size());
                                if (maximumfx < drawable.get(fyr).size()) maximumfx=drawable.get(fyr).size();
                                for(ShapeDrawable wall2 : walls) {
                                    if (isCollision(wall2, drawable.get(fyr).get(fxr),drawablebefore.get(fyr).get(fxr))) {
                                        reshape=true;
                                        countersafe++;
                                        break;
                                    }else{
                                        reshape=false;
                                    }
                                }
                                if (countersafe > drawable.size()*maximumfx){
                                    fxr=fx;
                                    fyr=fy;
                                    break;
                                }
                            }
                            Rect r;
                            if (countersafe > drawable.size()*drawable.get(fy).size()){
                                r = drawablebefore.get(fyr).get(fxr).getBounds();
                            }else{
                                r = drawable.get(fyr).get(fxr).getBounds();
                            }
                            Integer dummy = probability.get(fyr).get(fxr);
                            dummy++;
                            probability.get(fyr).set(fxr, dummy);
                            if (probability.get(fyr).get(fxr) > maxprob)
                                maxprob = probability.get(fyr).get(fxr);
                            drawable.get(fy).get(fx).setBounds(r.left,r.top,r.right,r.bottom);
                            // drawable.get(fy).get(fx).setBounds(width/2-8 , height/2-8, width/2+8, height/2+8);

                        }
                    }
                }
            }
        }



        if (resampling >= 10){
            Toast.makeText(getApplication(), "resampling", Toast.LENGTH_SHORT).show();
            if (convergence <= 0.5){
                convergence+= 0.03;
            }
            int tempmaxprob = maxprob;
            maxprob = 1;
            for (int fy = 0; fy < drawable.size(); fy ++ ){
                for (int fx = 0; fx < drawable.get(fy).size(); fx ++){
                    int fyr = 0;
                    int fxr = 0;
                    Rect r;
                    if (probability.get(fy).get(fx) >= tempmaxprob*convergence){
                        r = drawable.get(fy).get(fx).getBounds();
                        drawable.get(fy).get(fx).setBounds(r.left,r.top,r.right,r.bottom);
                        drawablebefore.get(fy).get(fx).setBounds(r.left,r.top,r.right,r.bottom);
                        if (probability.get(fy).get(fx) > maxprob)
                            maxprob = probability.get(fy).get(fx);

                    }else{
                        do {
                            fyr = new Random().nextInt(drawable.size());
                            fxr = new Random().nextInt(drawable.get(fyr).size());
                        }while (probability.get(fyr).get(fxr)  < tempmaxprob*convergence);

                        if (convergence <= 0.2){
                            if (fx > drawable.get(fy).size()) break;
                            probability.get(fy).remove(fx);
                            drawablebefore.get(fy).remove(fx);
                            drawable.get(fy).remove(fx);
                            if(drawable.get(fy).size()== 0){
                                drawable.remove(fy);
                                fy--;
                                break;
                            }
                            fx--;
                        }else{
                            probability.get(fy).set(fx,0);
                            r = drawable.get(fyr).get(fxr).getBounds();
                            drawable.get(fy).get(fx).setBounds(r.left,r.top,r.right,r.bottom);
                            drawablebefore.get(fy).get(fx).setBounds(r.left,r.top,r.right,r.bottom);
                            Integer dummy = probability.get(fyr).get(fxr);
                            dummy++;
                            probability.get(fyr).set(fxr, dummy);
                            //if (probability.get(fyr).get(fxr) > maxprob)
                            //  maxprob = probability.get(fyr).get(fxr);
                        }
                    }
                }
            }
            resampling = 0;
            maxprob = 1;
            for (int fy = 0; fy < drawable.size(); fy ++ ) {
                for (int fx = 0; fx < drawable.get(fy).size(); fx++) {
                    probability.get(fy).set(fx,1);
                }
            }
        }

        // redrawing of the object
        canvas.drawColor(Color.WHITE);
        for (int fy = 0; fy < drawable.size(); fy++) {
            for (ShapeDrawable dots : drawable.get(fy))
                dots.draw(canvas);
        }
        for (ShapeDrawable wall : walls)
            wall.draw(canvas);
    }

    /**
     * Determines if the drawable dot intersects with any of the walls.
     * @return True if that's true, false otherwise.
     */
    private boolean isCollision() {
        for(ShapeDrawable wall : walls) {
            for(int fy=0; fy < drawable.size(); fy++)
                for (int fx=0; fx < drawable.get(fy).size(); fx++)
                    if(isCollision(wall,drawable.get(fy).get(fx),drawablebefore.get(fy).get(fx) ))
                        return true;
        }
        return false;
    }

    /**
     * Determines if two shapes intersect.
     * @param first The first shape.
     * @param second The second shape.
     * @return True if they intersect, false otherwise.
     */
    private boolean isCollision(ShapeDrawable first, ShapeDrawable second, ShapeDrawable third) {
        Rect secondRect = new Rect(second.getBounds()); // point actual
        Rect thirdRect = new Rect(third.getBounds()); //point before
        Rect stepRect = new Rect();
        if (secondRect.left < thirdRect.left - 6 || secondRect.top < thirdRect.top - 6){
            if (secondRect.left > thirdRect.left){
                stepRect.set(thirdRect.left, secondRect.top, secondRect.right ,thirdRect.bottom);
            }else if (secondRect.top > thirdRect.top){
                stepRect.set(secondRect.left, thirdRect.top, thirdRect.right ,secondRect.bottom);
            }else{
                stepRect.set(secondRect.left, secondRect.top, thirdRect.right ,thirdRect.bottom);
            }
        }else {
            if (secondRect.left < thirdRect.left){
                stepRect.set(secondRect.left, thirdRect.top, thirdRect.right ,secondRect.bottom);
            }else if (secondRect.top < thirdRect.top){
                stepRect.set(thirdRect.left, secondRect.top, secondRect.right ,thirdRect.bottom);
            }else{
                stepRect.set(thirdRect.left, thirdRect.top, secondRect.right ,secondRect.bottom);
            }
        }
        return stepRect.intersect(first.getBounds());
    }

    // Simple function that can be used to display toasts
    public void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });

    }
}