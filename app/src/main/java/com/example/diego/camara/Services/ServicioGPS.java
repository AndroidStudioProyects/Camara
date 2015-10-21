package com.example.diego.camara.Services;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

import com.example.diego.camara.Actividades.MainActivity;

/**
 * Created by diego on 15/09/15.
 */
public class ServicioGPS extends Service implements LocationListener {

    private final Context ctx;
    private TextView texto;
    double latitud = 0;
    double longitud = 0;
    Location location;
    boolean gpsActivo;
    LocationManager locationManager;

    public ServicioGPS() {
        super();
        this.ctx = this.getApplicationContext();
    }

    public ServicioGPS(Context c) {
        super();
        this.ctx = c;
        getLocation();

    }

    private void getLocation() {
        try {
            locationManager = (LocationManager) this.ctx.getSystemService(LOCATION_SERVICE);
            gpsActivo = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
        }
        if (gpsActivo) {   //LatyLong=servicio.LatyLong();

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 60, 10, this);
            location=locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
            latitud=location.getLatitude();
            KeepAlive.Lat=String.valueOf(latitud);
            MainActivity.Latitud=String.valueOf(latitud);

            longitud=location.getLongitude();
            KeepAlive.Long=String.valueOf(longitud);
            MainActivity.Longitud=String.valueOf(longitud);

            Log.d("GPS","Esta activo");
        }else{

            Log.d("GPS","No esta activo");

        }
    }

    public String LatyLong(){
    String geolocation="0,0";

        geolocation=String.valueOf(latitud)+","+String.valueOf(longitud);
    return geolocation;

    }

    public void setView(TextView v){
        texto=v;
        texto.setText("Coordenadas: "+latitud+","+longitud);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onLocationChanged(Location location) {


        getLocation();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
