package com.example.diego.camara.Actividades;

import android.app.Activity;
import android.os.Bundle;

import com.example.diego.camara.R;

/**
 * Created by Diego on 14/05/2015.
 */
public class Lay_Configuracion extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_configuracion);
        LevantarXML();
        Botones();
    }
    private void LevantarXML() {
    }
    private void Botones() {

    }

}
