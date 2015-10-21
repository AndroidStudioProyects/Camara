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
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsMessage;
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
import com.example.diego.camara.Services.ServicioGPS;

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
// prueba commit
    public static String USER_FTP = "";
    public static String PASS_FTP = "";

    public static String MASTER_PHONE = "";
    public static String LatyLong;
    public static String BLUE_PRUEBA_STATIC;
    public static Boolean MUTEALARM = false;
    public static Boolean  BOOLFILM=true;

    public static String Latitud="0.0",Longitud="0.0";
//
    static String ALARMA_ALMACENADA="";
    public static final String OK="1";
    public static final String ALARMA_INTRUSION="2";
    public static final String ALARMA_APERTURA="3";
    public static final String ALARMA_EN_BATERIAS="4";
    public static final String C0NECTADO_RED_ELECTRICA="5";
    public static final String BLUETOOTH_OFF="6";
    public static final String BLUETOOTH_ON="7";
    public static final String HARD_RESET_BLUETOOTH="8";
    public static final String SMS_RECIBIDO="9";
    public static final String ALARMA_BATERIA_BAJA="10";
    public static final String SMS_SOLICITUD_FTP="11";
    public static final String GPS="12";
    public static final String REINICIO_COMPLETADO="13";
    public static final String SENSORES_ACTIVADOS="14";
    public static final String SENSORES_DESACTIVADOS="15";
    public static final String SMS_SOLICITUD_VIDEO="16";
    public static final String APLICACION_CERRADA="17";
    public static final String SMS_FOTO="18";
    public static final String INCIA_APLICACION="19";
    public static final String SMS_ALARMA_SIMULADA="20";
    public static final String OPEN_DOOR="21";
    public static final String LOOP_EXITOSO="22";
    public static final String RESET_BLUETOOTH="23";
    public static final String CONEXION_BLUETOOTH_EXITOSA="24";
    public static final String PERSONAL_NO_AUTORIZADO="25";
    public static final String STATUS_INFO="26";


    public static String STATUS_BATTERY="";

    ///////////////////////////////

    String Nivel_anterior="0",Status_anterior="0";
    int Status_Contador=0;
    //Linvor Bluetooth
    public static String address = "00:12:12:04:41:11";
    public static String Alarmabluetooth = "0";

    static boolean CONEXIONBLUE=false;
    static ConexionIP ClienteTCP;
    ServicioGPS servicio;
    // sms
    EnviarSMS sms;
    //   BLUETOOTH !

    private StringBuilder sb = new StringBuilder();
    static final int RECIEVE_MESSAGE = 1;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    private static Handler h;
    ConnectedThread mConnectedThread;
    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static boolean BoolFoto = false;
    // MAC-address of Bluetooth module (you must edit this line)
    //private static String address = "00:14:01:06:13:29";

    // BLUETOOTH FIN

    ToggleButton toggleAudio, toggle_ka;
    Switch switch_muteAlarm;
    static EditText  edit_IP, edit_Port, edit_IdRadio,  edit_TimerKA, edit_PortKA, edit_DuracionVideo,edit_Telefono;
    Button  btn_Prueba, btn_Foto, btn_Video, btn_Intrusion, btn_USB;
    Button btn_Energia, btn_Apertura, btn_Conf_FTP, btn_Enviar_FTP;
    Button btn_Reboot,btn_BlueConnect,btn_Led,btn_Loop,btn_BlueReset;
    public TextView  text_Bytes,text_GPS,txtConexionBluetooth;
    TextView batteryLevel,batteryVoltage,batteryTemperature,batteryTechnology,batteryStatus,batteryHealth;
    public ProgressBar progressBar;

    String stringToRx;
    private FrameLayout preview;
    private SurfaceView mPreview;
    Boolean isRecording = false;

    private Camera mCamera;
    private MediaRecorder mMediaRecorder;
    Camera.Parameters parameters;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private int calidadFoto = 100;
    public static Boolean Bandera = false;
    static final String TAG = "Camara";
    boolean audioBool = false;
    public static int IdRadiobase = 0;
    Intent intentKeepAlive;
    public ConnectUploadAsync cliente;
    static String IpPublica;
    //CheckAlarmas alarmas;
    Thread alarmas;


    public static Thread blueLocal ;
   public static ThreadBlue blue;
    ToggleButton tb_Led;
    private BroadcastReceiver SmsRecibido;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate inicio");

        setContentView(R.layout.activity_main);

        LevantarXML();
        Botones();

      //  CargarPreferencias();

        IdRadiobase = Integer.parseInt(edit_IdRadio.getText().toString());
        IpPublica = edit_IP.getText().toString();
        MASTER_PHONE=edit_Telefono.getText().toString();

        CAMARA_ON();

        ////defino bluetooth adapter
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();//Checkeo el estado

        BroadcastSMS();


        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {

                    case RECIEVE_MESSAGE:
                                           // if receive massage
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);                 // create string from bytes array
                        sb.append(strIncom);                                                // append string
                        int endOfLineIndex = sb.indexOf("\r\n");                            // determine the end-of-line
                        if (endOfLineIndex > 0) {                                            // if end-of-line,

                        String sbprint = sb.substring(0, endOfLineIndex);               // extract string
                        Alarmabluetooth = sbprint;
                        sb.delete(0, sb.length());                                      // and clear
                        Log.d(TAG, "Mensaje del Arduino: " + Alarmabluetooth);


                        switch (Alarmabluetooth){


                            case "A":
                                if (!MUTEALARM) {
                                    Toast.makeText(getApplicationContext(),"entro",Toast.LENGTH_SHORT).show();
                                    ALARMA_ALMACENADA=ALARMA_INTRUSION;
                                   Filmacion();
                                }
                                break;
                            case "B":
                                if (!MUTEALARM) {
                                    ALARMA_ALMACENADA=ALARMA_INTRUSION;
                                   Filmacion();
                                }
                                break;
                            case "C":
                                if (!MUTEALARM) {
                                    ALARMA_ALMACENADA=ALARMA_INTRUSION;
                                    Filmacion();
                                }
                                break;
                            case "D":
                                if (!MUTEALARM) {
                                    ALARMA_ALMACENADA=ALARMA_APERTURA;
                                    Filmacion();
                                }
                                break;
                            case "f":
                                if (!MUTEALARM) {
                                    ClienteTCP=new ConexionIP(IpPublica,9001," "+IdRadiobase+" "+SMS_ALARMA_SIMULADA);
                                    ClienteTCP.start();
                                    // alarma simulada por sms
                                }
                                break;
                            case "o" :
                                sms=new EnviarSMS(getApplicationContext(),MASTER_PHONE,"Open Door ok");
                                sms.sendSMS();
                                ClienteTCP=new ConexionIP(IpPublica,9001," "+IdRadiobase+" "+OPEN_DOOR);
                                ClienteTCP.start();
                                break;
                            case "l":
                                sms=new EnviarSMS(getApplicationContext(),MASTER_PHONE,"Loop OK");
                                sms.sendSMS();
                                ClienteTCP=new ConexionIP(IpPublica,9001," "+IdRadiobase+" "+LOOP_EXITOSO);
                                ClienteTCP.start();
                                break;
                            case "e":

                                break;

                            case "b" :
                                sms=new EnviarSMS(getApplicationContext(),MASTER_PHONE,"Hard reset Bluetooth ok");
                                sms.sendSMS();
                                ClienteTCP=new ConexionIP(IpPublica,9001," "+IdRadiobase+"+ "+HARD_RESET_BLUETOOTH);
                                ClienteTCP.start();
                                break;
                            default:break;


                        }

                    }


                        break;

                }

            }
        };

        Log.d(TAG, "ConectarBluetooth Oncreate");

        new ThreadBlue().run();

        Log.d(TAG, "ConectarBluetooth Oncreate");

        servicio=new ServicioGPS(getApplicationContext());
        text_GPS.setText(servicio.LatyLong());


