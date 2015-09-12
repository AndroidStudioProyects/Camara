package com.example.diego.camara.Actividades;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.diego.camara.Ftp.ConnectUploadAsync;
import com.example.diego.camara.Funciones.CheckAlarmas;
import com.example.diego.camara.Funciones.ConexionIP;
import com.example.diego.camara.Funciones.EnviarSMS;
import com.example.diego.camara.R;
import com.example.diego.camara.Services.KeepAlive;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

// sms
    EnviarSMS sms;
            //   BLUETOOTH !

    private StringBuilder sb= new StringBuilder();
    final int RECIEVE_MESSAGE=1;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    private Handler h;
    ConnectedThread mConnectedThread;
    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static boolean BoolFoto=false;
    // MAC-address of Bluetooth module (you must edit this line)
    //private static String address = "00:14:01:06:13:29";

    //Linvor Bluetooth
    private static String address = "00:12:12:04:41:11";
    public static String Alarmabluetooth="0";
    // BLUETOOTH FIN

    ToggleButton toggleAudio, toggle_ka;
    Switch switch_muteAlarm;
    static EditText textOut,edit_IP,edit_Port,edit_IdRadio,textIn,edit_TimerKA,edit_PortKA,edit_DuracionVideo;
    Button buttonSend, btn_Prueba, btn_Foto, btn_Video, btn_Intrusion,btn_USB;
    Button btn_Energia,btn_Apertura,btn_Conf_FTP,btn_Enviar_FTP;
    public TextView textAlarma1,text_Bytes;
    public ProgressBar progressBar;

    String stringToRx;
    private FrameLayout preview;
    private SurfaceView mPreview;
    Boolean isRecording = false;
    Boolean MuteAlarm=false;
    private Camera mCamera;
    private MediaRecorder mMediaRecorder;
    Camera.Parameters parameters;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private int calidadFoto = 90;
    public static Boolean Bandera=false;
    static final String TAG = "Camara";
    boolean audioBool=false;
     public static int  IdRadiobase=0;
    Intent intentKeepAlive;
    public ConnectUploadAsync cliente;
    String IpPublica;
    CheckAlarmas alarmas;

    public static final String Diego="2235776581";

    private BroadcastReceiver SmsRecibido ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate inicio");

        setContentView(R.layout.activity_main);
        LevantarXML();
        Botones();


        IdRadiobase = Integer.parseInt(edit_IdRadio.getText().toString());
        IpPublica=edit_IP.getText().toString();
  //      BotonesEnabled(false);
        CAMARA_ON();
        ////defino bluetooth adapter
        btAdapter=BluetoothAdapter.getDefaultAdapter();
        checkBTState();//Checkeo el estado

