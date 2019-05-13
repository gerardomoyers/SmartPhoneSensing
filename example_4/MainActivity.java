package com.example.example4;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.concurrent.TimeUnit;

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

    String filename = "BSSIDS.txt";

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

    public void saveFile(String file,  ArrayList<ArrayList<String>> scanResults, ArrayList<Integer> counters){

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

        final File filee = new File(path, "BSSIDS.txt");
        final File filee2 = new File(path, "countsBSSIDS.txt");

        try{
            //FileOutputStream fos = openFileOutput(file, Context.MODE_PRIVATE);
            filee.createNewFile();
            FileOutputStream fos = new FileOutputStream(filee);
            filee2.createNewFile();
            FileOutputStream fos2 = new FileOutputStream(filee2);

            for (int j=0;j<scanResults.size();j++){
                ArrayList<String> temp= new ArrayList<String>(scanResults.get(j));
                int k=0;


                // Write results to a label
                for ( String scanResult : temp) {
                    Integer tempc=counters.get(k);
                    //fos.write(scanResult.getBytes());
                    fos.write(scanResult.getBytes());
                    fos.write("\n".getBytes());
                    //fos.write("   ".getBytes());
                    fos2.write(tempc.toString().getBytes());
                    fos2.write("\n".getBytes());
                    k++;
                }

            }



            fos.close();
            fos2.close();

            Toast.makeText(MainActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

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
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        // Start a wifi scan.
        ArrayList<ArrayList<String>> BSSIDS = new ArrayList<ArrayList<String>>();
        ArrayList<String> cell = new ArrayList<String>();
        ArrayList<Integer> cellcount= new ArrayList<Integer>();
        int dummy=cell.size();

        for (int x=0; x<20; x++) {
            wifiManager.startScan();

            // Store results in a list.
            List<ScanResult> scanResults = wifiManager.getScanResults();
            int pos=0;
            //mock data
            //ScanResult data1 = new scanResult();
            int cellsize=cell.size();
            // Write results to a label
            if (cellsize==dummy) {
                for (ScanResult scanResult : scanResults) {
                    textRssi.setText(textRssi.getText() + "\n\tBSSID = "
                            + scanResult.BSSID + "    RSSI = "
                            + scanResult.level + "dBm");
                    cell.add(scanResult.BSSID);
                    cellcount.add(1);

                }
            }else{
                for (ScanResult scanResult : scanResults) {
                    for (int n=0;n<cell.size();n++){
                        Integer sum=cellcount.get(n); // temp variable for counting each address
                        if (scanResult.BSSID.contentEquals(cell.get(n))) {
                            sum++;
                            cellcount.set(n,sum);
                            break;
                        }
                        if (n==cell.size()-1) {
                            textRssi.setText(textRssi.getText() + "\n\tBSSID = "
                                    + scanResult.BSSID + "    RSSI = "
                                    + scanResult.level + "dBm");
                            cell.add(scanResult.BSSID);
                            cellcount.add(1);
                        }
                    }
                }

            }

            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }


        BSSIDS.add(cell);
        saveFile(filename, BSSIDS,cellcount);
        for (int j=cell.size();j==0;j--){
            cell.remove(j);
            cellcount.remove(j);
        }
            //cell.clear();
            //cellcount.clear();


    }
}