package com.example.diego.camara.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.example.diego.camara.Funciones.ConexionIP;
import com.example.diego.camara.Funciones.EnviarSMS;
import com.example.diego.camara.Root.Root;


/**
 * Created by Diego on 14/05/2015.
 */
public class SmsRecibido extends BroadcastReceiver {
    Context contexto;
    EnviarSMS sms;
    Root BooteoRoot;
    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences mispreferencias = context.getSharedPreferences("PreferenciasUsuario", Context.MODE_PRIVATE);
        String IP = mispreferencias.getString("edit_IP", "idirect.dlinkddns.com");

        int Puerto = Integer.parseInt(mispreferencias.getString("edit_Port", "9001"));

        ConexionIP ClienteTCP = new ConexionIP(IP, Puerto, " 1 9");
        ClienteTCP.start();

        final Bundle bundle = intent.getExtras();

        try {
            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();

                    switch (message) {

                    case "Reboot":

                        BooteoRoot =    new Root().setContext(contexto);
                        BooteoRoot.execute("reboot");
                            break;
                    }

                }
            }
        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);
        }
    }



}

