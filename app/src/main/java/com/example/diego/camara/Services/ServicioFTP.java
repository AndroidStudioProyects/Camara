package com.example.diego.camara.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.example.diego.camara.Ftp.ConnectUploadAsyncService;

/**
 * Created by diego on 23/09/15.
 */
public class ServicioFTP extends Service {

   public  Context ctx;
    static Boolean   Bool = false;

    public ConnectUploadAsyncService cliente;
    public ServicioFTP() {
        super();
        this.ctx = this.getApplicationContext();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bool = true;
        Hilo funcion=new Hilo();
        funcion .start();

        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Bool = false;

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    public void EnviarFTP(){


        String ip="idirect.dlinkddns.com";
        String userName="idirect";
        String pass="IDIRECT";

        cliente = new ConnectUploadAsyncService(getApplicationContext(),ip,userName,pass);
        cliente.execute();
    }

    public class Hilo extends Thread {

        @Override
        public void run() {
              while(Bool){

                  EnviarFTP();
                try {
                    Thread.sleep(10000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }}
            //  Log.d(TAG, "KeepAlive run salida bool: "+Bool);
        }
    }


}

