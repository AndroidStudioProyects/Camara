package com.example.diego.camara.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.diego.camara.Actividades.MainActivity;
import com.example.diego.camara.Funciones.ConexionIP;
import com.example.diego.camara.Funciones.EnviarSMS;

/**
 * Created by Diego on 30/04/2015.
 */
public class Booteo extends BroadcastReceiver {
EnviarSMS sms;

    @Override
    public void onReceive(Context context, Intent intent) {
        sms=new EnviarSMS(context,"2235776581","SmartPhone Reiniciado");
        sms.sendSMS();
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        Intent intento= new Intent(context,MainActivity.class);
        intento.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intento.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(intento);


        SharedPreferences mispreferencias=context.getSharedPreferences("PreferenciasUsuario", Context.MODE_PRIVATE);
        String IP=mispreferencias.getString("edit_IP", "idirect.dlinkddns.com");
        String Id=mispreferencias.getString("IdRadio", "1");
        int Puerto= Integer.parseInt(mispreferencias.getString("edit_Port", "9001"));

        ConexionIP ClienteTCP=new ConexionIP(IP,Puerto," "+Id+" 13");
        ClienteTCP.start();
        sms=new EnviarSMS(context,"02235776581","Reseteo completado");
        sms.sendSMS();

    }
}
