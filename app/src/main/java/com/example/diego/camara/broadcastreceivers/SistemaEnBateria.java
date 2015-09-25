package com.example.diego.camara.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.diego.camara.Funciones.CheckAlarmas;
import com.example.diego.camara.Funciones.ConexionIP;


/**
 * Created by Diego on 30/04/2015.
 */
public class SistemaEnBateria extends BroadcastReceiver {

ConexionIP ClienteTCP;
    CheckAlarmas alarmas;

    @Override
    public void onReceive(Context context, Intent intent) {


        SharedPreferences mispreferencias=context.getSharedPreferences("PreferenciasUsuario", Context.MODE_PRIVATE);
        String IP=mispreferencias.getString("edit_IP", "idirect.dlinkddns.com");

        int Puerto= Integer.parseInt(mispreferencias.getString("edit_Port", "9001"));
        int IdRadiobase=mispreferencias.getInt("edi", 1);
        Boolean audioBool=mispreferencias.getBoolean("audioBool",true);
     //   Toast.makeText(context, "Sistema Sobre Baterias", Toast.LENGTH_SHORT).show();
        ClienteTCP = new ConexionIP(IP, Puerto, " 1 4");
        ClienteTCP.start();
       alarmas = new CheckAlarmas(IdRadiobase, "4", IP, Puerto, context,audioBool);
        alarmas.run();


    }


}
