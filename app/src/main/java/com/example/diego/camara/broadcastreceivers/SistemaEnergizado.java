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
public class SistemaEnergizado extends BroadcastReceiver {
    Context contexto;
CheckAlarmas alarmas;
    ConexionIP ClienteTCP;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.contexto = context;

        SharedPreferences mispreferencias = contexto.getSharedPreferences("PreferenciasUsuario", Context.MODE_PRIVATE);
        int IdRadiobase = mispreferencias.getInt("IdRadiobase", 1);
        String IP = mispreferencias.getString("edit_IP", "localhost");
        int Puerto = Integer.parseInt(mispreferencias.getString("edit_Port", "9001"));
        Boolean audioBool=mispreferencias.getBoolean("audioBool",true);
       // Toast.makeText(contexto, "Sistema Energizado", Toast.LENGTH_SHORT).show();

        ClienteTCP = new ConexionIP(IP, Puerto, " 1 6");
        ClienteTCP.start();
        alarmas = new CheckAlarmas(IdRadiobase, "6", IP, Puerto, context,audioBool);
        alarmas.run();
    }
}