/// BroadcastReceiver  Bluettoth

        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        this.registerReceiver(mReceiver, filter1);
        this.registerReceiver(mReceiver, filter2);
        this.registerReceiver(mReceiver, filter3);

        BroadcastSMS();

        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RECIEVE_MESSAGE:                                                   // if receive massage
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);                 // create string from bytes array
                        sb.append(strIncom);                                                // append string
                        int endOfLineIndex = sb.indexOf("\r\n");                            // determine the end-of-line
                        if (endOfLineIndex > 0) {                                            // if end-of-line,

                            String sbprint = sb.substring(0, endOfLineIndex);               // extract string
                            Toast.makeText(getBaseContext(),"Alarma: '"+sbprint+"'",Toast.LENGTH_LONG).show();

                            Alarmabluetooth=sbprint;
                            sb.delete(0, sb.length());                                      // and clear
                            Log.d(TAG, "Alarma recibida: "+Alarmabluetooth);

                            if(!MuteAlarm){
                            alarmas=new CheckAlarmas(IdRadiobase, "2",IpPublica, 9001, getApplicationContext(),audioBool);
                            alarmas.run();
                            Filmacion();

                            }

                        }
                     //   Log.d(TAG, "...String:"+ sb.toString() +  "Byte:" + msg.arg1 + "...");
                        break;
                }
            }
        };


        new conetarBluetooth().run();

    }

    @Override
    public void onResume() {
        super.onResume();
       new conetarBluetooth().run();
        Log.d(TAG, "OnResume ");
        CargarPreferencias();
        mCamera.startPreview();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "OnStop");
        GuardarPreferencias();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(SmsRecibido);
        ConexionIP ClienteTCP=new ConexionIP(IpPublica,9001," 1 17");
        ClienteTCP.start();
        Log.d(TAG, "OnDestroy");
        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event
        MuteAlarm=false;
        /// bluetooth

        //
        if (outStream != null) {
            try {
                outStream.flush();
            } catch (IOException e) {
                errorExit("Fatal Error", "In onPause() and failed to flush output stream: " + e.getMessage() + ".");
            }
        }

        //intenta cerrar la comunicacion
        try     {
            btSocket.close();
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
    }

    private void CAMARA_ON() {
       mCamera = getCameraInstance();
       mPreview = new CameraPreview(getApplicationContext(), mCamera);
       preview.addView(mPreview);
        mCamera.startPreview();

    }

    /////////////            Bluettohhh     /////////

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection",e);
            }
        }
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if(btAdapter==null) {
            errorExit("Fatal Error", "Bluetooth not support");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }

    }

    private void errorExit(String title, String message){
        finish();
    }

    private class ConnectedThread extends Thread {

        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);        // Get number of bytes and message in "buffer"
                    h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();     // Send to message queue Handler
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String message) {
            Log.d(TAG, "...Data to send: " + message + "...");
            byte[] msgBuffer = message.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                Log.d(TAG, "...Error data send: " + e.getMessage() + "...");
            }
        }
    }

    ///////////////7//   //////////////////
    private void Botones() {

        switch_muteAlarm.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    Log.d(TAG, "Sensores Desactivados");
                    MuteAlarm=true;
                    alarmas=new CheckAlarmas(IdRadiobase, "8",IpPublica, 9001, getApplicationContext(),audioBool);
                    alarmas.run();
                    sms=new EnviarSMS(getApplicationContext(),Diego,"Sensores Desactivados");
                    sms.sendSMS();

              } else {
                    MuteAlarm=false;
                    Log.d(TAG, "Sensores Activados");
                    alarmas=new CheckAlarmas(IdRadiobase, "7",IpPublica, 9001, getApplicationContext(),audioBool);
                    alarmas.run();
                    sms=new EnviarSMS(getApplicationContext(),Diego,"Sensores Activados");
                    sms.sendSMS();
                    }
                GuardarPreferencias();
            }
        });


        btn_Enviar_FTP.setOnClickListener(new OnClickListener() {

            String ip=edit_IP.getText().toString();
            String userName="idirect";
            String pass="IDIRECT";

            @Override
            public void onClick(View v) {

        cliente = new ConnectUploadAsync(getApplicationContext(),ip,userName,pass, MainActivity.this);
               cliente.execute();

            }
        });

        btn_Conf_FTP.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intento=new Intent(getApplicationContext(),Lay_Ftp.class);

                startActivity(intento);

            }
        });

        btn_USB.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
               // new conetarBluetooth().run();
                new conetarBluetooth().run();

            }
        });

        btn_Foto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                            Log.d(TAG, "Boton de Foto");
            mCamera.takePicture(null,null,mPicture);

            }


        });

        btn_Video.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               Filmacion();

                Log.d(TAG, "Boton de Video");
            }
        });

        btn_Prueba.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

              Toast.makeText(getApplicationContext(),"Conexion?="+ btSocket.isConnected(),Toast.LENGTH_LONG).show();


            }
        });

        btn_Intrusion.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "Alarma Intrusion");
          textIn.setText("2");


            }
        });
        btn_Apertura.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.d(TAG, "Alarma de Apertura");
                textIn.setText("3");

            }
        });
        btn_Energia.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

          Log.d(TAG, "Alarma de Energia");
                textIn.setText("4");


            }
        });

        buttonSend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final String textToSend = textOut.getText().toString();
                if (textToSend != "") {
                    stringToRx = "";
                    textIn.setText("");

                }

            }
        });
        toggleAudio.setOnCheckedChangeListener(new OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                audioBool=isChecked;
                if(isChecked){
                    Log.d(TAG, "Audio ON");
                    Toast.makeText(getApplicationContext(), "Audio ON", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Audio ON", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Audio Off");
                }
            }
        });
        toggle_ka.setOnCheckedChangeListener(new OnCheckedChangeListener() {



            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if(isChecked){
                    GuardarPreferencias();
                    IdRadiobase= Integer.parseInt(edit_IdRadio.getText().toString());
                    IpPublica=edit_IP.getText().toString();

                    intentKeepAlive=new Intent(getApplicationContext(), KeepAlive.class);
                     intentKeepAlive.putExtra("bool", true);
                     startService(intentKeepAlive);
                    BotonesEnabled(isChecked);
                }else{

                    stopService(intentKeepAlive);
                    edit_TimerKA.setEnabled(true);
                     BotonesEnabled(isChecked);
                }

            }
        });

        textIn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG, "Cambio el texto: " + s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                Log.d(TAG, "Tesxtin: " + textIn.getText().toString());
                if(!textIn.getText().toString().equals("F")) {
                    Log.d(TAG, "Alarmeta");
                    String IP = edit_IP.getText().toString();
                    int Port = Integer.parseInt(edit_Port.getText().toString());
                    int IdRadiobase = Integer.parseInt(edit_IdRadio.getText().toString());
                    Log.d(TAG, "IdRadiobase:" + IdRadiobase);
                    String Alarma = textIn.getText().toString();
                    alarmas= new CheckAlarmas(IdRadiobase, Alarma, IP, Port, getApplicationContext(),audioBool);
                    alarmas.run();
                    textIn.setText("F");
                }
            }
        });
    }
