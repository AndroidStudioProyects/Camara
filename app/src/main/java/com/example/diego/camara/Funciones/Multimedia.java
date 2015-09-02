package com.example.diego.camara.Funciones;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.diego.camara.R;

/**
 * Created by Diego on 14/05/2015.
 */
public class Multimedia {
    MediaPlayer mp2,mp3,mp4;

       static String TAG="USB_ARDUINO";
    Context contexto;

int Alarma;

    public Multimedia(Context contexto,int Alarma){

        this.contexto=contexto;

        this.Alarma=Alarma;

        mp2= MediaPlayer.create(contexto, R.raw.alarmadeintrusion);
        mp3= MediaPlayer.create(contexto, R.raw.alarmadeapertura);
        mp4= MediaPlayer.create(contexto, R.raw.alarmadeenergia);


    }


    public void AudioPlay(){


            switch (Alarma) {
                case 2:

                    mp2.start();
                    break;
                case 3:

                    mp3.start();
                    break;
                case 4:
                    mp4.start();
                    break;
                default:
                    break;
            }

    }



}
