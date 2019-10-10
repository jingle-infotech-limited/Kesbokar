package com.kesbokar.kesbokar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    public static int SPLASH_SCREEN_TIME=1500;
    String versionUpdated,version_name;
    String cdn_url="https://cdn.kesbokar.com.au/";
    String base_url="https://www.kesbokar.com.au/";
    String api_url="http://serv.kesbokar.com.au/jil.0.1/";
    String api_topken="FSMNrrMCrXp2zbym9cun7phBi3n2gs924aYCMDEkFoz17XovFHhIcZZfCCdK";
                       //FSMNrrMCrXp2zbym9cun7phBi3n2gs924aYCMDEkFoz17XovFHhIcZZfCCdK

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences=getSharedPreferences("data",0);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("cdn_url",cdn_url);
        editor.putString("base_url",base_url);
        editor.putString("api_url",api_url);
        editor.putString("api_token",api_topken);
        editor.apply();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (isNetworkAvailable()) {
            try {
                versionChecker VersionChecker = new versionChecker();
                versionUpdated = VersionChecker.execute().get();
                Log.i("version code is", versionUpdated);


                PackageInfo packageInfo = null;
                try {
                    packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                int version_code = packageInfo.versionCode;
                version_name = packageInfo.versionName;
                Log.i("updated version code", version_code + "  " + version_name);
                if (!version_name.equals(versionUpdated)) {
                    String packageName = getApplicationContext().getPackageName();//
                    UpdateMeeDialog updateMeeDialog = new UpdateMeeDialog();
                    updateMeeDialog.showDialogAddRoute(MainActivity.this, packageName);
                    Toast.makeText(getApplicationContext(), "New Update is Available", Toast.LENGTH_LONG).show();
                } else {
                    setContentView(R.layout.activity_main);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(MainActivity.this, Navigation.class);
                            startActivity(i);
                            finish();
                        }
                    }, SPLASH_SCREEN_TIME);
                }
            } catch (Exception e) {
                e.getStackTrace();
                Log.i("Error", e.toString());
            }
        }
        else {
            setContentView(R.layout.activity_main);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(MainActivity.this, Navigation.class);
                    startActivity(i);
                    finish();
                }
            }, SPLASH_SCREEN_TIME);
        }

    }
    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public void getData(){

    }

}
