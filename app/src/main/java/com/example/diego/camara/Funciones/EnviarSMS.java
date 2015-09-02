package com.example.diego.camara.Funciones;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

/**
 * Created by Diego on 10/05/2015.
 */
public class EnviarSMS  {
    Context contexto;
    String phoneNumber, message;

    public EnviarSMS(Context contexto,String phoneNumber, String message){

        this.contexto=contexto;
        this.phoneNumber=phoneNumber;
        this.message=message;




    }

    public void sendSMS() {

        SmsManager manager = SmsManager.getDefault();
        PendingIntent sentIntent = PendingIntent.getActivity(contexto, 0, new Intent(), 0);
        PendingIntent deliveryIntent = PendingIntent.getActivity(contexto, 0, new Intent(), 0);
        manager.sendTextMessage(phoneNumber, null, message, sentIntent, deliveryIntent);

    }

}
