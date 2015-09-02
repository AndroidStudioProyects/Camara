package com.example.diego.camara.Actividades;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.diego.camara.Ftp.ConnectUploadAsync;
import com.example.diego.camara.R;

import java.io.File;

/**
 * Created by Diego on 30/05/2015.
 */
public class Lay_Ftp extends Activity {

        // connnectingwithFTP cliente;
        ConnectUploadAsync cliente;

        Button btn_Subir, btn_Conectar;
        EditText edit_ServerFtp,edit_Puerto,edit_User,edit_Pass;
        File Archivo;
        public TextView text_Bytes;
        String userName,pass,ip;

        public ProgressBar progressbar;



        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.lay_ftp);
            levantar_xml();
            Botones();
            CargarPreferenciasFTP();


        }

        @Override
        protected void onPause() {
            super.onPause();
            GuardarPreferenciasFTP();


        }

        @Override
        protected void onResume() {
            super.onResume();

            CargarPreferenciasFTP();
        }

        private void Botones() {

            btn_Conectar.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {

                    ip=edit_ServerFtp.getText().toString();
                    userName=edit_User.getText().toString();
                    pass=edit_Pass.getText().toString();


                }
            });


        }

        private void levantar_xml() {
            btn_Conectar= (Button) findViewById(R.id.btn_Conectar);
            btn_Subir= (Button) findViewById(R.id.btn_Subir);

            text_Bytes=(TextView)findViewById(R.id.text_Bytes);

            edit_ServerFtp=(EditText)findViewById(R.id.edit_ServerFtp);
            edit_Puerto=(EditText)findViewById(R.id.edit_Puerto);
            edit_User=(EditText)findViewById(R.id.edit_User);
            edit_Pass=(EditText)findViewById(R.id.edit_Pass);
            progressbar=(ProgressBar)findViewById(R.id.progressBar);
        }


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
                return true;
            }

            return super.onOptionsItemSelected(item);
        }


        ///////////////////// PREFERENCIAS DE USUARIO ////////////////
        public void CargarPreferenciasFTP(){

            SharedPreferences mispreferencias=getSharedPreferences("PreferenciasUsuarioFTP", Context.MODE_PRIVATE);
            edit_ServerFtp.setText(mispreferencias.getString("edit_IP_Ftp", "giovanazzi.dlinkddns.com"));
            edit_User.setText(mispreferencias.getString("edit_User_Ftp", "idirect"));
            edit_Pass.setText(mispreferencias.getString("edit_Pass_Ftp", "IDIRECT"));
            Log.d("Android_FTP", "Preferencias Cargadas");


        }

        public void GuardarPreferenciasFTP() {
            SharedPreferences mispreferencias = getSharedPreferences("PreferenciasUsuarioFTP", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mispreferencias.edit();
            editor.putString("edit_IP_Ftp", edit_ServerFtp.getText().toString());
            editor.putString("edit_User_Ftp", edit_User.getText().toString());
            editor.putString("edit_Pass_Ftp", edit_Pass.getText().toString());
            editor.commit();
            Log.d("Android_FTP", "Preferencias Almacenadas");

        }
// //////////////////////////////////////////////////////////
    }