/// BroadcastReceiver  Bluettoth

        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        this.registerReceiver(mReceiver, filter1);
        this.registerReceiver(mReceiver, filter3);

        this.registerReceiver(this.myBatteryReceiver,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        ClienteTCP=new ConexionIP(IpPublica,9001," "+IdRadiobase+" "+INCIA_APLICACION);
        ClienteTCP.start();
    }


    @Override
    public void onResume() {
        super.onResume();
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
      //  ClienteTCP=new ConexionIP(IpPublica,9001," "+IdRadiobase+" "+APLICACION_CERRADA);

        unregisterReceiver(SmsRecibido);

        BLUE_PRUEBA_STATIC="B\n";
        mConnectedThread.write(BLUE_PRUEBA_STATIC);
        Log.d(TAG, "OnDestroy");
        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event
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

    private void Botones() {

        btn_Led.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BLUE_PRUEBA_STATIC="O\n";
                mConnectedThread.write(BLUE_PRUEBA_STATIC);
            }
        });

        btn_Loop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BLUE_PRUEBA_STATIC="L\n";
                mConnectedThread.write(BLUE_PRUEBA_STATIC);


            }
        });

        btn_BlueReset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BLUE_PRUEBA_STATIC = "B\n";
                mConnectedThread.write(BLUE_PRUEBA_STATIC);

            }
        });

        switch_muteAlarm.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    Log.d(TAG, "Sensores Desactivados");
                    MUTEALARM = true;
                    alarmas = new Thread(new CheckAlarmas(IdRadiobase, SENSORES_DESACTIVADOS, IpPublica, 9001, getApplicationContext(), audioBool));
                    alarmas.start();
                } else {
                    MUTEALARM = false;
                    Log.d(TAG, "Sensores Activados");
                    alarmas = new Thread(new CheckAlarmas(IdRadiobase, SENSORES_ACTIVADOS, IpPublica, 9001, getApplicationContext(), audioBool));
                    alarmas.start();
                }
                GuardarPreferencias();
            }
        });


        btn_Enviar_FTP.setOnClickListener(new OnClickListener() {

            String ip = edit_IP.getText().toString();

            @Override
            public void onClick(View v) {

                cliente = new ConnectUploadAsync(getApplicationContext(), ip, USER_FTP, PASS_FTP, MainActivity.this);
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

        btn_BlueConnect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new ThreadBlue().run();
            }

        });

        btn_Foto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                            Log.d(TAG, "Boton de Foto");
            mCamera.takePicture(null, null, mPicture);

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



            }
        });

        btn_Intrusion.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "Alarma Intrusion");

                BLUE_PRUEBA_STATIC="F\n";
                mConnectedThread.write(BLUE_PRUEBA_STATIC);

            }
        });
        btn_Apertura.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.d(TAG, "Alarma de Apertura");
                BLUE_PRUEBA_STATIC="F\n";
                mConnectedThread.write(BLUE_PRUEBA_STATIC);
            }
        });
        btn_Energia.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.d(TAG, "Alarma de Energia");
                ClienteTCP=new ConexionIP(IpPublica,9001," "+IdRadiobase+" "+ALARMA_EN_BATERIAS);
                ClienteTCP.start();
            }
        });



        tb_Led.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    Log.d(TAG, "Led ON");
                 //   Toast.makeText(getApplicationContext(), "Led ON", Toast.LENGTH_SHORT).show();

                }else{
                 //   Toast.makeText(getApplicationContext(), "Led OFF", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Led Off");

                }
            }
        });

        toggleAudio.setOnCheckedChangeListener(new OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                audioBool=isChecked;
                if(isChecked){
                    Log.d(TAG, "Audio ON");
                //    Toast.makeText(getApplicationContext(), "Audio ON", Toast.LENGTH_SHORT).show();
                }else{
               //     Toast.makeText(getApplicationContext(), "Audio OFF", Toast.LENGTH_SHORT).show();
                  //  Log.d(TAG, "Audio Off");
                }
            }
        });
        toggle_ka.setOnCheckedChangeListener(new OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked) {
                    GuardarPreferencias();
                    IdRadiobase = Integer.parseInt(edit_IdRadio.getText().toString());
                    IpPublica = edit_IP.getText().toString();
                    MASTER_PHONE=edit_Telefono.getText().toString();
                    intentKeepAlive = new Intent(getApplicationContext(), KeepAlive.class);
                    intentKeepAlive.putExtra("bool", true);
                    startService(intentKeepAlive);
                    BotonesEnabled(isChecked);
                } else {

                    stopService(intentKeepAlive);
                    edit_TimerKA.setEnabled(true);
                    BotonesEnabled(isChecked);
                }

            }
        });


    }

    private void LevantarXML() {

        text_Bytes=(TextView)findViewById(R.id.text_Bytes);
        text_GPS=(TextView)findViewById(R.id.text_GPS);

        batteryLevel= (TextView)findViewById(R.id.batteryLevel);
        batteryVoltage= (TextView)findViewById(R.id.batteryVoltage);
        batteryTemperature= (TextView)findViewById(R.id.batteryTemperature);
        batteryTechnology= (TextView)findViewById(R.id.batteryTechnology);
        batteryStatus= (TextView)findViewById(R.id.batteryStatus);
        batteryHealth= (TextView)findViewById(R.id.batteryHealth);

        txtConexionBluetooth=(TextView)findViewById(R.id.txtConexionBluetooth);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);

        edit_IP =(EditText)findViewById(R.id.edit_IP);
        edit_Port=(EditText)findViewById(R.id.edit_Port);
        edit_IdRadio =(EditText)findViewById(R.id.edit_IdRadio);
        edit_TimerKA= (EditText) findViewById(R.id.edit_TimerKA);
        edit_PortKA=(EditText)findViewById(R.id.edit_PortKA);
        edit_DuracionVideo=(EditText)findViewById(R.id.edit_DuracionVideo);
        edit_Telefono=(EditText)findViewById(R.id.edit_Telefono);

        toggle_ka= (ToggleButton) findViewById(R.id.toggle_ka);
        toggleAudio= (ToggleButton) findViewById(R.id.toggleAudio);
        tb_Led= (ToggleButton) findViewById(R.id.tb_Led);

        btn_Foto = (Button) findViewById(R.id.btn_Captura);
        btn_Video = (Button) findViewById(R.id.btn_Video);
        btn_Intrusion = (Button) findViewById(R.id.btn_Intrusion);
        btn_Energia = (Button) findViewById(R.id.btn_Energia);
        btn_Prueba = (Button) findViewById(R.id.btn_Prueba);
        btn_Apertura = (Button) findViewById(R.id.btn_Apertura);
        btn_Conf_FTP= (Button) findViewById(R.id.btn_Conf_FTP);
        btn_Enviar_FTP=(Button) findViewById(R.id.btn_Enviar_FTP);
        btn_BlueConnect=(Button) findViewById(R.id.btn_BlueConnect);
        btn_Led=(Button) findViewById(R.id.btn_Led);
        btn_Loop=(Button) findViewById(R.id.btn_Loop);
        btn_BlueReset=(Button) findViewById(R.id.btn_BlueReset);
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
        parameters.setRotation(90);
    //    parameters.setPictureSize(350, 600);

      //    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //parameters.setGpsTimestamp(Long.parseLong(timeStamp));
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
        String timeStampFechaFolder = new SimpleDateFormat("yyyy_MM_dd").format(new Date());
        String timeStampFecha = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String timeStampHora = new SimpleDateFormat("HHmmss").format(new Date());
        File mediaFile;
        String link;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "Radiobase_"+edit_IdRadio.getText().toString()+"_IMG_"+ timeStampFecha + "_"+timeStampHora+".jpg");
          link= "videos/Radiobase_ID_"+IdRadiobase+"/"+timeStampFechaFolder+"/"+
                  "Radiobase_"+edit_IdRadio.getText().toString()+"_IMG_"+ timeStampFecha +"_"+timeStampHora+".jpg";


        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "Radiobase_"+edit_IdRadio.getText().toString()+"_VID_"+ timeStampFecha +"_"+timeStampHora+".mp4");
            link= "videos/Radiobase_ID_"+IdRadiobase+"/"+timeStampFechaFolder+"/"+
            "Radiobase_"+edit_IdRadio.getText().toString()+"_VID_"+ timeStampFecha +"_"+timeStampHora+".mp4";

        } else {


            return null;
        }

        ClienteTCP=new ConexionIP(IpPublica,9001," "+IdRadiobase+" "+ALARMA_ALMACENADA+" "+link );
        ClienteTCP.start();
        return mediaFile;
    }

    //////////////////////////--- FOTO Y VIDEO ----//////////////////////

    private void BotonesEnabled (boolean ena){
        edit_Telefono.setEnabled(!ena);
        edit_IdRadio.setEnabled(!ena);
        edit_IP.setEnabled(!ena);
        edit_Port.setEnabled(!ena);
        edit_PortKA.setEnabled(!ena);
        edit_TimerKA.setEnabled(!ena);
        edit_DuracionVideo.setEnabled(!ena);

    }

    private void EnviarFTP(){


        String ip=IpPublica;

        cliente = new ConnectUploadAsync(getApplicationContext(),ip,USER_FTP,PASS_FTP,MainActivity.this);
        cliente.execute();
   }

    public void Filmacion(){

        MUTEALARM=true;
        alarmas = new Thread(new CheckAlarmas(IdRadiobase, ALARMA_ALMACENADA, IpPublica, 9001, getApplicationContext(), audioBool));
        alarmas.start();

            if (prepareVideoRecorder()) {
                   mMediaRecorder.start();
                Log.d(TAG, "Filmacion Comenzada");

                // inform the user that recording has started
                btn_Video.setText("Stop");
                isRecording = true;
            } else {
                // prepare didn't work, release the camera
                releaseMediaRecorder();
                Log.d(TAG, "Se libero el MadiaRecorder");
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

            }

        MUTEALARM=false;

        EnviarFTP();

    }

 ////////////////   bROADCAST RECEIVERS


    private BroadcastReceiver myBatteryReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub
            String Level = "0%",Voltage = "0v",Temperature = "0c",Status = "Unknown",Health = "Unknown";


            if (arg1.getAction().equals(Intent.ACTION_BATTERY_CHANGED)){
                Status_Contador++;
                batteryLevel.setText("Level: "
                        + String.valueOf(arg1.getIntExtra("level", 0)) + "%");
                Level= String.valueOf(arg1.getIntExtra("level", 0)) + "%";
                batteryVoltage.setText("Voltage: "
                        + String.valueOf((float)arg1.getIntExtra("voltage", 0)/1000) + "V");
                Voltage=String.valueOf((float)arg1.getIntExtra("voltage", 0)/1000) + "V";
                batteryTemperature.setText("Temperature: "
                        + String.valueOf((float)arg1.getIntExtra("temperature", 0)/10) + "c");
                Temperature= String.valueOf((float)arg1.getIntExtra("temperature", 0)/10) + "c";
                batteryTechnology.setText("Technology: " + arg1.getStringExtra("technology"));

                int status = arg1.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN);
                String strStatus;
                if (status == BatteryManager.BATTERY_STATUS_CHARGING){
                    strStatus = "Charging";
                } else if (status == BatteryManager.BATTERY_STATUS_DISCHARGING){
                    strStatus = "Dis-charging";
                } else if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING){
                    strStatus = "Not charging";
                } else if (status == BatteryManager.BATTERY_STATUS_FULL){
                    strStatus = "Full";
                } else {
                    strStatus = "Unknown";
                }
                batteryStatus.setText("Status: " + strStatus);
                Status=strStatus;

                int health = arg1.getIntExtra("health", BatteryManager.BATTERY_HEALTH_UNKNOWN);
                String strHealth;
                if (health == BatteryManager.BATTERY_HEALTH_GOOD){
                    strHealth = "Good";
                } else if (health == BatteryManager.BATTERY_HEALTH_OVERHEAT){
                    strHealth = "Over Heat";
                } else if (health == BatteryManager.BATTERY_HEALTH_DEAD){
                    strHealth = "Dead";
                } else if (health == BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE){
                    strHealth = "Over Voltage";
                } else if (health == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE){
                    strHealth = "Unspecified Failure";
                } else{
                    strHealth = "Unknown";
                }
                batteryHealth.setText("Health: " + strHealth);
                Health=strHealth;

            }

            //agregado 21-10-2015

            if(!(Nivel_anterior.toString().equals(Level) || Status_anterior.toString().equals(Status))){

                Nivel_anterior=Level;
                Status_anterior=Status;
                ClienteTCP=new ConexionIP(IpPublica,9001," "+IdRadiobase+" "+STATUS_INFO+" "+Level+" "+Voltage+" "+Temperature+" "+Status+" "+Health+" "+Latitud+","+Longitud);
                ClienteTCP.start();
                Log.d(TAG, "Status Info IpServer: " + IpPublica + " Puerto: 9001 "+ IdRadiobase+" "+STATUS_INFO+" "+Level+" "+Voltage+" "+Temperature+" "+Status+" "+Health+" "+Latitud+","+Longitud);

            }


        }

    };



  private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {

                Log.d(TAG, " BluetoothDesconectado!! ");
                ConexionIP ClienteTCP=new ConexionIP(IpPublica, 9001," "+IdRadiobase+" "+BLUETOOTH_OFF);
                ClienteTCP.start();
                txtConexionBluetooth.setText("ConexionBluetooth: DESCONECTADO");

                new ThreadBlue().run();

            }

          if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {


              //     Toast.makeText(getApplicationContext(),"Device is now connected",Toast.LENGTH_SHORT).show();
              ConexionIP ClienteTCP=new ConexionIP(IpPublica, 9001," "+IdRadiobase+" "+BLUETOOTH_ON);
              ClienteTCP.start();
          //    sms=new EnviarSMS(context,MASTER_PHONE,"Bluetooth Conectado");
          //    sms.sendSMS();

          }

        }
    };


    private void BroadcastSMS() {

        SmsRecibido=new BroadcastReceiver() {

            ConexionIP ClienteTCP;

            @Override
            public void onReceive(Context context, Intent intent) {

                final Bundle bundle = intent.getExtras();

                try {
                    if(bundle != null){

                        final Object[] pdusObj = (Object[]) bundle.get("pdus");

                        for(int i = 0; i < pdusObj.length; i++){
                            SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                            String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                            String senderNum = phoneNumber;
                            String message = currentMessage.getDisplayMessageBody();
                       //   Toast.makeText(getApplicationContext(),senderNum,Toast.LENGTH_SHORT).show();
                            if(phoneNumber.toString().equals(MASTER_PHONE)){

                            switch (message){

                                case "V":
                                    sms=new EnviarSMS(getApplicationContext(),senderNum,"Solicitud de Video:ok");
                                    sms.sendSMS();
                               //     ClienteTCP=new ConexionIP(IpPublica,9001," "+IdRadiobase+" "+SMS_SOLICITUD_VIDEO);
                               // ClienteTCP.start();
                                    ALARMA_ALMACENADA=SMS_SOLICITUD_VIDEO;
                                    Filmacion();
                                    Log.d(TAG, " VIDEO SMS");
                                    break;
                                case "F":
                                    sms=new EnviarSMS(getApplicationContext(),senderNum,"Solicitud de Foto:ok");
                                    sms.sendSMS();
                                //    ClienteTCP=new ConexionIP(IpPublica,9001," "+IdRadiobase+" "+SMS_FOTO);
                                //    ClienteTCP.start();
                                    ALARMA_ALMACENADA=SMS_FOTO;
                                    // MUTEALARM=true;
                                    mCamera.takePicture(null, null, mPicture);
                                    // MUTEALARM=false;
                                      Log.d(TAG, " FOTO SMS");

                                    break;
                                case "Br":   Log.d(TAG, " BLUETOOTH RESET SMS");
                                    sms=new EnviarSMS(context,senderNum,"Solicitud de Reset Hard Bluetooth");
                                    sms.sendSMS();
                                    BLUE_PRUEBA_STATIC="B\n";
                                    mConnectedThread.write(BLUE_PRUEBA_STATIC);
                                    Log.d(TAG, " RESET HARD BLUETOOTH SMS");
                                    break;
                                case "Bon":
                                    sms=new EnviarSMS(context,senderNum,"Solicitud de Conexión Bluetooth");
                                    sms.sendSMS();

                                    //conectarBluetooth();
                                  //  blueLocal =new Thread(new ThreadBluetooth());
                                  //  blueLocal.start();
                                   new ThreadBlue().run();
                                    Log.d(TAG, " CONEXION BLUETOOTH SMS");

                                    break;

                                case "Conf":
                                    SharedPreferences mispreferencias=getSharedPreferences("PreferenciasUsuario", Context.MODE_PRIVATE);
                                    String id =mispreferencias.getString("IdRadio", "1");
                                    String server=mispreferencias.getString("edit_IP", "idirect.dlinkddns.com");
                                    String portserver=mispreferencias.getString("edit_Port", "9001");
                                    String timevideo=String.valueOf(mispreferencias.getInt("edit_DuracionVideo", 10));
                                    boolean SensorState=mispreferencias.getBoolean("muteAlarm", false);

                                    String Sensores="Activados";
                                    if(SensorState){
                                        Sensores="Desactivados";
                                    }

                                    String Info= "IdRadio: "+id
                                            +"\nServer: "+server
                                            +"\nPuerto Eventos: "+portserver
                                            +"\nVideo Tiempo: "+timevideo
                                            +"\nSensores: "+Sensores;

                                    sms=new EnviarSMS(getApplicationContext(),senderNum,Info);
                                    sms.sendSMS();
                                    break;

                             
                                case "K":
                                    Log.d(TAG, " KILL APP SMS");
                                    ClienteTCP=new ConexionIP(IpPublica,9001," "+IdRadiobase+" "+APLICACION_CERRADA);
                                    ClienteTCP.start();
                                    Thread.sleep(3000);
                                    finish();
                                    System.exit(0);
                                    break;
                                case "Open":
                                    BLUE_PRUEBA_STATIC="O\n";
                                    mConnectedThread.write(BLUE_PRUEBA_STATIC);

                                case "Loop":
                                    Log.d(TAG, " LOOP POR SMS");
                                    sms=new EnviarSMS(context,senderNum,"Solicitud de Loop Recibida");
                                    sms.sendSMS();
                                    BLUE_PRUEBA_STATIC="L\n";
                                    mConnectedThread.write(BLUE_PRUEBA_STATIC);
                                    break;

                                case "A":
                                    Log.d(TAG, " ALARMA SIMULADA POR SMS");
                                    BLUE_PRUEBA_STATIC="F\n";
                                    mConnectedThread.write(BLUE_PRUEBA_STATIC);
                                    break;
                                case "Pna":
                                    Log.d(TAG, " Personal no autorizado POR SMS");
                                    alarmas=new Thread(new CheckAlarmas(IdRadiobase,PERSONAL_NO_AUTORIZADO ,IpPublica, 9001, getApplicationContext(),audioBool));
                                    alarmas.start();
                                    break;
                                case "On":
                                    Log.d(TAG, " SENSORES ACTIVADOS SMS");

                                  //  alarmas=new Thread(new CheckAlarmas(IdRadiobase,SENSORES_ACTIVADOS ,IpPublica, 9001, getApplicationContext(),audioBool));
                                  //  alarmas.start();
                                    switch_muteAlarm.setChecked(false);
                                    sms=new EnviarSMS(getApplicationContext(),senderNum,"Sensores Activados");
                                    sms.sendSMS();
                                    break;

                                case "Off":
                                    Log.d(TAG, " SENSORES DESACTIVADOS SMS");

                                  //  alarmas=new Thread(new CheckAlarmas(IdRadiobase, SENSORES_DESACTIVADOS,IpPublica, 9001, getApplicationContext(),audioBool));
                                  //  alarmas.start();
                                     switch_muteAlarm.setChecked(true);
                                    sms=new EnviarSMS(getApplicationContext(),senderNum,"Sensores Desactivados");
                                    sms.sendSMS();
                                    GuardarPreferencias();
                                    break;

                                case "Ftp":
                                    Log.d(TAG, " FTP SMS");

                                    EnviarFTP();
                                    ClienteTCP=new ConexionIP(IpPublica,9001," "+IdRadiobase+" "+SMS_SOLICITUD_FTP);
                                    ClienteTCP.start();
                                    sms=new EnviarSMS(getApplicationContext(),senderNum,"Solicitud de envio de Archivos");
                                    sms.sendSMS();

                                    break;
                               case "Gps":
                                   Log.d(TAG, " GPS SMS http://maps.google.com/maps?z=15&q="+servicio.LatyLong());

                                   sms=new EnviarSMS(getApplicationContext(),senderNum,"http://maps.google.com/maps?z=15&q="+servicio.LatyLong());
                                    sms.sendSMS();

                                    break;

                                default:
                                    Log.d(TAG, " COMANDO NO RECONOCIDOS SMS");

                                    break;

                            }

                            }


                        }
                    }
                } catch (Exception e) {
                    Log.e("SmsReceiver", "Exception smsReceiver" + e);
                }
            }
        };

        registerReceiver(SmsRecibido, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));

    }

         ////////////////////////fUNCIONES Bluettoh

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
    //    CONEXIONBLUE=false;
   //     Toast.makeText(getApplicationContext(),"Errorrr",Toast.LENGTH_SHORT).show();
    }

    static class ConnectedThread extends Thread {

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

            BLUE_PRUEBA_STATIC=message;

           // byte[] msgBuffer = message.getBytes();
            byte[] msgBuffer = BLUE_PRUEBA_STATIC.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                Log.d(TAG, "...Error data send: " + e.getMessage() + "...");
            }
        }

    }

    public void conectarBluetooth() {

        BluetoothDevice device = btAdapter.getRemoteDevice(address);// mac address de bluetooth

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.

        try {
            btSocket = createBluetoothSocket(device);
          //  CONEXIONBLUE=true;
        } catch (IOException e1) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e1.getMessage() + ".");
         //   CONEXIONBLUE=false;
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter.cancelDiscovery();


        // Establish the connection.  This will block until it connects.
        Log.d(TAG, "...Connecting...");
        try {
            btSocket.connect();
         //   CONEXIONBLUE=true;
      //    Toast.makeText(getApplicationContext(),"se conectoooo",Toast.LENGTH_SHORT).show();

            txtConexionBluetooth.setText("ConexionBluetooth: CONECTADO");
            Log.d(TAG, "...Connection ok...");
        } catch (IOException e) {
            try {
                btSocket.close();
               // txtConexionBluetooth.setText("ConexionBluetooth: Socket cerrado");
                Log.d(TAG, "...Cerro Socket...");
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


    public class ThreadBlue extends Thread{

        public ThreadBlue(){

            Log.d(TAG,"Hilo Thread Bluetooth Creado");
        }

        @Override
        public void run() {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Log.d(TAG, "conectarBluetooth");

                    conectarBluetooth();

                }
            });



        }
    }
    ///////////////////// PREFERENCIAS DE USUARIO ////////////////
    public void CargarPreferencias(){

        SharedPreferences mispreferencias=getSharedPreferences("PreferenciasUsuario", Context.MODE_PRIVATE);
        edit_Telefono.setText(mispreferencias.getString("Telefono", "00000000000"));
        edit_IdRadio.setText(mispreferencias.getString("IdRadio", "1"));
        edit_IP.setText(mispreferencias.getString("edit_IP", "idirect.dlinkddns.com"));
        edit_Port.setText(mispreferencias.getString("edit_Port", "9001"));
        edit_PortKA.setText(mispreferencias.getString("edit_PortKA", "9002"));
        edit_TimerKA.setText(mispreferencias.getString("edit_TimerKA", "10"));
        edit_DuracionVideo.setText(String.valueOf(mispreferencias.getInt("edit_DuracionVideo", 10)));
        toggleAudio.setChecked(mispreferencias.getBoolean("audioBool", true));
        toggle_ka.setChecked(mispreferencias.getBoolean("boolKA", true));
        switch_muteAlarm.setChecked(mispreferencias.getBoolean("muteAlarm", false));
        USER_FTP=mispreferencias.getString("User_Ftp", "anonimo");
        PASS_FTP=mispreferencias.getString("Pass_Ftp", "anonimo");
        Log.d(TAG, "Preferencias Cargadas , boolKA: " + toggle_ka.isChecked());

    }

    public void GuardarPreferencias() {

        SharedPreferences mispreferencias = getSharedPreferences("PreferenciasUsuario", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mispreferencias.edit();
        editor.putString("Telefono",edit_Telefono.getText().toString());
        editor.putString("IdRadio", edit_IdRadio.getText().toString());
        editor.putString("edit_IP", edit_IP.getText().toString());
        editor.putString("edit_Port", edit_Port.getText().toString());
        editor.putString("edit_PortKA", edit_PortKA.getText().toString());
        editor.putString("edit_TimerKA", edit_TimerKA.getText().toString());
        editor.putInt("edit_DuracionVideo", Integer.parseInt(edit_DuracionVideo.getText().toString()));
        editor.putBoolean("audioBool", toggleAudio.isChecked());
        editor.putBoolean("boolKA", toggle_ka.isChecked());
        editor.putBoolean("muteAlarm", switch_muteAlarm.isChecked());
        editor.putString("edit_User_Ftp", USER_FTP);
        editor.putString("edit_Pass_Ftp", PASS_FTP);
        editor.commit();
        Log.d(TAG, "Preferencias Guardadas , boolKA: " + mispreferencias.getBoolean("boolKA", true));

    }

    ///////////////////////MENU /////////////////////////////

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

            Toast.makeText(getApplicationContext(), "Correo del desarrollador:\n diegogiovanazzi@gmail.com", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}



