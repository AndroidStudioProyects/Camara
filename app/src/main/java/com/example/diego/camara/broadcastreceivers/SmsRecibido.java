package com.example.diego.camara.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.example.diego.camara.Funciones.ConexionIP;
import com.example.diego.camara.Funciones.EnviarSMS;


/**
 * Created by Diego on 14/05/2015.
 */
public class SmsRecibido extends BroadcastReceiver {
   // Context contexto;

    @Override
    public void onReceive(Context context, Intent intent) {
       // this.contexto=context;
      //  Toast.makeText(context, "Sms Recibido", Toast.LENGTH_SHORT).show();

        SharedPreferences mispreferencias=context.getSharedPreferences("PreferenciasUsuario", Context.MODE_PRIVATE);
        String IP=mispreferencias.getString("edit_IP", "idirect.dlinkddns.com");

        int Puerto= Integer.parseInt(mispreferencias.getString("edit_Port", "9001"));

        ConexionIP ClienteTCP=new ConexionIP(IP,Puerto," 1 9");
        ClienteTCP.start();

        Bundle b = intent.getExtras();

        if (b != null) {
            Object[] pdus = (Object[]) b.get("pdus");

            SmsMessage[] mensajes = new SmsMessage[pdus.length];

            for (int i = 0; i < mensajes.length; i++) {
                mensajes[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);

                String idMensaje = mensajes[i].getOriginatingAddress();
                String textoMensaje = mensajes[i].getMessageBody();

                Toast.makeText(context,"SMS:"+textoMensaje,Toast.LENGTH_SHORT).show();
                EnviarSMS sms=new EnviarSMS(context,idMensaje,"Mensaje: "+textoMensaje);
                sms.sendSMS();

                Log.d("Camara", "Remitente: " + idMensaje);
                Log.d("Camara", "Mensaje: " + textoMensaje);
            }
        }

    }


}
