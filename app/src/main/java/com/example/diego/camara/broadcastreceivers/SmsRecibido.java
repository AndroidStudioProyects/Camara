package com.example.diego.camara.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.*;
import android.telephony.SmsMessage;
import android.util.Log;

import com.example.diego.camara.Actividades.MainActivity;
import com.example.diego.camara.Funciones.ConexionIP;
import com.example.diego.camara.Funciones.EnviarSMS;
import com.example.diego.camara.Services.ServicioGPS;

import java.io.DataOutputStream;
import java.lang.Process;


/**
 * Created by Diego on 14/05/2015.
 */
public class SmsRecibido extends BroadcastReceiver {
    Context contexto;
    EnviarSMS sms;
  //  Root BooteoRoot;
    ServicioGPS servicio;
    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences mispreferencias = context.getSharedPreferences("PreferenciasUsuario", Context.MODE_PRIVATE);
        String IP = mispreferencias.getString("edit_IP", "idirect.dlinkddns.com");
        String Id=mispreferencias.getString("IdRadio", "1");
        int Puerto = Integer.parseInt(mispreferencias.getString("edit_Port", "9001"));

        ConexionIP ClienteTCP = new ConexionIP(IP, Puerto, " "+Id+" 9");
        ClienteTCP.start();
        servicio=new ServicioGPS(contexto);

        final Bundle bundle = intent.getExtras();

        try {
            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();
                    if(phoneNumber.toString().equals("02235776581")) {
                        switch (message) {

                             case "Inicio":
                                Intent intento = new Intent(context, MainActivity.class);
                                intento.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intento.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                context.startActivity(intento);
                                sms = new EnviarSMS(context, "02235776581", "Solo Inicio de aplicacion");
                                sms.sendSMS();

                                ClienteTCP = new ConexionIP(IP, Puerto, " " + Id + " 19");
                                ClienteTCP.start();
                                break;

                            default:
                                break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);
        }
    }

private static void rebootDevice(){
    try{
        Process proceso=Runtime.getRuntime().exec("su");
        DataOutputStream os =new DataOutputStream(proceso.getOutputStream());
        os.writeBytes("reboot\n");
    }catch (Throwable t){
        t.printStackTrace();
    }


    }




}

