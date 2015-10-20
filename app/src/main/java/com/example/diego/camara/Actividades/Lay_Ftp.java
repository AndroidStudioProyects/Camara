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
import android.widget.Toast;

import com.example.diego.camara.Ftp.ConnectUploadAsync;
import com.example.diego.camara.R;

import java.io.File;

/**
 * Created by Diego on 30/05/2015.
 */
public class Lay_Ftp extends Activity {

        // connnectingwithFTP cliente;
        ConnectUploadAsync cliente;

        Button btn_Guardar;
        EditText edit_User,edit_Pass;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.lay_ftp);
            levantar_xml();
            Botones();


        }



        @Override
        protected void onResume() {
            super.onResume();
            CargarPreferenciasFTP();


        }



        private void Botones() {

            btn_Guardar.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {
                    GuardarPreferenciasFTP();

                }
            });


        }

        private void levantar_xml() {

            edit_User=(EditText)findViewById(R.id.edit_User);
            edit_Pass=(EditText)findViewById(R.id.edit_Pass);

            btn_Guardar=(Button)findViewById(R.id.btn_Guardar);
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

            SharedPreferences mispreferencias = getSharedPreferences("PreferenciasUsuario", Context.MODE_PRIVATE);
        edit_User.setText(mispreferencias.getString("User_Ftp", "anonimo"));
            edit_Pass.setText(mispreferencias.getString("Pass_Ftp", "anonimo"));
            Log.d("Android_FTP", "Preferencias Cargadas");


        }

        public void GuardarPreferenciasFTP() {
            SharedPreferences mispreferencias = getSharedPreferences("PreferenciasUsuario", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mispreferencias.edit();
           editor.putString("User_Ftp", edit_User.getText().toString());
            editor.putString("Pass_Ftp", edit_Pass.getText().toString());
            editor.commit();
            Log.d("Android_FTP", "Preferencias Almacenadas");
            Toast.makeText(getApplicationContext(),"Preferencias FTP Almacenadas",Toast.LENGTH_SHORT).show();
        }
// //////////////////////////////////////////////////////////
    }


