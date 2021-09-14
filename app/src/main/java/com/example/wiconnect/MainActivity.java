package com.example.wiconnect;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSuggestion;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WifiManager WiManage;
    private List<ScanResult> results;
    private String pass = "761f9ad9BA";

    TextView infor;
    Button escanear;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        escanear = findViewById(R.id.btnscan);
        infor = findViewById(R.id.tvinfo);

        escanear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infor.setText("");

                VerificarAdaptador();

                ScanWifi();
            }
        });

        WiManage = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        infor.setText("");

        VerificarAdaptador();
        
        ScanWifi();
    }

    private void VerificarAdaptador()
    {
        if(!WiManage.isWifiEnabled())
        {
            //Toast.makeText(getBaseContext(),"Activando adaptador wifi",Toast.LENGTH_SHORT).show();
            infor.setText(infor.getText()+"\nPaso 1: Activando adaptador wifi \n");
            Log.i("Paso 1:","Activando adaptador wifi");
            WiManage.setWifiEnabled(true);
        }
        else
        {
            infor.setText(infor.getText()+"Paso 1: Adaptador Wifi activado! \n");
            Log.i("\nPaso 1:","Verificando Adaptador Wifi!");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void connectNet()
    {
        final WifiNetworkSuggestion suggestion1 =
                new WifiNetworkSuggestion.Builder()
                        .setSsid("test111111")
                        .setIsAppInteractionRequired(true) // Optional (Needs location permission)
                        .build();

        final WifiNetworkSuggestion suggestion2 =
                new WifiNetworkSuggestion.Builder()
                        .setSsid("test222222")
                        .setWpa2Passphrase(pass)
                        .setIsAppInteractionRequired(true) // Optional (Needs location permission)
                        .build();

        final WifiNetworkSuggestion suggestion3 =
                new WifiNetworkSuggestion.Builder()
                        .setSsid("test333333")
                        .setWpa3Passphrase(pass)
                        .setIsAppInteractionRequired(true) // Optional (Needs location permission)
                        .build();

        final List<WifiNetworkSuggestion> suggestionsList =
                new ArrayList<WifiNetworkSuggestion> ();
        suggestionsList.add(suggestion1);
        suggestionsList.add(suggestion2);
        suggestionsList.add(suggestion3);



        final int status = WiManage.addNetworkSuggestions(suggestionsList);
        if (status != WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
// do error handling here…
        }

// Optional (Wait for post connection broadcast to one of your suggestions)
        final IntentFilter intentFilter =
                new IntentFilter(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION);

        final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!intent.getAction().equals(
                        WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION)) {
                    return;
                }
                // do post connect processing here...
            }
        };
        getApplicationContext().registerReceiver(broadcastReceiver, intentFilter);

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void ScanWifi()
    {
        registerReceiver(wifiReceiver,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        WiManage.startScan();
        infor.setText(infor.getText()+"Paso 2: Escaneando perfiles de red! \n");
        //Toast.makeText(getBaseContext(),"Escaneando perfiles de red",Toast.LENGTH_SHORT).show();
        Log.i("Paso 2:","Escaneando perfiles de red");

    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            results = WiManage.getScanResults();
            unregisterReceiver(this);

            String perfiles = "";
            int index=1;
            for(ScanResult scanresu:results)
            {
                perfiles += "("+index+") "+scanresu.SSID + " - " + scanresu.capabilities + " - " + scanresu.BSSID + ",\n";
                index++;
            }

            //Toast.makeText(getBaseContext(),perfiles,Toast.LENGTH_SHORT).show();
            infor.setText(infor.getText()+"\nPerfiles ["+results.size()+"]:\n"+perfiles + " \n\n");
            Log.i("Perfiles de red["+results.size()+"]->",perfiles);

            if(results.size()>0)
            {
                //Toast.makeText(getBaseContext(),"Realizando intentos de conexion!",Toast.LENGTH_SHORT).show();
                infor.setText(infor.getText()+"Paso 3: Realizando intentos de conexion! \n");
                Log.i("Paso 3:","Realizando intentos de conexion!");
                //connectNet();
            }
        }
    };
}