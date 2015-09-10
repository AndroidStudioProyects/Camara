package com.example.diego.camara.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.diego.camara.Actividades.MainActivity;
import com.example.diego.camara.Funciones.ConexionIP;

/**
 * Created by Diego on 30/04/2015.
 */
public class Booteo extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Intent intento= new Intent(context,MainActivity.class);
        intento.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intento);

        SharedPreferences mispreferencias=context.getSharedPreferences("PreferenciasUsuario", Context.MODE_PRIVATE);
        String IP=mispreferencias.getString("edit_IP", "idirect.dlinkddns.com");

        int Puerto= Integer.parseInt(mispreferencias.getString("edit_Port", "9001"));

        ConexionIP ClienteTCP=new ConexionIP(IP,Puerto," 1 7");
        ClienteTCP.start();

    }
}