/*
    public void Filmacion() {
        if (isRecording) {
            // stop recording and release camera
            mMediaRecorder.stop();  // stop the recording
            releaseMediaRecorder(); // release the MediaRecorder object
            mCamera.lock();         // take camera access back from MediaRecorder

            EnviarFTP();
            // inform the user that recording has stopped
            btn_Video.setText("Rec");
            isRecording = false;
            Log.d(TAG, "Filmacion Detenida");

        } else {
            if (prepareVideoRecorder()) {
                // Camera is available and unlocked, MediaRecorder is prepared,
                // now you can start recording

                mMediaRecorder.start();
                Log.d(TAG, "Filmacion Comenzada");

                // inform the user that recording has started
                btn_Video.setText("Stop");
                isRecording = true;
            } else {
                // prepare didn't work, release the camera
                releaseMediaRecorder();
                Log.d(TAG, "Se libero el MadiaRecorder");

                // inform user
            }
        }


    }
*/
    private void LevantarXML() {

        textAlarma1 = (TextView) findViewById(R.id.textAlarma1);
        textOut = (EditText) findViewById(R.id.textout);
        textIn = (EditText) findViewById(R.id.textin);
        text_Bytes=(TextView)findViewById(R.id.text_Bytes);

        progressBar=(ProgressBar)findViewById(R.id.progressBar);

        edit_IP =(EditText)findViewById(R.id.edit_IP);
        edit_Port=(EditText)findViewById(R.id.edit_Port);
        edit_IdRadio =(EditText)findViewById(R.id.edit_IdRadio);
        edit_TimerKA= (EditText) findViewById(R.id.edit_TimerKA);
        edit_PortKA=(EditText)findViewById(R.id.edit_PortKA);
        edit_DuracionVideo=(EditText)findViewById(R.id.edit_DuracionVideo);

        toggle_ka= (ToggleButton) findViewById(R.id.toggle_ka);
        toggleAudio= (ToggleButton) findViewById(R.id.toggleAudio);

        buttonSend = (Button) findViewById(R.id.send);
        btn_Foto = (Button) findViewById(R.id.btn_Captura);
        btn_Video = (Button) findViewById(R.id.btn_Video);
        btn_Intrusion = (Button) findViewById(R.id.btn_Intrusion);
        btn_Energia = (Button) findViewById(R.id.btn_Energia);
        btn_Prueba = (Button) findViewById(R.id.btn_Prueba);
        btn_Apertura = (Button) findViewById(R.id.btn_Apertura);
        btn_Conf_FTP= (Button) findViewById(R.id.btn_Conf_FTP);
        btn_Enviar_FTP=(Button) findViewById(R.id.btn_Enviar_FTP);
        btn_USB=(Button) findViewById(R.id.btn_USB);

        switch_muteAlarm = (Switch) findViewById(R.id.switch_Alarma);

        preview = (FrameLayout) findViewById(R.id.camera_preview);
        mPreview = (SurfaceView) findViewById(R.id.surfaceView);
        Log.d(TAG, "XML LEvantado");

    }

    ////////////////////////// ++++   FOTO y VIDEO +++++ /////////////////

    public void PARAMETROS() {

        parameters =mCamera.getParameters();
        if(parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO))
        {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);

        }
        if(parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
        {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        parameters.setRotation(90);
        parameters.setJpegQuality(calidadFoto);
        //  String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
      //  parameters.setGpsTimestamp(Long.parseLong(timeStamp));
       // parameters.setZoom(4);

        parameters.setVideoStabilization(true);


        mCamera.setParameters(parameters);
        Log.d(TAG, "Parametros de la Camara Cargados");
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
            Log.d(TAG, "Camara Abierta");
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
            Log.d(TAG, "Camara Cerrada");
        }
        return c; // returns null if camera is unavailable
    }

    /** A basic Camera preview class */
    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mHolder;
        private Camera mCamera;

        public CameraPreview(Context context, Camera camera) {
            super(context);
            mCamera = camera;
            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);


            // deprecated setting, but required on Android versions prior to 3.0
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        public void surfaceCreated(SurfaceHolder holder) {
            // The Surface has been created, now tell the camera where to draw the preview.
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (IOException e) {
                Log.d(TAG, "Error setting camera preview: " + e.getMessage());
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // empty. Take care of releasing the Camera preview in your activity.
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.

            if (mHolder.getSurface() == null){
                // preview surface does not exist
                return;
            }

            // stop preview before making changes
            try {
                mCamera.stopPreview();
            } catch (Exception e){
                // ignore: tried to stop a non-existent preview
            }

            // set preview size and make any resize, rotate or
            // reformatting changes here
            mCamera.setDisplayOrientation(90);
            PARAMETROS();

            // start preview with new settings
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();

            } catch (Exception e){
                Log.d(TAG, "Error starting camera preview: " + e.getMessage());
            }
        }
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null){
                Log.d(TAG, "Error creating media file, check storage permissions: ");// +  e.getMessage());
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                EnviarFTP();
                mCamera.startPreview();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };

    private void releaseCamera(){
        mCamera.stopPreview();
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
            Log.d(TAG, "Camara Liberada");

        }
    }

    private boolean prepareVideoRecorder(){


        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);
        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)

        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_QVGA));

        // Step 4: Set output file
        mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());
        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());
        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
            Log.d("prepareVideoRecorder", " mMediaRecorder.prepare()");
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    private void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use

        }
    }
    /////////////////////////////////////////////////////////////

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Radiobases");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "Radiobase_"+edit_IdRadio.getText().toString()+"_IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "Radiobase_"+edit_IdRadio.getText().toString()+"_VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    //////////////////////////--- FOTO Y VIDEO ----//////////////////////

    private void BotonesEnabled (boolean ena){

        edit_IdRadio.setEnabled(!ena);
        edit_IP.setEnabled(!ena);
        edit_Port.setEnabled(!ena);
        edit_PortKA.setEnabled(!ena);
        edit_TimerKA.setEnabled(!ena);
        edit_DuracionVideo.setEnabled(!ena);

    }

