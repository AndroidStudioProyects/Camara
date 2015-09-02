package com.example.diego.camara.Ftp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.diego.camara.Actividades.MainActivity;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


/**
 * Created by Diego on 26/05/2015.
 */
public class ConnectUploadAsync extends AsyncTask<Void,Integer,Boolean> {

    FTPClient mFtpClient;
    Context contexto;
    boolean status = false;
    boolean completed;
    boolean done;
    String Errores=null;
    File fileLast;
    String ip, userName,pass;
    MainActivity ac;

    public ConnectUploadAsync(Context contexto, String ip, String userName, String pass, MainActivity ac){

        this.contexto=contexto;
        this.ip=ip;
        this.userName=userName;
        this.pass=pass;

        this.ac=ac;


    }


    @Override
    protected Boolean doInBackground(Void... params) {

        try {
            mFtpClient = new FTPClient();
            mFtpClient.setConnectTimeout(10 * 1000);
            mFtpClient.connect(InetAddress.getByName(ip));
            status = mFtpClient.login(userName, pass);
            Log.d("Api FTP", "Se conecto: " + String.valueOf(status));
            if (FTPReply.isPositiveCompletion(mFtpClient.getReplyCode())) {
                mFtpClient.setFileType(FTP.BINARY_FILE_TYPE);
                mFtpClient.enterLocalPassiveMode();

                FTPFile[] mFileArray = mFtpClient.listFiles();

              //  Log.d("Api FTP", "Numeros de archivos" + String.valueOf(mFileArray.length));

                for(int i=0;i<mFileArray.length;i++){
               //     Log.d("Api FTP", "nombre archivo" + mFileArray[i].getName());
                  }

                Log.d("Api FTP", "IP Server:" + String.valueOf(mFtpClient.getRemoteAddress()));



                // APPROACH #2: uploads second file using an OutputStream

                //Defino la ruta donde busco los ficheros
                File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Radiobases/");
                //Creo el array de tipo File con el contenido de la carpeta
                File[] files = f.listFiles();

                for (int i = 0; i < files.length; i++)

                {
                    //Sacamos del array files un fichero
                    File file = files[i];

                    //Si es directorio...
              //     System.out.println("[" + i + "]" + file.getName());
                }

        try {  fileLast = files[files.length-1];
            Errores=null;

            float tamaño= (float)fileLast.length()/1024;
            System.out.println("Longitud en kbites" +tamaño+"kb");
            String NomFileaTransferir=fileLast.getName();
            System.out.println("fileLast: " +NomFileaTransferir+" Tamaño: "+(int)tamaño+"kb");
            File secondLocalFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"Radiobases/"+NomFileaTransferir);




            String secondRemoteFile =NomFileaTransferir;
            InputStream inputStream = new FileInputStream(secondLocalFile);

            System.out.println("Start uploading second file");

            OutputStream outputStream = mFtpClient.storeFileStream(secondRemoteFile);

            int ancho = (int) secondLocalFile.length();

            byte[] bytesIn = new byte[4096];
            float cantidad =(float)ancho/4096;
            int cant=(int)cantidad+1;
            int read = 0;
            int c=0;

            while ((read = inputStream.read(bytesIn)) != -1) {
                outputStream.write(bytesIn, 0, read);
                c++;
                float porcentage=(float)((c*100)/cant);
                publishProgress((int)porcentage);


            }

            inputStream.close();
            outputStream.close();

            completed = mFtpClient.completePendingCommand();
            if (completed) {
                System.out.println("The second file is uploaded successfully.");
                fileLast.delete();
                mFtpClient.disconnect();

            }


        }catch (Exception e){
            Errores="No hay Archivos";
            mFtpClient.disconnect();
        }

                ////////////////////////////

            }
        } catch (SocketException e) {
            e.printStackTrace();


        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return completed;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
       super.onProgressUpdate(values);

       ac.progressBar.setProgress(values[0]);
      ac.text_Bytes.setText("%" + values[0]);

    }

    @Override
    protected void onPostExecute(Boolean o) {
        super.onPostExecute(o);
        if(o){
            Toast.makeText(contexto, "archivo trasferido !!!" + o, Toast.LENGTH_SHORT).show();
            ac.progressBar.setProgress(0);
            ac.text_Bytes.setText("Transmision Finalizada");

        }
        else{
         Toast.makeText(contexto, "no se pudo transmitir" + o, Toast.LENGTH_SHORT).show();
                }
      //  Toast.makeText(contexto,"Errores:"+Errores,Toast.LENGTH_SHORT).show();
    }



}
