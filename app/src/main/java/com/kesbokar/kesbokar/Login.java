package com.kesbokar.kesbokar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.toolbox.HttpClientStack;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

public class Login extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    int flag=0;
    ProgressDialog progressDialog;
    EditText edtLoginId, edtLoginPass;
    String loginId, loginPass, full_name, email, image, phone_no,created,updated;
    int id;
    SharedPreferences loginData;
    private static final int LOADER_LOGIN_ID = 35;
    private LoaderManager.LoaderCallbacks<LoginInfo> login_info_loader;
    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    DrawerLayout drawer;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!isNetworkAvailable())
        {
            setContentView(R.layout.no_internet);
        }
        else {
            setContentView(R.layout.activity_login);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        edtLoginId = findViewById(R.id.edtLoginId);
        edtLoginPass = findViewById(R.id.edtLoginPass);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        loginId = "";
        loginPass = "";

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.kesbokar));
            //If true set flag =1
        }


        Button logbut=findViewById(R.id.logbut);
        logbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()) {
                    loginId = edtLoginId.getText().toString();
                    loginPass = edtLoginPass.getText().toString();
                    progressDialog = new ProgressDialog(Login.this);
                    progressDialog.setMessage("Logging In...");
                    progressDialog.show();
                    getLoaderManager().restartLoader(LOADER_LOGIN_ID, null, login_info_loader);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            if (flag == 1) {
                                onBackPressed();
                            } else {
                                Toast.makeText(Login.this, "Invalid Email or Password.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, 4000);
                }else{
                    setContentView(R.layout.no_internet);
                }
            }
        });

        login_info_loader = new LoaderManager.LoaderCallbacks<LoginInfo>() {
            @Override
            public Loader<LoginInfo> onCreateLoader(int i, Bundle bundle) {
                LoaderLogin loaderLogin = new LoaderLogin(Login.this,loginId,loginPass,"http://serv.kesbokar.com.au/jil.0.1/auth/login");
                return loaderLogin;
            }

            @Override
            public void onLoadFinished(Loader<LoginInfo> loader, LoginInfo loginInfos) {
                if(loginInfos!=null) {
                    flag = 1;
                    full_name = loginInfos.getFullName();
                    email = loginInfos.getEmail_id();
                    image = loginInfos.getImage();
                    phone_no = loginInfos.getPhone_no();
                    created=loginInfos.getCreated();
                    updated=loginInfos.getUpdated();
                    id=loginInfos.getid();
                    Log.i("Login_data", loginInfos + "");
                    saveData();
                }else{
                    flag=0;
                }

                Log.i("FlagValue",flag + "");
            }

            @Override
            public void onLoaderReset(Loader<LoginInfo> loader) {

            }
        };
        getLoaderManager().initLoader(LOADER_LOGIN_ID,null,login_info_loader);
    }


    @Override
    public void onBackPressed() {
        Intent intent=new Intent(Login.this,Navigation.class);
//        intent.putExtra("Flag", flag);
//        intent.putExtra("Name",full_name);
//        intent.putExtra("mail",email);
//        intent.putExtra("image",image);
//        intent.putExtra("phone",phone_no);
//        intent.putExtra("create",created);
//        intent.putExtra("update",updated);
//        intent.putExtra("id",id);
        startActivity(intent);
        finish();
    }

    public void saveData()
    {
        loginData= getSharedPreferences("data",0);
        SharedPreferences.Editor editor=loginData.edit();
        editor.putInt("Flag",flag);
        editor.putString("Name",full_name);
        editor.putString("mail",email);
        editor.putString("image",image);
        editor.putString("phone",phone_no);
        editor.putString("create",created);
        editor.putString("update",updated);
        editor.putInt("id",id);
        editor.apply();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int Id = item.getItemId();

        if (Id == R.id.nav_share) {
            if (flag==1){
                Intent about=new Intent(Login.this,Main3BusinessActivity.class);
                startActivity(about);
            } else {
                Intent intent = new Intent(Login.this, Login.class);
                startActivity(intent);
            }

        } else if (Id == R.id.business_lg_page) {
            if (flag==1) {
                Intent intent=new Intent(Login.this,ProfileBusinessListing.class);
                intent.putExtra("Flag",flag);
                intent.putExtra("Name",full_name);
                intent.putExtra("mail",email);
                intent.putExtra("image",image);
                intent.putExtra("phone",phone_no);
                intent.putExtra("create",created);
                intent.putExtra("update",updated);
                intent.putExtra("id",id);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent, 0);
                overridePendingTransition(0, 0);


            } else {
                Intent intent = new Intent(Login.this, Login.class);
                startActivity(intent);
            }

        } else if (Id == R.id.nav_send) {

            if (flag==1){
                Intent about=new Intent(Login.this,ProductManagementActivity.class);
                startActivity(about);
            } else {
                Intent intent = new Intent(Login.this, Login.class);
                startActivity(intent);
            }

        } else if (Id == R.id.market_lg_page) {

            if (flag==1) {
                Intent intent=new Intent(Login.this,ProfileMarket.class);
                intent.putExtra("Flag",flag);
                intent.putExtra("Name",full_name);
                intent.putExtra("mail",email);
                intent.putExtra("image",image);
                intent.putExtra("phone",phone_no);
                intent.putExtra("create",created);
                intent.putExtra("update",updated);
                intent.putExtra("id",id);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent, 0);
                overridePendingTransition(0, 0);

            } else {
                Intent intent = new Intent(Login.this, Login.class);
                startActivity(intent);
            }


        } else if (Id == R.id.business_in) {

            if (flag==1){
                Intent intent=new Intent(Login.this,inbox_business.class);
                startActivity(intent);

            } else {
                Intent intent = new Intent(Login.this, Login.class);
                startActivity(intent);
            }


        } else if (Id == R.id.market_in) {

            if (flag==1){
                Intent intent=new Intent(Login.this,inbox_market.class);
                intent.putExtra("Flag",flag);
                intent.putExtra("Name",full_name);
                intent.putExtra("mail",email);
                intent.putExtra("image",image);
                intent.putExtra("phone",phone_no);
                intent.putExtra("create",created);
                intent.putExtra("update",updated);
                intent.putExtra("id",id);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent, 0);
                overridePendingTransition(0, 0);

            } else {
                Intent intent = new Intent(Login.this, Login.class);
                startActivity(intent);
            }

        } else if (Id == R.id.profile) {

            if (flag==1) {
                Intent intent = new Intent(Login.this, Profile.class);
                intent.putExtra("Flag",flag);
                intent.putExtra("Name",full_name);
                intent.putExtra("mail",email);
                intent.putExtra("image",image);
                intent.putExtra("phone",phone_no);
                intent.putExtra("create",created);
                intent.putExtra("update",updated);
                intent.putExtra("id",id);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent, 0);
                overridePendingTransition(0, 0);

            } else {
                Intent intent = new Intent(Login.this, Login.class);
                startActivity(intent);
            }

        } else if(Id == R.id.manage_help_desk) {

            if (flag==1) {
                Intent intent = new Intent(Login.this, ManageHelpDeskActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(Login.this, Login.class);
                startActivity(intent);
            }

        } else if (Id == R.id.about) {

            if (flag==1){
                Intent intent = new Intent(Login.this, About.class);
                intent.putExtra("Flag", flag);
                intent.putExtra("Name",full_name);
                intent.putExtra("mail",email);
                intent.putExtra("image",image);
                intent.putExtra("phone",phone_no);
                intent.putExtra("create",created);
                intent.putExtra("update",updated);
                intent.putExtra("id",id);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent, 0);
                overridePendingTransition(0, 0);


            } else {
                Intent intent = new Intent(Login.this, Login.class);
                startActivity(intent);
            }

        } else if (Id == R.id.career) {

            if (flag==1){
                Intent intent = new Intent(Login.this, Career.class);
                intent.putExtra("Flag", flag);
                intent.putExtra("Name",full_name);
                intent.putExtra("mail",email);
                intent.putExtra("image",image);
                intent.putExtra("phone",phone_no);
                intent.putExtra("create",created);
                intent.putExtra("update",updated);
                intent.putExtra("id",id);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent, 0);
                overridePendingTransition(0, 0);


            } else {
                Intent intent = new Intent(Login.this, Login.class);
                startActivity(intent);
            }


        } else if (Id == R.id.loginPage) {

            if (flag==1){
                Intent intent=new Intent(Login.this,LoginData.class);
                intent.putExtra("Flag", flag);
                intent.putExtra("Name",full_name);
                intent.putExtra("mail",email);
                intent.putExtra("image",image);
                intent.putExtra("phone",phone_no);
                intent.putExtra("create",created);
                intent.putExtra("update",updated);
                intent.putExtra("id",id);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent, 0);
                overridePendingTransition(0, 0);

            }

            else {
                Intent intent = new Intent(Login.this, Login.class);
                startActivity(intent);
            }

        } else if(Id == R.id.dashboard) {

            if (flag==1) {
                Intent intent = new Intent(Login.this, Navigation.class);
                startActivity(intent);
            }

            else {
                Intent intent = new Intent(Login.this, Login.class);
                startActivity(intent);
            }
        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
