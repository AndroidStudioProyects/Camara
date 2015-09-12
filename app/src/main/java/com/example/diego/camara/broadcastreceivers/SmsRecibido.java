package com.example.diego.camara.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.example.diego.camara.Actividades.MainActivity;
import com.example.diego.camara.Funciones.ConexionIP;
import com.example.diego.camara.Funciones.EnviarSMS;


/**
 * Created by Diego on 14/05/2015.
 */
public class SmsRecibido extends BroadcastReceiver {
   Context contexto;
   EnviarSMS sms;
    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences mispreferencias=context.getSharedPreferences("PreferenciasUsuario", Context.MODE_PRIVATE);
        String IP=mispreferencias.getString("edit_IP", "idirect.dlinkddns.com");

        int Puerto= Integer.parseInt(mispreferencias.getString("edit_Port", "9001"));

        ConexionIP ClienteTCP=new ConexionIP(IP,Puerto," 1 9");
        ClienteTCP.start();

        final Bundle bundle = intent.getExtras();

        try {
            if(bundle != null){

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for(int i = 0; i < pdusObj.length; i++){
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();
                    int Comando = 0;
                    try{
                        Comando=Integer.parseInt(message);

                    }catch (Exception e){

                        Toast.makeText(context,"Mensaje mal escrito", Toast.LENGTH_SHORT).show();
                    }

                    switch (Comando){

                        case 20:
                            sms=new EnviarSMS(context,phoneNumber,"Solicitud de inicio de aplicacion:ok");
                            sms.sendSMS();
                            Intent intentoStart= new Intent(context,MainActivity.class);
                            intentoStart.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intentoStart.addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                            context.startActivity(intentoStart);
                            break;
                        case 21:
                            sms=new EnviarSMS(context,phoneNumber,"Solicitud de Cierre de Aplicacion:ok");
                            sms.sendSMS();
                            Intent intentoStop = new Intent(context,MainActivity.class);
                            intentoStop.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intentoStop.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            context.startActivity(intentoStop);
                                      // System.exit(0);
                            break;
                    }

                }
            }
        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);
        }
        }

    }



