package com.example.diego.camara.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.diego.camara.Funciones.ConexionIP;


/**
 * Created by Diego on 09/05/2015.
 */
public class KeepAlive  extends Service{
    static ConexionIP ClienteTCP;
    static int IdRadiobase, TiempoSeg,PuertoKA;
    static String IpPublica;
    static String TAG = "Camara";
    static boolean Bool=true;
    Intent intento;
    Hilo hilito;
    static ServicioGPS geoloc;
    public static String Lat="-,",Long="-";
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
                ClienteTCP=new ConexionIP(IpPublica,PuertoKA," "+IdRadiobase+" 1 - - - - - "+Lat+Long);
                Log.d(TAG, "\nKeep Alive !! IpServer: " + IpPublica + " Puerto: " + PuertoKA + " TiempoSeg: " + TiempoSeg+" IdRadiobase: " + IdRadiobase);


                ClienteTCP.start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }}
          //  Log.d(TAG, "KeepAlive run salida bool: "+Bool);
        }
    }





}
