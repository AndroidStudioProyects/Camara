package com.example.diego.camara.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

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



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        this.intento=intent;
        Bool=true;
        IpPublica = intento.getExtras().getString("Ip");
        IdRadiobase = intento.getExtras().getInt("Id");
        PuertoKA=intento.getExtras().getInt("PuertoKA");
       // Bool = intento.getExtras().getBoolean("boolKA");
        TiempoSeg = intento.getExtras().getInt("Timer");
        hilito=new Hilo();
        hilito.start();
        Toast.makeText(getApplicationContext(), "Servicio Keep Alive iniciado", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "OnStart Keep Alive Bool: "+Bool);
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
                ClienteTCP=new ConexionIP(IpPublica,PuertoKA," "+IdRadiobase+" 1");
                Log.d(TAG, "\nKeep Alive !! IpServer: " + IpPublica + " Puerto: " + PuertoKA + " TiempoSeg: " + TiempoSeg+" IdRadiobase: " + IdRadiobase);
                ClienteTCP.start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }}
          //  Log.d(TAG, "KeepAlive run salida bool: "+Bool);
        }
    }
}