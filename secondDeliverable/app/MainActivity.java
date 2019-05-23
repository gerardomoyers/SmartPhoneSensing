package com.example.example4;

import java.io.File;
import java.io.FileOutputStream;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Map;

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
public class MainActivity<pmf> extends Activity implements OnClickListener {

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

    ArrayList<Float> pmf= new ArrayList<Float>();

    //Map<Integer, Float[]> row = new HashMap<Integer, Float[]>();

    Map<String, Map<Integer, Float[]>> matrix = new HashMap<String, Map<Integer, Float[]>>();
    boolean see = false;
    boolean see2 = false;
    boolean see3 = false;




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

    private void readFromFile2() throws IOException {
        final File path =
                Environment.getExternalStoragePublicDirectory
                        (
                                Environment.DIRECTORY_DOWNLOADS
                        );
        File file = new File(path, "MyFile.txt");

        BufferedReader br = new BufferedReader(new FileReader(file));

        String st;
        Integer cell = 0;
        Integer ind = 0;
        String kkey = "";
        String kname= "key";

        while ((st = br.readLine()) != null) {
            Log.e("string", st);

            if (st.indexOf('y')>0){

                ind = st.indexOf('y');
                ind = ind +1;
                Integer coun = 0;
                if (st.substring(0, ind).contentEquals(("key"))) {
                    Map<Integer, Float[]> row = new HashMap<Integer, Float[]>();
                    //store BSSID


                    kkey = st.substring(5, st.length());https://www.google.com/search?client=ubuntu&channel=fs&q=ordered+dict+in+python+3&ie=utf-8&oe=utf-8;
                    Log.e("I stored key", kkey);
                    //matrix.put(kkey, row);

                    st=br.readLine(); //empty line
                    if (st == null){
                        break;
                    }
                    st = br.readLine(); //cell
                    if (st == null){
                        break;
                    }

                    while (!(st.contentEquals(""))) {
                        Log.e("I stored key", kkey);
                        if (st.contentEquals("")){
                            Log.e("hey","shoulnot1");
                        }
                        if (coun > 0){
                            st = br.readLine();
                            if (st == null){
                                break;
                            }
                        }
                        if (st.contentEquals("")){
                            break;
                        }
                        if (st.substring(0, st.indexOf('l')+2).contentEquals("cell")) {
                            String thiis = st.substring(st.indexOf('l') + 4, st.length());
                            String thiis2 = st.substring(st.indexOf('l') + 4, st.length()-1);
                            cell = Integer.valueOf(st.substring(st.indexOf('l') + 4, st.length()));
                        }

                        //the thing is that I will have read the one I want to check
                        st = br.readLine();
                        if (st == null){
                            break;
                        }
                        //st is already 0,0,0,0...
                        String[] stringsF = st.split(",");
                        Float[] arr = new Float[stringsF.length];
                        for (int i = 0; i < stringsF.length; i++) {
                            //pmf.add(Float.valueOf(stringsF[i]));
                            arr[i]=Float.valueOf(stringsF[i]);
                        }
                        row.put(cell, arr);
                        //matrix.put(kkey, row);
                        matrix.put(kkey, row);
                        //Log.e("row", String.valueOf(row.get(cell).get(1)));
                        for (int j = pmf.size(); j >= 0; j--) {
                            if (j>0) {
                                pmf.remove(j - 1);
                            }
                        }
                        coun++;
                    }

                }
            }

        }
        Log.e("handler", "here");
        cell = 0;
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
        Float[] prob_cells = new Float[] {
                (float) 1/16, (float) 1/16, (float) 1/16, (float) 1/16, (float) 1/16, (float) 1/16, (float) 1/16, (float) 1/16, (float) 1/16, (float) 1/16, (float) 1/16, (float) 1/16, (float) 1/16, (float) 1/16, (float) 1/16, (float) 1/16
        };
        Float maxprob = (float) 1/16;
        //here is where we create dictionary
        try {
            readFromFile2();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Set text.
        textRssi.setText("\n\tScan all access points:");
        // Set wifi manager.
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        // Start a wifi scan.
        wifiManager.startScan();

        // Store results in a list.
        List<ScanResult> scanResults = wifiManager.getScanResults();
        int pos=0;
        int coun = 0;

        // Write results to a label
        Log.e("COUNTER", String.valueOf(counter));
        for (ScanResult scanResult : scanResults) {
            //textRssi.setText(textRssi.getText() + "\n\tBSSID = "
            //        + scanResult.BSSID + "    RSSI = "
            //        + scanResult.level + "dBm");
            textRssi.setText("\ncoun" +counter);


////   -38 a -95 dbm

            if (matrix.get(scanResult.BSSID)!=null){
                Map<Integer, Float[]> temp = matrix.get(scanResult.BSSID);
                Float[] arr = new Float[16];
                Integer indexcell, index = (-scanResult.level)-38;
                Float normalize;
                for (Map.Entry<Integer, Float[]> entry: temp.entrySet()){
                    //arr[counter] = entry.getValue()[index];
                    arr[counter] = (float)0.01 * counter;
                    counter = counter +1;
                }
                counter = 0;
                //while (maxprob < (float) 0.8){
                    normalize = (float)0;
                    for (int i = 0; i<arr.length; i++) {
                        prob_cells[i] *= arr[i];
                        normalize += prob_cells[i];
                    }
                    for (int i = 0; i<prob_cells.length; i++) {
                        prob_cells[i] /= normalize;
                        if (prob_cells[i] > maxprob) {
                            maxprob = prob_cells[i];
                            indexcell = i;
                        }
                    }
                //}
                if (maxprob > 0.5) {
                    // cell choosen
                    break;
                }
            }
        }

        //check matrix




    }
}
