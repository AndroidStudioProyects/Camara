package com.example.diego.camara.Funciones;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.diego.camara.R;

/**
 * Created by Diego on 14/05/2015.
 */
public class Multimedia {
    MediaPlayer mp2,mp3,mp4,mp6,mp7,mp8,mp9;

       static String TAG="Camara";
    Context contexto;

int Alarma;

    public Multimedia(Context contexto,int Alarma){

        this.contexto=contexto;

        this.Alarma=Alarma;

        mp2= MediaPlayer.create(contexto, R.raw.alarmadeintrusion);
        mp3= MediaPlayer.create(contexto, R.raw.alarmadeapertura);
        mp4= MediaPlayer.create(contexto, R.raw.alarmadeenergia);
        mp6= MediaPlayer.create(contexto, R.raw.energiarestablecida);
        mp7= MediaPlayer.create(contexto, R.raw.sensoresactivados);
        mp8= MediaPlayer.create(contexto, R.raw.sensoresdesactivados);
        mp9= MediaPlayer.create(contexto, R.raw.personalnoautorizado);

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
                case 6:
                    mp6.start();
                    break;
                case 7:
                    mp7.start();
                    break;
                case 8:
                    mp8.start();
                    break;
                case 9:
                    mp9.start();
                    break;
                default:
                    break;
            }

    }



}
