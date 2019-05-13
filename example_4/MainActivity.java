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
    private final String[][] trainingArray = new String[][]//static to determine where I am
            {
                    new String[] {"00:14:5c:8e:94:08","8c:68:c8:88:b7:ec","bc:1c:81:28:df:0a","8c:68:c8:88:b7:ed","70:3a:cb:f5:ec:1f","70:3a:cb:f5:ed:d3","70:3a:cb:f5:ec:1b","00:71:c2:0d:5a:29","70:3a:cb:f5:ed:d7","d4:6e:0e:36:e4:3d","30:b5:c2:22:d1:46","70:3a:cb:fa:c8:27","74:85:2a:18:5e:32","1c:74:0d:3b:98:16","00:0c:f6:cb:04:cc","00:0f:94:b3:cf:c4","d2:6e:0e:36:e4:3e","d4:6e:0e:36:e4:3e","70:3a:cb:fa:c8:23","64:d1:a3:26:90:7e","70:3a:cb:f5:eb:b1","88:03:55:ee:f9:4d","00:0f:94:b3:ca:74","80:2a:a8:d1:26:54","70:3a:cb:fa:c4:54","72:3a:cb:f9:15:15","bc:30:7e:38:ec:81","2a:a1:83:9b:2e:2b","72:3a:cb:f9:15:d9","74:a7:8e:e1:1d:f0","62:a7:8e:e1:1d:f1","00:0f:94:b3:cb:44","72:df::70:a3:68:5d","e8:df:70:a3:68:5c","dc:9f:db:64:2e:b8"},
                    new String[] {"00:14:5c:8e:94:08","8c:68:c8:88:b7:ec","bc:1c:81:28:df:0a","8c:68:c8:88:b7:ed","64:d1:a3:26:90:7e","00:71:c2:0d:5a:29","1c:74:0d:3b:98:16","00:0f:94:b3:cf:c4","30:b5:c2:22:d1:46","00:71:c2:0d:5c:c1","70:3a:cb:f5:ed:d7","80:2a:a8:d1:26:54","74:85:2a:18:5e:32","00:14:5c:97:8b:3e","62:a7:8e:e1:1d:f1","70:3a:cb:f5:ec:1f","00:18:4d:92:8d:c8","74:a7:8e:e1:1d:f0","00:0c:f6:cb:04:cc","70:3a:cb:f5:eb:b1","00:0f:94:b3:cb:44","88:03:55:ee:f9:4d","e8:df:70:a3:68:5c","d4:6e:0e:36:e4:3e","70:3a:cb:fa:c8:27","d2:6e:0e:36:e4:3e","72:df::70:a3:68:5d","fc:ec:da:8a:31:f6","00:0f:94:b3:ca:74","70:3a:cb:f5:ed:d3","70:3a:cb:f5:ec:1b","d4:6e:0e:36:e4:3d"},
                    new String[] {"00:14:5c:8e:94:08","bc:1c:81:28:df:0a","8c:68:c8:88:b7:ec","64:d1:a3:26:90:7e","8c:68:c8:88:b7:ed","70:3a:cb:fa:c8:27","00:71:c2:0d:5a:29","00:0f:94:b3:cf:c4","70:3a:cb:f5:ed:d7","1c:74:0d:3b:98:16","00:71:c2:0d:5c:c1","74:85:2a:18:5e:32","70:3a:cb:f5:ec:1f","30:b5:c2:22:d1:46"},
                    new String[] {"00:14:5c:8e:94:08","70:3a:cb:f5:ed:d7","8c:68:c8:88:b7:ec","64:d1:a3:26:90:7e","bc:1c:81:28:df:0a","e0:28:6d:a3:99:7b","70:3a:cb:fa:c8:27","94:6a:b0:d0:90:11","20:4e:7f:22:ef:dc","cc:ce:1e:e0:6e:a1","44:d9:e7:fd:f3:94","70:3a:cb:f5:ed:d3","70:3a:cb:fa:c4:54"}

            };
    private final Float[][] weights = new Float[][]//static to determine where I am
            {
                    new Float[]{(float)(0.25),(float)(0.25),(float)(0.25),(float)(0.25),(float)(0.2375),(float)(0.2375),(float)(0.2375),(float)(0.225),(float)(0.225),(float)(0.2),(float)(0.1875),(float)(0.1625),(float)(0.15),(float)(0.15),(float)(0.15),(float)(0.15),(float)(0.125),(float)(0.1125),(float)(0.0875),(float)(0.075),(float)(0.0625),(float)(0.0625),(float)(0.0625),(float)(0.05),(float)(0.025),(float)(0.025),(float)(0.025),(float)(0.025),(float)(0.025),(float)(0.0125),(float)(0.0125),(float)(0.0125),(float)(0.0125),(float)(0.0125),(float)(0.0125)},//332   35
                    new Float[]{(float)(0.25),(float)(0.25),(float)(0.25),(float)(0.25),(float)(0.25),(float)(0.225),(float)(0.225),(float)(0.2125),(float)(0.2125),(float)(0.2),(float)(0.1875),(float)(0.125),(float)(0.125),(float)(0.0875),(float)(0.0875),(float)(0.075),(float)(0.075),(float)(0.075),(float)(0.0625),(float)(0.05),(float)(0.05),(float)(0.05),(float)(0.0375),(float)(0.0375),(float)(0.0375),(float)(0.025),(float)(0.025),(float)(0.025),(float)(0.0125),(float)(0.0125),(float)(0.0125),(float)(0.0125),(float)0,(float)0,(float)0},//289
                    new Float[]{(float)(0.25),(float)(0.25),(float)(0.2125),(float)(0.2125),(float)(0.2125),(float)(0.175),(float)(0.1625),(float)(0.0875),(float)(0.0075),(float)(0.0625),(float)(0.0375),(float)(0.0375),(float)(0.0375),(float)(0.025),(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0},//147
                    new Float[]{(float)(0.25),(float)(0.25),(float)(0.2125),(float)(0.2125),(float)(0.1875),(float)(0.1),(float)(0.1),(float)(0.1),(float)(0.0875),(float)(0.0375),(float)(0.025),(float)(0.025),(float)(0.0125),(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0,(float)0}//128
             };
    private Integer[][] booleans = new Integer[][]//static to determine where I am
            {

                    new Integer[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                    new Integer[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                    new Integer[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                    new Integer[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
/*
                    new Integer[]{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    new Integer[]{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    new Integer[]{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    new Integer[]{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
  */          };




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

        try{
            //FileOutputStream fos = openFileOutput(file, Context.MODE_PRIVATE);
            filee.createNewFile();
            FileOutputStream fos = new FileOutputStream(filee);

            for (int j=0;j<scanResults.size();j++){
                ArrayList<String> temp= new ArrayList<String>(scanResults.get(j));
                int k=0;


                // Write results to a label
                for ( String scanResult : temp) {
                    Integer tempc=counters.get(k);
                    //fos.write(scanResult.getBytes());
                    fos.write(scanResult.getBytes());
                    fos.write("   ".getBytes());
                    fos.write(tempc.toString().getBytes());
                    fos.write("\n".getBytes());
                    k++;
                }

                fos.write("\n".getBytes());
                fos.write("\n".getBytes());

            }



            fos.close();
            Toast.makeText(MainActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
        }
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
        int counter =0;

        //What I need to do is to take as reference my array of scanResults and create a new array with results of booleans

        int numbelem = trainingArray.length;


        List<ScanResult> scanResults = wifiManager.getScanResults();

        for (int i=0; i < numbelem; i++){
            //1 to 4
            counter =0;

            for (String nameSSID:trainingArray[i]){

                for (ScanResult scanResult : scanResults) {
                    if (nameSSID.contentEquals(scanResult.BSSID)){
                        if (counter<35) {
                            booleans[i][counter] = 1;

                        }
                    };
                }
                counter = counter +1;
            }
        }
        saveFileMat(filename, booleans, weights);

        for (int i=0;i<numbelem;i++)for (int j=0;j<24;j++) booleans[i][j]=0;


    }
}