///////////////////// PREFERENCIAS DE USUARIO ////////////////
    public void CargarPreferencias(){

        SharedPreferences mispreferencias=getSharedPreferences("PreferenciasUsuario", Context.MODE_PRIVATE);
        edit_IdRadio.setText(mispreferencias.getString("IdRadio", "1"));
        edit_IP.setText(mispreferencias.getString("edit_IP", "idirect.dlinkddns.com"));
        edit_Port.setText(mispreferencias.getString("edit_Port", "9001"));
        edit_PortKA.setText(mispreferencias.getString("edit_PortKA", "9002"));
        edit_TimerKA.setText(mispreferencias.getString("edit_TimerKA", "10"));
       edit_DuracionVideo.setText(String.valueOf(mispreferencias.getInt("edit_DuracionVideo", 10)));
        toggleAudio.setChecked(mispreferencias.getBoolean("audioBool", true));
        toggle_ka.setChecked(mispreferencias.getBoolean("boolKA", true));
        switch_muteAlarm.setChecked(mispreferencias.getBoolean("muteAlarm",false));
        Log.d(TAG, "Preferencias Cargadas , boolKA: " + toggle_ka.isChecked());

 }

    public void GuardarPreferencias() {

        SharedPreferences mispreferencias = getSharedPreferences("PreferenciasUsuario", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mispreferencias.edit();
        editor.putString("IdRadio", edit_IdRadio.getText().toString());
        editor.putString("edit_IP", edit_IP.getText().toString());
        editor.putString("edit_Port", edit_Port.getText().toString());
        editor.putString("edit_PortKA", edit_PortKA.getText().toString());
        editor.putString("edit_TimerKA", edit_TimerKA.getText().toString());
        editor.putInt("edit_DuracionVideo", Integer.parseInt(edit_DuracionVideo.getText().toString()));
        editor.putBoolean("audioBool", toggleAudio.isChecked());
        editor.putBoolean("boolKA", toggle_ka.isChecked());
        editor.putBoolean("muteAlarm", switch_muteAlarm.isChecked());
        editor.commit();
        Log.d(TAG, "Preferencias Guardadas , boolKA: " + mispreferencias.getBoolean("boolKA", true));

    }

// //////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent intent=new Intent(this,Lay_Configuracion.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.about) {

           Toast.makeText(getApplicationContext(), "Correo del desarrollador:\n diegogiovanazzi@gmail.com" +
                   " correo: DiegoGiovanazzi@gmail.com", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void EnviarFTP(){


        String ip=IpPublica;
        String userName="idirect";
        String pass="IDIRECT";

        cliente = new ConnectUploadAsync(getApplicationContext(),ip,userName,pass,MainActivity.this);
        cliente.execute();
   }

    public void Filmacion(){



            if (prepareVideoRecorder()) {
              //  MuteAlarm=true;
                // Camera is available and unlocked, MediaRecorder is prepared,
                // now you can start recording

                mMediaRecorder.start();
                Log.d(TAG, "Filmacion Comenzada");

                // inform the user that recording has started
                btn_Video.setText("Stop");
                isRecording = true;
            } else {
                // prepare didn't work, release the camera
                releaseMediaRecorder();
                Log.d(TAG, "Se libero el MadiaRecorder");
                             // inform user
                //
                          }

            int Duracion =Integer.parseInt(edit_DuracionVideo.getText().toString());
            Log.d(TAG,"Duracion del Video: "+Duracion+" Seg.");
            try {
                Thread.sleep(Duracion*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
             if (isRecording) {
                // stop recording and release camera
                 mMediaRecorder.stop();  // stop the recording
                 releaseMediaRecorder(); // release the MediaRecorder object
                 mCamera.lock();         // take camera access back from MediaRecorder


                // inform the user that recording has stopped

                isRecording = false;


                 Log.d(TAG,"filmacion detenida y en proceso de envio");
                // MuteAlarm=false;
            }

            EnviarFTP();

    }

    public class conetarBluetooth implements Runnable{

        @Override
        public void run() {

            BluetoothDevice device = btAdapter.getRemoteDevice(address);// mac address de bluetooth

            // Two things are needed to make a connection:
            //   A MAC address, which we got above.
            //   A Service ID or UUID.  In this case we are using the
            //     UUID for SPP.

            try {
                btSocket = createBluetoothSocket(device);
            } catch (IOException e1) {
                errorExit("Fatal Error", "In onResume() and socket create failed: " + e1.getMessage() + ".");
            }

            // Discovery is resource intensive.  Make sure it isn't going on
            // when you attempt to connect and pass your message.
            btAdapter.cancelDiscovery();


            // Establish the connection.  This will block until it connects.
            Log.d(TAG, "...Connecting...");
            try {
                btSocket.connect();


                Toast.makeText(getApplicationContext(),"se conectoooo",Toast.LENGTH_SHORT).show();

                Log.d(TAG, "...Connection ok...");
            } catch (IOException e) {
                try {
                    btSocket.close();
                } catch (IOException e2) {
                    errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
                }
            }

            // Create a data stream so we can talk to server.
            Log.d(TAG, "...Create Socket...");

            try {
                outStream = btSocket.getOutputStream();
            } catch (IOException e) {
                errorExit("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
            }
            mConnectedThread = new ConnectedThread(btSocket);
            mConnectedThread.start();
        }
    }

    //The BroadcastReceiver that listens for bluetooth broadcasts
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

          if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                //Device is now connected
                Toast.makeText(getApplicationContext(),"Device is now connected",Toast.LENGTH_SHORT).show();
              ConexionIP ClienteTCP=new ConexionIP(IpPublica,9001," 1 13");
              ClienteTCP.start();
                 EnviarSMS sms=new EnviarSMS(context,Diego,"Bluetooth Conectado");
                 sms.sendSMS();
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                //Device has disconnected
                Toast.makeText(getApplicationContext(),"Device has disconnected",Toast.LENGTH_SHORT).show();
                new  conetarBluetooth().run();

                ConexionIP ClienteTCP=new ConexionIP(IpPublica,9001," 1 5");
                ClienteTCP.start();
                EnviarSMS sms=new EnviarSMS(context,Diego,"Bluetooth Desconectado");
                sms.sendSMS();
            }
        }
    };


    private void BroadcastSMS() {

        SmsRecibido=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                //           Toast.makeText(context,"BroadcasrReceiver",Toast.LENGTH_SHORT).show();
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
                                case 0:

                                //    sms=new EnviarSMS(context,phoneNumber,"Kill aplication");

                                 //   sms.sendSMS();
                              //      finish();
                                    //
                                   // moveTaskToBack(true);
                                  //  moveTaskToBack(true);
                                  //  moveTaskToBack(true);
                            //        android.os.Process.killProcess(android.os.Process.myPid());
                                    Intent intentoStop = new Intent(context,MainActivity.class);

                                    intentoStop.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intentoStop.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    startActivity(intentoStop);
                                    finish();
                                    System.exit(0);
                                    android.os.Process.killProcess(android.os.Process.myPid());



                                case 1:

                                    alarmas=new CheckAlarmas(IdRadiobase, "12",IpPublica, 9001, getApplicationContext(),audioBool);
                                    alarmas.run();
                                    Filmacion();
                                    sms=new EnviarSMS(context,phoneNumber,"Solicitud de Video:ok");
                                    sms.sendSMS();
                                    break;
                                case 2:
                                    new conetarBluetooth().run();
                                    sms=new EnviarSMS(context,phoneNumber,"Solicitud de Conexión bluetooth:ok");
                                    sms.sendSMS();
                                    break;
                                case 3:

                                    sms=new EnviarSMS(context,phoneNumber,"Kill Aplication:ok");
                                    sms.sendSMS();
                                    finish();
                                    break;
                                case 7:
                                    switch_muteAlarm.setChecked(false);
                                    sms=new EnviarSMS(context,phoneNumber,"Sensores Activados");
                                    sms.sendSMS();
                                    break;

                                case 8:
                                     switch_muteAlarm.setChecked(true);
                                    sms=new EnviarSMS(context,phoneNumber,"Sensores Desactivados");
                                    sms.sendSMS();
                                    GuardarPreferencias();
                                    break;

                                case 12:
                                    EnviarFTP();
                                    alarmas=new CheckAlarmas(IdRadiobase, "11",IpPublica, 9001, getApplicationContext(),audioBool);
                                    alarmas.run();
                                    sms=new EnviarSMS(context,phoneNumber,"Solicitud de envio de Archivos");
                                    sms.sendSMS();

                                    break;


                            }

                        }
                    }
                } catch (Exception e) {
                    Log.e("SmsReceiver", "Exception smsReceiver" + e);
                }

            }
        };

        registerReceiver(SmsRecibido, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
        CargarPreferencias();
    }




}