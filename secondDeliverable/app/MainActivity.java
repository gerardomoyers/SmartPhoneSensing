package com.example.app2_redmi;
//package com.example.myapplication2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.List;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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
import java.lang.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Set;


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
    private Button buttonRssi, cell1, cell2, cell3, cell4, cell5, cell6, cell7, cell8, cell9, cell10, cell11, cell12, cell13, cell14, cell15, cell16;
    /**
     * Training data
     */

    String filename = "BSSIDS" ;
    int counter = 0;

    ArrayList<Float> pmf= new ArrayList<Float>();

    //Map<Integer, Float[]> row = new HashMap<Integer, Float[]>();

    Map<String, Map<Integer, Float[]>> matrix = new HashMap<String, Map<Integer, Float[]>>();

    Integer lengthPmf= 0;
    int indexcell = 0;
    int lastcell = 0;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create items.
        textRssi = (TextView) findViewById(R.id.textRSSI);
        buttonRssi = (Button) findViewById(R.id.buttonRSSI);
        cell1 = (Button) findViewById(R.id.cell1);
        cell2 = (Button) findViewById(R.id.cell2);
        cell3 = (Button) findViewById(R.id.cell3);
        cell4 = (Button) findViewById(R.id.cell4);
        cell5 = (Button) findViewById(R.id.cell5);
        cell6 = (Button) findViewById(R.id.cell6);
        cell7 = (Button) findViewById(R.id.cell7);
        cell8 = (Button) findViewById(R.id.cell8);
        cell9 = (Button) findViewById(R.id.cell9);
        cell10 = (Button) findViewById(R.id.cell10);
        cell11 = (Button) findViewById(R.id.cell11);
        cell12 = (Button) findViewById(R.id.cell12);
        cell13 = (Button) findViewById(R.id.cell13);
        cell14 = (Button) findViewById(R.id.cell14);
        cell15 = (Button) findViewById(R.id.cell15);
        cell16 = (Button) findViewById(R.id.cell16);


        // Set listener for the button.
        buttonRssi.setOnClickListener(this);

    }

    private void readFromFile2() throws IOException {
        final File path =
                Environment.getExternalStoragePublicDirectory
                        (
                                Environment.DIRECTORY_DOWNLOADS
                        );
        File file = new File(path, "MyFileRawGaus.txt");

        BufferedReader br = new BufferedReader(new FileReader(file));

        String st;
        Integer cell = 0;
        Integer ind = 0;
        Integer ind2 = 0;
        String kkey = "";
        String kname= "key";

        while ((st = br.readLine()) != null) {


            if (st.indexOf('y')>0){

                ind = st.indexOf('y');
                ind2 = st.indexOf('k');
                ind = ind +1;
                Integer coun = 0;
                if (st.substring(ind2, ind).contentEquals(("key"))) {
                    Map<Integer, Float[]> row = new HashMap<Integer, Float[]>();
                    //store BSSID


                    kkey = st.substring(5, st.length()); https://www.google.com/search?client=ubuntu&channel=fs&q=ordered+dict+in+python+3&ie=utf-8&oe=utf-8;

                    //matrix.put(kkey, row);

                    st=br.readLine(); //empty line
                    //
                    //
                    //
                    // st=br.readLine(); //empty line
                    if (st == null){
                        break;
                    }
                    st = br.readLine(); //cell
                    if (st == null){
                        break;
                    }

                    while (!(st.contentEquals(""))) {

                        if (st.contentEquals("")){

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

                        //for use later on
                        lengthPmf=stringsF.length;

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

    @SuppressLint("ResourceType")
    @Override
    public void onClick(View v) {

        Float[] prob_cells = new Float[]{
                (float) 1 / 16, (float) 1 / 16, (float) 1 / 16, (float) 1 / 16, (float) 1 / 16, (float) 1 / 16, (float) 1 / 16, (float) 1 / 16, (float) 1 / 16, (float) 1 / 16, (float) 1 / 16, (float) 1 / 16, (float) 1 / 16, (float) 1 / 16, (float) 1 / 16, (float) 1 / 16
        };
        Float maxprob = (float) 1 / 16;

        Float[] posteriorFinal = new Float[]{
                (float) 0, (float) 0, (float) 0, (float) 0, (float) 0, (float) 0, (float) 0, (float) 0, (float) 0, (float) 0, (float) 0, (float) 0, (float) 0, (float) 0, (float) 0, (float) 0
        };
        Float[] normalized = new Float[]{
                (float) 0, (float) 0, (float) 0, (float) 0, (float) 0, (float) 0, (float) 0, (float) 0, (float) 0, (float) 0, (float) 0, (float) 0, (float) 0, (float) 0, (float) 0, (float) 0
        };


        // Set text.
        textRssi.setText("\n\tScan all access points:");
        //here is where we create dictionary
        try {
            readFromFile2();
        } catch (IOException e) {
            e.printStackTrace();

        }

        //here is where we create dictionary
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //DYNAMIC DATA: after wifi scan

        wifiManager.startScan();
        // Store results in a list.
        List<ScanResult> scanResults = wifiManager.getScanResults();

        //useHandler();
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        wifiManager.startScan();
        List<ScanResult> list2 = wifiManager.getScanResults();

        scanResults.addAll(list2);

        //useHandler();
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        wifiManager.startScan();
        List<ScanResult> list3 = wifiManager.getScanResults();

        scanResults.addAll(list3);


        //ordering results
        HashMap<String, Integer> codenames = new HashMap<String, Integer>();


        for (ScanResult scanResult : scanResults) {
            codenames.put(scanResult.BSSID, scanResult.level);
        }

        Set<Map.Entry<String, Integer>> entries = codenames.entrySet();

        // inspired by Read more: http://www.java67.com/2015/01/how-to-sort-hashmap-in-java-based-on.html#ixzz5oxgKKgRo
        Comparator<Map.Entry<String, Integer>> valueComparator = new Comparator<Map.Entry<String, Integer>>() {

            @Override
            public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
                Integer v1 = e1.getValue();
                Integer v2 = e2.getValue();
                return v2.compareTo(v1);
            }
        };

        List<Map.Entry<String, Integer>> listOfEntries = new ArrayList<Map.Entry<String, Integer>>(entries);

        // sorting HashMap by values using comparator
        Collections.sort(listOfEntries, valueComparator);

        LinkedHashMap<String, Integer> sortedByValue = new LinkedHashMap<String, Integer>(listOfEntries.size());

        // copying entries from List to Map
        for (Map.Entry<String, Integer> entry : listOfEntries) {
            sortedByValue.put(entry.getKey(), entry.getValue());
        }

        //scanResults SORTED by level, now BAYES:

        Float normalize = (float) 0;
        Integer indexcell = 0;


        for (Map.Entry<String, Integer> entryScan : sortedByValue.entrySet()) {

            Float[] arr = new Float[16];

            if (matrix.get(entryScan.getKey()) != null) {

                Map<Integer, Float[]> temp = matrix.get(entryScan.getKey());
                Integer index = (-entryScan.getValue() - 38);
                Boolean check = false;

                for (Map.Entry<Integer, Float[]> entry : temp.entrySet()) {
                    Integer c = (Integer) temp.keySet().toArray()[counter];
                    arr[c - 1] = entry.getValue()[index];
                    if (arr[c - 1] != 0) {
                        check = true;
                    }
                    counter = counter + 1;
                }


                prob_cells = new Float[]{
                        (float) 1 / 16, (float) 1 / 16, (float) 1 / 16, (float) 1 / 16, (float) 1 / 16, (float) 1 / 16, (float) 1 / 16, (float) 1 / 16, (float) 1 / 16, (float) 1 / 16, (float) 1 / 16, (float) 1 / 16, (float) 1 / 16, (float) 1 / 16, (float) 1 / 16, (float) 1 / 16
                };

                for (int ii = 0; ii < arr.length; ii++) {
                    prob_cells[ii] *= arr[ii];
                    posteriorFinal[ii] = posteriorFinal[ii]+prob_cells[ii];
                    //normalize = normalize + posteriorFinal[ii]; //we start adding values of normalization
                }

                counter = 0;
                for (int j = 0; j < posteriorFinal.length; j++){
                    normalize = normalize + posteriorFinal[j];
                }
                for (int j = 0; j < posteriorFinal.length; j++) {
                    normalized[j] = posteriorFinal[j]/ normalize;
                    if (normalized[j] > maxprob) {
                        maxprob = normalized[j];
                        indexcell = j + 1;
                    }
                }
                normalize = Float.valueOf(0);

            }

        }



/*
        //STATIC DATA
        int pos=0;
        int counter = 0;
        Map<Integer, Map<String,Integer>> scanResults = new HashMap<Integer, Map<String,Integer>>();
        HashMap<String, Integer> codenames = new HashMap<String, Integer>();
        final File path =
                Environment.getExternalStoragePublicDirectory
                        (
                                Environment.DIRECTORY_DCIM + "/Camera/"
                        );
        File file = new File(path, "BSSIDS10:40:55.txt");
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line = "";
        Integer indd = 0;
        String stringg= "k";
        Integer counterr =0;
        Integer linees = 0;
        Integer here =0;
        while (true) {
            try {
                if (!((line = br.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            Map<String, Integer> inside = new HashMap<String, Integer>();
            if (line.substring(0, 1).contentEquals("-")) {
                here = 1;
                //Map<String, Integer> inside = new HashMap<String, Integer>();
                indd = Integer.parseInt(line);
                inside.put(stringg, indd);
                scanResults.put(counterr, inside);
                codenames.put(stringg, indd);
                counterr = counterr+1;
            }
            else{
                stringg = line;
            }
            linees = linees+1;
            if (linees>600){
                break;
            }
        }
        Set<Map.Entry<String, Integer>> entries = codenames.entrySet();
        // inspired by Read more: http://www.java67.com/2015/01/how-to-sort-hashmap-in-java-based-on.html#ixzz5oxgKKgRo
        Comparator<Map.Entry<String, Integer>> valueComparator = new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
                Integer v1 = e1.getValue();
                Integer v2 = e2.getValue();
                return v2.compareTo(v1);
            }
        };
        List<Map.Entry<String, Integer>> listOfEntries = new ArrayList<Map.Entry<String, Integer>>(entries);
        // sorting HashMap by values using comparator
        Collections.sort(listOfEntries, valueComparator);
        LinkedHashMap<String, Integer> sortedByValue = new LinkedHashMap<String, Integer>(listOfEntries.size());
        // copying entries from List to Map
        for(Map.Entry<String, Integer> entry : listOfEntries){
            sortedByValue.put(entry.getKey(), entry.getValue());
        }
*/



// start SERIAL!!!!
/*
        for (Map.Entry<String, Integer> entryScan : sortedByValue.entrySet()) {
                Float[] arr = new Float[16];
                if (matrix.get(entryScan.getKey()) != null) {
                    Map<Integer, Float[]> temp = matrix.get(entryScan.getKey());
                    Integer index = (-entryScan.getValue() - 38);
                    Float normalize;
                    Integer indexcell = 0;
                    Boolean check = false;
                    for (Map.Entry<Integer, Float[]> entry : temp.entrySet()) {
                        Integer c = (Integer) temp.keySet().toArray()[counter];
                        arr[c - 1] = entry.getValue()[index];
                        if (arr[c - 1] != 0) {
                            check = true;
                        }
                        counter = counter + 1;
                    }
                    if (check) {
                        normalize = (float) 0;
                        for (int ii = 0; ii < arr.length; ii++) {
                            prob_cells[ii] *= arr[ii];
                            normalize += prob_cells[ii];
                        }
                        for (int j = 0; j < prob_cells.length; j++) {
                            prob_cells[j] /= normalize;
                            if (prob_cells[j] > maxprob) {
                                maxprob = prob_cells[j];
                                indexcell = j;
                            }
                        }
                        //}
                        Log.e("result", String.valueOf(indexcell));
                        if (maxprob > 0.9) {
                            // cell choosen
                            textRssi.setText("\n\tScan all access points:" + indexcell);
                            break;
                        }
                    }
                }
                counter = 0;
            }
*/

// end SERIAL!!!
        //start PARALELL
        //paralell processing
        /*
        Float normalize = (float) 0;
        Integer indexcell = 0;
        for (Map.Entry<String, Integer> entryScan : sortedByValue.entrySet()) {
            Float[] arr = new Float[16];
            if (matrix.get(entryScan.getKey()) != null) {
                Map<Integer, Float[]> temp = matrix.get(entryScan.getKey());
                Integer index = (-entryScan.getValue() - 38);
                Boolean check = false;
                for (Map.Entry<Integer, Float[]> entry : temp.entrySet()) {
                    Integer c = (Integer) temp.keySet().toArray()[counter];
                    arr[c - 1] = entry.getValue()[index];
                    if (arr[c - 1] != 0) {
                        check = true;
                    }
                    counter = counter + 1;
                }
                    prob_cells = new Float[]{
                            (float) 1 / 16, (float) 1 / 16, (float) 1 / 16, (float) 1 / 16, (float) 1 / 16, (float) 1 / 16, (float) 1 / 16, (float) 1 / 16, (float) 1 / 16, (float) 1 / 16, (float) 1 / 16, (float) 1 / 16, (float) 1 / 16, (float) 1 / 16, (float) 1 / 16, (float) 1 / 16
                    };
                    for (int ii = 0; ii < arr.length; ii++) {
                        prob_cells[ii] *= arr[ii];
                        posteriorFinal[ii] = posteriorFinal[ii]+prob_cells[ii];
                        //normalize = normalize + posteriorFinal[ii]; //we start adding values of normalization
                    }
                counter = 0;
                for (int j = 0; j < posteriorFinal.length; j++){
                    normalize = normalize + posteriorFinal[j];
                }
                for (int j = 0; j < posteriorFinal.length; j++) {
                    normalized[j] = posteriorFinal[j]/ normalize;
                    if (normalized[j] > maxprob) {
                        maxprob = normalized[j];
                        indexcell = j;
                    }
                }
                normalize = Float.valueOf(0);
            }
        }
        for (int j = 0; j < posteriorFinal.length; j++) {
            normalized[j] = posteriorFinal[j]/ normalize;
            if (posteriorFinal[j] > maxprob) {
                maxprob = posteriorFinal[j];
                indexcell = j;
            }
        }
        if (maxprob > 0.9) {
            // cell choosen
            textRssi.setText("\n\tScan all access points:" + indexcell);
        }
*/


//end paralell



        //indexcell= new Random().nextInt(16) + 1;
        //indexcell++;
        switch (lastcell) {
            // cell 1

            case 1: {
                cell1.getBackground().clearColorFilter();
                break;
            }
            case 2: {
                cell2.setBackgroundColor(Color.GRAY);
                break;
            }
            case 3: {
                cell3.getBackground().clearColorFilter();
                break;
            }
            case 4: {
                cell4.setBackgroundColor(Color.GRAY);
                break;
            }
            case 5: {
                cell5.setBackgroundColor(Color.GRAY);
                break;
            }
            case 6: {
                cell6.setBackgroundColor(Color.GRAY);
                break;
            }
            case 7: {
                cell7.setBackgroundColor(Color.GRAY);
                break;
            }
            case 8: {
                cell8.setBackgroundColor(Color.GRAY);
                break;
            }
            case 9: {
                cell9.setBackgroundColor(Color.GRAY);
                break;
            }
            case 10: {
                cell10.setBackgroundColor(Color.GRAY);
                break;
            }
            case 11: {
                cell11.getBackground().clearColorFilter();
                break;
            }
            case 12: {
                cell12.setBackgroundColor(Color.GRAY);
                break;
            }
            case 13: {
                cell13.getBackground().clearColorFilter();
                break;
            }
            case 14: {
                cell14.getBackground().clearColorFilter();
                break;
            }
            case 15: {
                cell15.setBackgroundColor(Color.GRAY);
                break;
            }
            case 16: {
                cell16.getBackground().clearColorFilter();
                break;
            }
            case 0:{
                // Toast.makeText(MainActivity.this, "Not Found", Toast.LENGTH_SHORT).show();
            }
        }


        switch (indexcell) {
            // cell 1
            case 1: {
                cell1.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                break;
            }
            case 2: {
                cell2.setBackgroundColor(Color.RED);
                break;
            }
            case 3: {
                cell3.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                break;
            }
            case 4: {
                cell4.setBackgroundColor(Color.RED);
                break;
            }
            case 5: {
                cell5.setBackgroundColor(Color.RED);
                break;
            }
            case 6: {
                cell6.setBackgroundColor(Color.RED);
                break;
            }
            case 7: {
                cell7.setBackgroundColor(Color.RED);
                break;
            }
            case 8: {
                cell8.setBackgroundColor(Color.RED);
                break;
            }
            case 9: {
                cell9.setBackgroundColor(Color.RED);
                break;
            }
            case 10: {
                cell10.setBackgroundColor(Color.RED);
                break;
            }
            case 11: {
                cell11.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                break;
            }
            case 12: {
                cell12.setBackgroundColor(Color.RED);
                break;
            }
            case 13: {
                cell13.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                break;
            }
            case 14: {
                cell14.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                break;
            }
            case 15: {
                cell15.setBackgroundColor(Color.RED);
                break;
            }
            case 16: {
                cell16.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                break;
            }
            case 0:{

                Toast.makeText(MainActivity.this, "Not Found", Toast.LENGTH_SHORT).show();
            }
        }
        lastcell=indexcell;
    }
}
