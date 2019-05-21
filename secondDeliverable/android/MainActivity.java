package com.example.example4;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import android.app.Activity;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Smart Phone Sensing Example 4. Wifi received signal strength.
 */
public class MainActivity extends Activity implements OnClickListener {

    /**
     * The wifi manager.
     */
    private WifiManager wifiManager;
    /**
     * The text view.
     */
    private TextView textRssi;
    /**
     * The button.
     */
    private Button buttonRssi;
    /**
     * Training data
     */

    String filename = "BSSIDS" ;
    int counter = 0;

    ArrayList<ArrayList<String>> BSSIDS = new ArrayList<ArrayList<String>>();
    ArrayList<ArrayList<Integer>> RSS = new ArrayList<ArrayList<Integer>>();
    ArrayList<String> cell = new ArrayList<String>();
    ArrayList<Integer> cellIn = new ArrayList<Integer>();
    ArrayList<Integer> cellcount= new ArrayList<Integer>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create items.
        textRssi = (TextView) findViewById(R.id.textRSSI);
        buttonRssi = (Button) findViewById(R.id.buttonRSSI);
        // Set listener for the button.
        buttonRssi.setOnClickListener(this);
    }

    public void saveFile(String file,  ArrayList<ArrayList<String>> scanResults, ArrayList<Integer> counters, ArrayList<ArrayList<Integer>> levels){

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
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
        String strDate = mdformat.format(calendar.getTime());

        filename = filename + strDate;
        filename = filename + ".txt";
        final File filee = new File(path, filename);
        final File filee2 = new File(path, "countsBSSIDS.txt");

        try{
            //FileOutputStream fos = openFileOutput(file, Context.MODE_PRIVATE);
            filee.createNewFile();
            FileOutputStream fos = new FileOutputStream(filee);
            filee2.createNewFile();
            FileOutputStream fos2 = new FileOutputStream(filee2);

            ArrayList<String> temp= new ArrayList<String>(scanResults.get(0)); //we get a row
            ArrayList<Integer> lev = new ArrayList<Integer>(levels.get(0));
            int k=0;
            //Log.e("numberRow", String.valueOf(j));


                // Write results to a label
                for ( String scanResult : temp) {

                    fos.write(scanResult.getBytes());
                    fos.write("\n".getBytes());
                    Log.e("leve", lev.get(k).toString());
                    fos.write(lev.get(k).toString().getBytes());
                    fos.write("\n".getBytes());

                    k++;

                }



            fos.close();
            fos2.close();

            Toast.makeText(MainActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    Handler mHandler;
    public void useHandler() {
        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, 150);
    }

    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            Log.e("Handlers", "Calls");

            wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            // Start a wifi scan.

            counter = counter +1;

            wifiManager.startScan();

            // Store results in a list.
            List<ScanResult> scanResults = wifiManager.getScanResults();
            int pos=0;
            int coun = 0;
            if (counter != 0) {
                coun = cell.size();
            }

            // Write results to a label
            Log.e("COUNTER", String.valueOf(counter));
            for (ScanResult scanResult : scanResults) {
                //textRssi.setText(textRssi.getText() + "\n\tBSSID = "
                //        + scanResult.BSSID + "    RSSI = "
                //        + scanResult.level + "dBm");
                textRssi.setText("\ncoun" +counter);
                Log.e("rssi", String.valueOf(scanResult.level));
                Log.e("nameeee", scanResult.BSSID);
                cell.add(scanResult.BSSID);
                cellIn.add(scanResult.level);
                Log.e("rssiCell", String.valueOf(cellIn.get(coun)));
                Log.e("nameeeeCell", cell.get(coun));
                coun = coun+1;
                }

            mHandler.postDelayed(mRunnable, 8000);


            BSSIDS.add(cell);
            RSS.add(cellIn);
            for (int j = cell.size(); j == 0; j--) {
                cell.remove(j);
                cellIn.remove(j);
                cellcount.remove(j);
            }


            if (counter > 30) {
                Log.e("here", String.valueOf(counter));
                //Log.e("saving", BSSIDS);
                saveFile(filename, BSSIDS, cellcount, RSS);

                counter =0;
            }
         }

    };



    // onResume() registers the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
    }

    // onPause() unregisters the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        // Set text.
        textRssi.setText("\n\tScan all access points:");
        // Set wifi manager.
        useHandler();

            //cell.clear();
            //cellcount.clear();


    }
}