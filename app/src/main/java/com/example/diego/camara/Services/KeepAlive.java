package com.example.diego.camara.Services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import com.example.diego.camara.Services.ServicioGPS;
import com.example.diego.camara.Funciones.ConexionIP;


/**
 * Created by Diego on 09/05/2015.
 */
public class KeepAlive  extends Service {
    static ConexionIP ClienteTCP;
    static int IdRadiobase, TiempoSeg,PuertoKA;
    static String IpPublica;
    static String TAG = "Camara";
    static boolean Bool=true;
    Intent intento;
    Hilo hilito;
    static ServicioGPS geoloc;
    static String geo;
    static String Level,Voltage,Temperature,Status,Health;
   //static ServicioGPS servicio;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences mispreferencias=getSharedPreferences("PreferenciasUsuario", Context.MODE_PRIVATE);
         this.intento=intent;
        Bool=true;
        IpPublica = mispreferencias.getString("edit_IP", "idirect.dlinkddns.com");
        IdRadiobase = Integer.parseInt(mispreferencias.getString("IdRadio", "1"));
        PuertoKA=Integer.parseInt(mispreferencias.getString("edit_PortKA", "9002"));
        TiempoSeg = Integer.parseInt(mispreferencias.getString("edit_TimerKA", "10"));
        hilito=new Hilo();
        hilito.start();
        //geo=geoloc.LatyLong();
       Toast.makeText(getApplicationContext(), "Servicio Keep Alive iniciado: ", Toast.LENGTH_SHORT).show();
       // Log.d(TAG, "OnStart Keep Alive Bool: "+Bool);

      //  this.registerReceiver(this.myBatteryReceiver,
       //         new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

      return START_STICKY;
    }

    @Override
    public void onDestroy() {

        Bool = false;
        Log.d(TAG, "KeepAlive Destroy bool: "+Bool);
        Toast.makeText(getApplicationContext(), "Servicio detenido: " + Bool, Toast.LENGTH_SHORT).show();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public static class Hilo extends Thread {

        @Override
        public void run() {
            Log.d(TAG, "KeepAlive run ingreso bool: "+Bool);

            while(Bool){

            try {
                Thread.sleep(TiempoSeg * 1000);
                //ClienteTCP=new ConexionIP(IpPublica,PuertoKA," "+IdRadiobase+" 1 "+Level+" "+Voltage+" "+Temperature+" "+Status+" "+Health+" "+"-37.8669982,-58.0802339");
               // Log.d(TAG, "\nKeep Alive !! IpServer: " + IpPublica + " Puerto: " + PuertoKA + " TiempoSeg: " + TiempoSeg+" IdRadiobase: " + IdRadiobase);
                ClienteTCP=new ConexionIP(IpPublica,PuertoKA," "+IdRadiobase+" 1 - - - - - -");
                Log.d(TAG, "\nKeep Alive !! IpServer: " + IpPublica + " Puerto: " + PuertoKA + " TiempoSeg: " + TiempoSeg+" IdRadiobase: " + IdRadiobase);


                ClienteTCP.start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }}
          //  Log.d(TAG, "KeepAlive run salida bool: "+Bool);
        }
    }


/*
    private BroadcastReceiver myBatteryReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub

            if (arg1.getAction().equals(Intent.ACTION_BATTERY_CHANGED)){
                Level=String.valueOf(arg1.getIntExtra("level", 0)) + "%";
                Voltage=String.valueOf((float)arg1.getIntExtra("voltage", 0)/1000) + "V";
                Temperature=String.valueOf((float)arg1.getIntExtra("temperature", 0)/10) + "c";


                int status = arg1.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN);
                String strStatus;
                if (status == BatteryManager.BATTERY_STATUS_CHARGING){
                    strStatus = "Charging";
                } else if (status == BatteryManager.BATTERY_STATUS_DISCHARGING){
                    strStatus = "Dis-charging";
                } else if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING){
                    strStatus = "Not charging";
                } else if (status == BatteryManager.BATTERY_STATUS_FULL){
                    strStatus = "Full";
                } else {
                    strStatus = "Unknown";
                }
                Status=strStatus;

                int health = arg1.getIntExtra("health", BatteryManager.BATTERY_HEALTH_UNKNOWN);
                String strHealth;
                if (health == BatteryManager.BATTERY_HEALTH_GOOD){
                    strHealth = "Good";
                } else if (health == BatteryManager.BATTERY_HEALTH_OVERHEAT){
                    strHealth = "Over Heat";
                } else if (health == BatteryManager.BATTERY_HEALTH_DEAD){
                    strHealth = "Dead";
                } else if (health == BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE){
                    strHealth = "Over Voltage";
                } else if (health == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE){
                    strHealth = "Unspecified Failure";
                } else{
                    strHealth = "Unknown";
                }
                Health=strHealth;

            }
        }

    };*/



}
