package com.example.diego.camara.Root;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * Created by diego on 14/09/15.
 */
public class Root extends AsyncTask<String,Void,Void> {


    private Context context = null;
    boolean suAvailable = false;
    //Created by themakeinfo.com,Promote us !!!


    public Root setContext(Context context) {
        this.context = context;
        return this;
    }


    protected Void doInBackground(String... params) {
        suAvailable = Shell.SU.available();
        if (suAvailable) {

            // suResult = Shell.SU.run(new String[] {"cd data; ls"}); Shell.SU.run("reboot");
            switch (params[0]){
                case "reboot"  : Shell.SU.run("reboot");break;
                case "recov"   : Shell.SU.run("reboot recovery");break;
                case "shutdown": Shell.SU.run("reboot -p");break;
                //case "sysui"   : Shell.SU.run("am startservice -n com.android.systemui/.SystemUIService");break;
                case "sysui"   : Shell.SU.run("pkill com.android.systemui");break;
            }
        }
        else{
            Toast.makeText(context, "Phone not Rooted", Toast.LENGTH_SHORT).show();
        }

        return null;
    }


}
