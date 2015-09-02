package com.example.diego.camara.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.diego.camara.Funciones.ConexionIP;


/**
 * Created by Diego on 30/04/2015.
 */
public class SistemaEnBateria extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {



        SharedPreferences mispreferencias=context.getSharedPreferences("PreferenciasUsuario", Context.MODE_PRIVATE);
        String IP=mispreferencias.getString("edit_IP", "localhost");

        int Puerto= Integer.parseInt(mispreferencias.getString("edit_Port", "9001"));

        Toast.makeText(context, "Sistema Sobre Baterias", Toast.LENGTH_SHORT).show();
    /*   SmsManager manager = SmsManager.getDefault();
        PendingIntent sentIntent = PendingIntent.getActivity(contexto, 0, new Intent(), 0);
        PendingIntent deliveryIntent = PendingIntent.getActivity(contexto, 0, new Intent(), 0);*/
        //   manager.sendTextMessage("2235776581", null, "Sistema en Baterias", sentIntent, deliveryIntent);



        ConexionIP ClienteTCP =new ConexionIP(IP,Puerto," 1 5");
        ClienteTCP.start();


    }


}
