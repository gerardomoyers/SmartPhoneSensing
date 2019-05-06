package com.example.myapplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.graphics.Point;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.content.Context;
import android.os.Environment;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.*;
import java.util.concurrent.TimeUnit;


/**
 * Smart Phone Sensing Example 4. Wifi received signal strength.
 */
public class MainActivity extends Activity implements OnClickListener, SensorEventListener {

    private SensorManager sensorManager;
    /**
     * The accelerometer.
     */
    private Sensor accelerometer;

    /**
     * The wifi manager.
     */
    private WifiManager wifiManager;

     /* Accelerometer x value
     */
    private float aX = 0;
    /**
     * Accelerometer y value
     */
    private float aY = 0;
    /**
     * Accelerometer z value
     */
    private float aZ = 0;
    /*
     * The text view.
     */
    private TextView currentX, currentY, currentZ, stillmove;
    /**
     * The button.
     */
    private Button buttonRssi, cell1, cell2, cell3, cell4;
    /**
     * Training data
     */
    private final String[][] trainingArray = new String[][]//static to determine where I am
            {
                    new String[] {"00:14:5c:8e:94:08","8c:68:c8:88:b7:ec","bc:1c:81:28:df:0a","8c:68:c8:88:b7:ed","70:3a:cb:f5:ec:1f","70:3a:cb:f5:ed:d3","70:3a:cb:f5:ec:1b","00:71:c2:0d:5a:29","70:3a:cb:f5:ed:d7","d4:6e:0e:36:e4:3d","30:b5:c2:22:d1:46","70:3a:cb:fa:c8:27","74:85:2a:18:5e:32","1c:74:0d:3b:98:16","00:0c:f6:cb:04:cc","00:0f:94:b3:cf:c4","d2:6e:0e:36:e4:3e","d4:6e:0e:36:e4:3e","70:3a:cb:fa:c8:23","64:d1:a3:26:90:7e","70:3a:cb:f5:eb:b1","88:03:55:ee:f9:4d","00:0f:94:b3:ca:74","80:2a:a8:d1:26:54","70:3a:cb:fa:c4:54","72:3a:cb:f9:15:15","bc:30:7e:38:ec:81","2a:a1:83:9b:2e:2b","72:3a:cb:f9:15:d9","74:a7:8e:e1:1d:f0","62:a7:8e:e1:1d:f1","00:0f:94:b3:cb:44","72:df::70:a3:68:5d","e8:df:70:a3:68:5c","dc:9f:db:64:2e:b8"},
                    new String[] {"00:14:5c:8e:94:08","8c:68:c8:88:b7:ec","bc:1c:81:28:df:0a","8c:68:c8:88:b7:ed","64:d1:a3:26:90:7e","00:71:c2:0d:5a:29","1c:74:0d:3b:98:16","00:0f:94:b3:cf:c4","30:b5:c2:22:d1:46","00:71:c2:0d:5c:c1","70:3a:cb:f5:ed:d7","80:2a:a8:d1:26:54","74:85:2a:18:5e:32","00:14:5c:97:8b:3e","62:a7:8e:e1:1d:f1","70:3a:cb:f5:ec:1f","00:18:4d:92:8d:c8","74:a7:8e:e1:1d:f0","00:0c:f6:cb:04:cc","70:3a:cb:f5:eb:b1","00:0f:94:b3:cb:44","88:03:55:ee:f9:4d","e8:df:70:a3:68:5c","d4:6e:0e:36:e4:3e","70:3a:cb:fa:c8:27","d2:6e:0e:36:e4:3e","72:df::70:a3:68:5d","fc:ec:da:8a:31:f6","00:0f:94:b3:ca:74","70:3a:cb:f5:ed:d3","70:3a:cb:f5:ec:1b","d4:6e:0e:36:e4:3d"},
                    new String[] {"00:14:5c:8e:94:08","bc:1c:81:28:df:0a","8c:68:c8:88:b7:ec","64:d1:a3:26:90:7e","8c:68:c8:88:b7:ed","70:3a:cb:fa:c8:27","00:71:c2:0d:5a:29","00:0f:94:b3:cf:c4","70:3a:cb:f5:ed:d7","1c:74:0d:3b:98:16","00:71:c2:0d:5c:c1","74:85:2a:18:5e:32","70:3a:cb:f5:ec:1f","30:b5:c2:22:d1:46"},
                    new String[] {"00:14:5c:8e:94:08","70:3a:cb:f5:ed:d7","8c:68:c8:88:b7:ec","64:d1:a3:26:90:7e","bc:1c:81:28:df:0a","e0:28:6d:a3:99:7b","70:3a:cb:fa:c8:27","94:6a:b0:d0:90:11","20:4e:7f:22:ef:dc","cc:ce:1e:e0:6e:a1","44:d9:e7:fd:f3:94","70:3a:cb:f5:ed:d3","70:3a:cb:fa:c4:54"}

            };
    private final Float[][] weights = new Float[][]//static to determine where I am
            {
            /*        new Float[]{(float)(0.25),(float)(0.25),(float)(0.25),(float)(0.25),(float)(0.2375),(float)(0.2375),(float)(0.2375),(float)(0.225),(float)(0.225),(float)(0.2),(float)(0.1875),(float)(0.1625),(float)(0.15),(float)(0.15),(float)(0.15),(float)(0.15),(float)(0.125),(float)(0.1125),(float)(0.0875),(float)(0.075),(float)(0.0625),(float)(0.0625),(float)(0.0625),(float)(0.05),(float)(0.025),(float)(0.025),(float)(0.025),(float)(0.025),(float)(0.025),(float)(0.0125),(float)(0.0125),(float)(0.0125),(float)(0.0125),(float)(0.0125),(float)(0.0125)},//332   35   4.1499996
                    new Float[]{(float)(0.25),(float)(0.25),(float)(0.25),(float)(0.25),(float)(0.25),(float)(0.225),(float)(0.225),(float)(0.2125),(float)(0.2125),(float)(0.2),(float)(0.1875),(float)(0.125),(float)(0.125),(float)(0.0875),(float)(0.0875),(float)(0.075),(float)(0.075),(float)(0.075),(float)(0.0625),(float)(0.05),(float)(0.05),(float)(0.05),(float)(0.0375),(float)(0.0375),(float)(0.0375),(float)(0.025),(float)(0.025),(float)(0.025),(float)(0.0125),(float)(0.0125),(float)(0.0125),(float)(0.0125),(float)0,(float)0,(float)0},//289    3.6125004
                    new Float[]{(float)(0.25),(float)(0.25),(float)(0.2125),(float)(0.2125),(float)(0.2125),(float)(0.175),(float)(0.1625),(float)(0.0875),(float)(0.0075),(float)(0.0625),(float)(0.0375),(float)(0.0375),(float)(0.0375),(float)(0.025),(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0},//147   1.77
                    new Float[]{(float)(0.25),(float)(0.25),(float)(0.2125),(float)(0.2125),(float)(0.1875),(float)(0.1),(float)(0.1),(float)(0.1),(float)(0.0875),(float)(0.0375),(float)(0.025),(float)(0.025),(float)(0.0125),(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0}//128    1.6

                    new Float[]{(float)(1),(float)(1),(float)(1),(float)(1),(float)(0.95),(float)(0.95),(float)(0.95),(float)(0.9),(float)(0.9),(float)(0.8),(float)(0.75),(float)(0.65),(float)(0.6),(float)(0.6),(float)(0.6),(float)(0.6),(float)(0.5),(float)(0.45),(float)(0.35),(float)(0.3),(float)(0.25),(float)(0.25),(float)(0.25),(float)(0.2),(float)(0.1),(float)(0.1),(float)(0.1),(float)(0.1),(float)(0.1),(float)(0.05),(float)(0.05),(float)(0.05),(float)(0.05),(float)(0.05),(float)(0.05)},//332
                    new Float[]{(float)(1),(float)(1),(float)(1),(float)(1),(float)(1),(float)(0.9),(float)(0.9),(float)(0.85),(float)(0.85),(float)(0.8),(float)(0.75),(float)(0.5),(float)(0.5),(float)(0.35),(float)(0.35),(float)(0.3),(float)(0.3),(float)(0.3),(float)(0.25),(float)(0.2),(float)(0.2),(float)(0.2),(float)(0.15),(float)(0.15),(float)(0.15),(float)(0.1),(float)(0.1),(float)(0.1),(float)(0.05),(float)(0.05),(float)(0.05),(float)(0.05),(float)0,(float)0,(float)0},//289
                    new Float[]{(float)(1),(float)(1),(float)(0.85),(float)(0.85),(float)(0.85),(float)(0.7),(float)(0.65),(float)(0.35),(float)(0.3),(float)(0.25),(float)(0.15),(float)(0.15),(float)(0.15),(float)(0.1),(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0},//147
                    new Float[]{(float)(1),(float)(1),(float)(0.85),(float)(0.85),(float)(0.75),(float)(0.4),(float)(0.4),(float)(0.4),(float)(0.35),(float)(0.15),(float)(0.1),(float)(0.1),(float)(0.05),(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0}//128
*/
                    new Float[]{(float) 1,(float) 1,(float) 1,(float) 1,(float) 1,(float) 1},
                    new Float[]{(float) 1,(float) 1,(float) 1,(float) 1,(float) 1,(float) 1},
                    new Float[]{(float) 1,(float) 1,(float) 1,(float) 1,(float) 1,(float) 1},
                    new Float[]{(float) 1,(float) 1,(float) 1,(float) 1,(float) 1,(float) 1}
            };
    private Integer[][] booleans = new Integer[][]//static to determine where I am
            {

                    new Integer[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                    new Integer[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                    new Integer[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                    new Integer[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}

  /*                  new Integer[]{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    new Integer[]{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    new Integer[]{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    new Integer[]{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    */        };




    String filename = "BSSIDS.txt";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create items.

        buttonRssi = (Button) findViewById(R.id.buttonRSSI);
        cell1=(Button) findViewById(R.id.cell1);
        cell2=(Button) findViewById(R.id.cell2);
        cell3=(Button) findViewById(R.id.cell3);
        cell4=(Button) findViewById(R.id.cell4);

        stillmove = (TextView) findViewById(R.id.stillmove);
        currentX = (TextView) findViewById(R.id.currentX);
        currentY = (TextView) findViewById(R.id.currentY);
        currentZ = (TextView) findViewById(R.id.currentZ);
        // Set listener for the button.
        buttonRssi.setOnClickListener(this);

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

        //useHandler();

    }

    public void saveFileMat(String file,  Integer[][] bool, Float[][] weight){

        final File path =
                Environment.getExternalStoragePublicDirectory
                        (
                                Environment.DIRECTORY_DCIM + "/Camera/"
                        );

        if(!path.exists())
        {
            // Make it, if it doesn't exit
            path.mkdirs();
        }

        final File filee = new File(path, "matrix.txt");

        try{
            //FileOutputStream fos = openFileOutput(file, Context.MODE_PRIVATE);
            filee.createNewFile();
            FileOutputStream fos = new FileOutputStream(filee);
            Float sum[]={(float)0,(float)0,(float)0,(float)0};
            // Write results to a label
            for (int i =0; i< bool.length; i++) {
                int j=0;
                for (Integer b : bool[i]) {
                    Float a=weight[i][j];
                    a*=b;
                    fos.write(a.toString().getBytes());
                    fos.write(", ".getBytes());
                    j++;
                    sum[i]+=a;
                }
                fos.write("\n".getBytes());
                fos.write(sum[i].toString().getBytes());
                fos.write("\n".getBytes());
                fos.write("\n".getBytes());

            }

            fos.close();
            Toast.makeText(MainActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
        }
    } /// saveMat


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
    public void onClick(View v) {
        // Set text.
        //still_move.setText("Move");
        //still_move.setText("Still");
        // Set wifi manager.
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        // Start a wifi scan.

        wifiManager.startScan();

        // Store results in a list.
        List<ScanResult> scanResults = wifiManager.getScanResults();


        int counter =0;
        int numwifis=35; // variable of max number of wifi founded in all cells

        //What I need to do is to take as reference my array of scanResults and create a new array with results of booleans

        int numbelem = trainingArray.length;// number of cells

        //List<ScanResult> scanResults = wifiManager.getScanResults();

        for (int i=0; i < numbelem; i++){
            //1 to 4
            counter =0;

            for (String nameSSID:trainingArray[i]){

                for (ScanResult scanResult : scanResults) {
                    if (nameSSID.contentEquals(scanResult.BSSID)){
                        if (counter<numwifis) {
                            booleans[i][counter] = 1;
                            break;

                        }
                    };
                }
                counter = counter +1;
            }
        }
        saveFileMat(filename, booleans, weights);
        Float sum[]={(float)0,(float)0,(float)0,(float)0};
        // Write results to a label
        for (int i =0; i< booleans.length; i++) {
            int j=0;
            for (Integer b : booleans[i]) {
                Float a=weights[i][j];
                a*=b;
                j++;
                sum[i]+=a;
            }
        }

        Float tempcells= (float)0;
        int actualcell=0;
        for (int i=0; i<sum.length; i++){
            if (sum[i]>tempcells) {
                tempcells=sum[i];
                actualcell=i+1;
            }
        }

        switch (actualcell) {
            // cell 1
            case 1: {
                cell1.setBackgroundColor(Color.RED);
                cell2.setBackgroundColor(Color.GRAY);
                cell3.setBackgroundColor(Color.GRAY);
                cell4.setBackgroundColor(Color.GRAY);
                break;
            }
            case 2: {
                cell1.setBackgroundColor(Color.GRAY);
                cell2.setBackgroundColor(Color.RED);
                cell3.setBackgroundColor(Color.GRAY);
                cell4.setBackgroundColor(Color.GRAY);
                break;
            }
            case 3: {
                cell1.setBackgroundColor(Color.GRAY);
                cell2.setBackgroundColor(Color.GRAY);
                cell3.setBackgroundColor(Color.RED);
                cell4.setBackgroundColor(Color.GRAY);
                break;
            }
            case 4: {
                cell1.setBackgroundColor(Color.GRAY);
                cell2.setBackgroundColor(Color.GRAY);
                cell3.setBackgroundColor(Color.GRAY);
                cell4.setBackgroundColor(Color.RED);
                break;
            }
            case 0:{
                cell1.setBackgroundColor(Color.TRANSPARENT);
                cell2.setBackgroundColor(Color.TRANSPARENT);
                cell3.setBackgroundColor(Color.TRANSPARENT);
                cell4.setBackgroundColor(Color.TRANSPARENT);
                Toast.makeText(MainActivity.this, "Not Found", Toast.LENGTH_SHORT).show();
            }
        }


        for (int i=0;i<numbelem;i++)for (int j=0;j<numwifis;j++) booleans[i][j]=0;


    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        aX = event.values[0];
        aY = event.values[1];
        aZ = event.values[2];

        currentX.setText(Float.toString(aX));
        currentY.setText(Float.toString(aY));
        currentZ.setText(Float.toString(aZ));
        if (((aX >= (float) 0.4) || (aX <= (float) -0.3)) && (aZ >= (float) 10)){
            stillmove.setTextColor(Color.RED);
            Toast.makeText(MainActivity.this, "walking", Toast.LENGTH_SHORT).show();
        }else{
            stillmove.setTextColor(Color.BLUE);
        }





    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public boolean isExternalStorageWritable() {

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}