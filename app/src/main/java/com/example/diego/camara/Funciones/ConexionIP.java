package com.example.diego.camara.Funciones;


import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Diego on 09/05/2015.
 */
public class ConexionIP extends Thread {


    int Puerto ;
    String msg,Ip;

    String TAG="ConexionIP";

    public ConexionIP(String Ip,int Puerto,String msg) {

        this.Ip = Ip;
        this.msg = msg;
        this.Puerto = Puerto;
    }


    public void run() {

        try {
            Socket socket;
            //Create a client socket and define internet address and the port of the server
            socket = new Socket(Ip,Puerto);
            //Get the input stream of the client socket
        //    InputStream is = socket.getInputStream();
            //Get the output stream of the client socket
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            //Write data to the output stream of the client socket
          out.println(msg);// Id REadiobase + Id alarma

            //Buffer the data coming from the input stream
          //  final BufferedReader br = new BufferedReader(
            //        new InputStreamReader(is));

            socket.close();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Log.d(TAG, "NumberFormatException " + e);


        } catch (UnknownHostException e) {

            e.printStackTrace();
            Log.d(TAG, "UnknownHostException " + e);
        } catch (IOException e) {

            e.printStackTrace();
            Log.d(TAG, " IOException " + e);
        }


    }
}