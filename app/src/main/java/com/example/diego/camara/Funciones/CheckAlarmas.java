package com.example.diego.camara.Funciones;

import android.content.Context;
import android.util.Log;

/**
 * Created by Diego on 09/05/2015.
 */
public class CheckAlarmas implements Runnable {
    Multimedia Audio;
    String Alarma,IpPublica,msg;
    ConexionIP ClienteTCP;
    int Puerto,IdRadiobase;
    Context contex;
    static  String TAG="Camara";
    Boolean audioBool=false;

    public CheckAlarmas(int IdRadiobase,String Alarma,String IpPublica,int Puerto,Context contex,boolean audioBool){

        this.Alarma=Alarma;
        this.IpPublica=IpPublica;
        this.Puerto=Puerto;
        this.contex=contex;
        this.IdRadiobase=IdRadiobase;
        this.audioBool =audioBool;

    }

   public void run(){

       switch (Alarma){

           case "2":

               Audio =new Multimedia(contex,2);
               if(audioBool){Audio.AudioPlay();}
               Log.d(TAG, "Audio alarma Intrusion: ");
               break;
           case "3":
               Audio =new Multimedia(contex,3);
               if(audioBool){Audio.AudioPlay();}
               Log.d(TAG, "Audio alarma de apertura: ");
               break;
           case "4":
               Audio =new Multimedia(contex,4);
               if(audioBool){Audio.AudioPlay();}
               msg=Mensaje(IdRadiobase,4);
               ClienteTCP=new ConexionIP(IpPublica,Puerto,msg);
               ClienteTCP.start();
               Log.d(TAG, "Audio alarma en baterias:");
               break;
           case "6":
               Audio =new Multimedia(contex,6);
               if(audioBool){Audio.AudioPlay();}
               msg=Mensaje(IdRadiobase,5);
               ClienteTCP=new ConexionIP(IpPublica,Puerto,msg);
               ClienteTCP.start();
               Log.d(TAG, "Audio Sistema Energizado");
               break;

           case "14":
               Audio =new Multimedia(contex,7);
               if(audioBool){Audio.AudioPlay();}
               msg=Mensaje(IdRadiobase,14);
               ClienteTCP=new ConexionIP(IpPublica,Puerto,msg);
               ClienteTCP.start();
             Log.d(TAG, "Audio Sensores Activados");
               break;
           case "15":
               Audio =new Multimedia(contex,8);
               if(audioBool){Audio.AudioPlay();}
               msg=Mensaje(IdRadiobase,15);
               ClienteTCP=new ConexionIP(IpPublica,Puerto,msg);
               ClienteTCP.start();
               Log.d(TAG, "Audio Sensores Desactivados");
               break;

           case "25":
               Audio =new Multimedia(contex,9);
               if(audioBool){Audio.AudioPlay();}
               msg=Mensaje(IdRadiobase,25);
               ClienteTCP=new ConexionIP(IpPublica,Puerto,msg);
               ClienteTCP.start();
               Log.d(TAG, "Audio Personal no Autorizado");
               break;
           default:

               Log.d(TAG, "Alarma Default");
                    break;

       }
  Log.d(TAG, "Alarma: " + Alarma);

   }


    private String Mensaje(int IdRadiobase,int IdAlarma) {
        String msg=null;

        msg=" "+IdRadiobase+" "+IdAlarma;

        return msg;
    }
}
