package com.kesbokar.kesbokar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Base64DataException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.internal.SignInButtonImpl;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.select.Evaluator;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;


public class Login extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    int flag=0;
    ProgressDialog progressDialog;
    EditText edtLoginId, edtLoginPass;
    String loginId, loginPass, full_name, email, image, phone_no,created,updated,api_url,api_token;

    String personName, personEmail, personID;

    TextView name, create_an_account;
    Button login, logout, signup;

    private LoginButton loginButton;
    private CallbackManager callbackManager;

    private static final String EMAIL = "email";

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

    SignInButton signInButton;
    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.kesbokar.kesbokar",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
              //  Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

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

        View ab = navigationView.getHeaderView(0);
        signup = ab.findViewById(R.id.signup);
        login = ab.findViewById(R.id.login);
        logout=ab.findViewById(R.id.logout);
        name= ab.findViewById(R.id.name_user);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.kesbokar));
            //If true set flag =1
        }
//      Configure sign-in to request the user's ID, email address, and basic
//      profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

//        Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.sign_in_button:
                        signIn();
                        break;
                    // ...
                }
            }
        });

        create_an_account = findViewById(R.id.create_an_account);
        create_an_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new  Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });

        callbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.login_button);

        loginButton.setReadPermissions(Arrays.asList(EMAIL));
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                // App code
                final AccessToken accessToken = loginResult.getAccessToken();

                Toast.makeText(Login.this, "Hello", Toast.LENGTH_SHORT).show();

                final GraphRequest graphRequest=GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(final JSONObject object, GraphResponse response) {
                        getData();


                        Log.i("Response", response.toString());

                        String url1 = api_url+"auth/login/facebook";

                        RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, url1, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

//                                Log.e("Response_facebook", response.toString());

                                try {
                                    JSONObject jsonObject1 = new JSONObject(response);
                                 //   Log.i("Response",  jsonObject1.getString("data"));

                                    JSONObject jsonObject2 = new JSONObject(jsonObject1.getString("data"));
                                  //  Log.i("Response",  jsonObject2.getString("id"));

                                    if(jsonObject2!=null) {
                                        flag = 1;
                                        full_name = object.getString("name");
                                        email = jsonObject2.getString("email");
                                        image = jsonObject2.getString("image");
                                        phone_no = jsonObject2.getString("phone");
                                        id=jsonObject2.getInt("id");
                                        saveData();
                                        ResetLeftMenu();
                                    }else{
                                        flag=0;
                                    }

                                } catch (Exception e){

                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.i("Error",error.toString());
                            }
                        }){
                            @Override
                            protected Map<String, String> getParams(){
                                Map<String, String> params = new HashMap<String, String>();

                                try {
                                    params.put("email", object.getString("email"));
                                    params.put("name", object.getString("name"));
                                    params.put("provider_id", object.getString("id"));
                                    params.put("token", "");
                                    params.put("provider", "facebook");

                                    if (object!=null){
                                        params.put("avatar",  "https://graph.facebook.com/"+object.getString("id")+"/picture?type=normal");
                                    } else {
                                        params.put("avatar", "");
                                    }

                                }catch (JSONException e){
                                    System.out.println(e);
                                }
                                getData();
                                params.put("api_token", api_token);
                                return params;
                            }
                        };
                        requestQueue.add(stringRequest);
//                        flag = 1;
                        saveData();
//                        Intent intent = new Intent(Login.this, Navigation.class);
//                        startActivity(intent);
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "name,email,id");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code

            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

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
                getData();

                LoaderLogin loaderLogin = new LoaderLogin(Login.this, loginId, loginPass,api_url+"auth/login");
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

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.

            final String personName = account.getDisplayName();
//            String personGivenName = acct.getGivenName();
//            String personFamilyName = acct.getFamilyName();
            final String personEmail = account.getEmail();
            final String personId = account.getId();
            final Uri personPhoto = account.getPhotoUrl();
//            final String authToken = account.getIdToken();

            String url;
            getData();
            url = api_url+"auth/login/google";

            RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());

            final StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
//                    Log.i("Response", response.toString());

                    try {
                        JSONObject jsonObject1 = new JSONObject(response);
                        Log.i("Response",  jsonObject1.getString("data"));

                        JSONObject jsonObject2 = new JSONObject(jsonObject1.getString("data"));
//                        Log.i("Response",  jsonObject2.getString("id"));

                        if(jsonObject2!=null) {
                            flag = 1;
                            full_name = jsonObject2.getString("first_name")+" "+jsonObject2.getString("last_name");
                            email = jsonObject2.getString("email");
                            image = jsonObject2.getString("image");
                            phone_no = jsonObject2.getString("phone");
                            id=jsonObject2.getInt("id");
                            saveData();
                            ResetLeftMenu();
                        }else{
                            flag=0;
                        }

                    } catch (Exception e){

                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("Error",error.toString());
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    getData();
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("name", personName);
                    params.put("email", personEmail);
                    params.put("provider_id", personId);
                    params.put("token", "");
                    params.put("provider", "google");
                    if (personPhoto != null){
                        params.put("avatar", personPhoto.toString());
                    } else {
                        params.put("avatar", "");
                    }
                    getData();
                    params.put("api_token", api_token);
                    return params;
                }
            };
            requestQueue.add(stringRequest);
            flag=1;

            if (account != null){

//              startActivity(new Intent(MainActivity.this, Main2Activity.class));
                Menu show=navigationView.getMenu();

                if(flag==1) {

                    name.setText(full_name);
                    full_name=personName;

                    login.setVisibility(View.INVISIBLE);
                    signup.setVisibility(View.INVISIBLE);
                    show.findItem(R.id.nav_send).setVisible(true);
                    show.findItem(R.id.nav_share).setVisible(true);
                    show.findItem(R.id.advertise).setVisible(false);
                    show.findItem(R.id.loginPage).setVisible(true);
                    logout.setVisibility(View.VISIBLE);
                }
                saveData();
                Intent intent = new Intent(Login.this, Navigation.class);
                startActivity(intent);
            }

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("TAG",  "signInResult:failed code=" + e.getStatusCode());
//            updateUI(null);

        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        //Check for existing Google Sign In account, If the user is already signed in
        //The Google Sign in Account will be non null
        //This is used to Get Profile Information
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (account != null){

//          startActivity(new Intent(MainActivity.this, Main2Activity.class));
//            Intent intent = new Intent(Login.this, Navigation.class);
//            startActivity(intent);
        }

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
        loginData = getSharedPreferences("data",0);
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

    public void ResetLeftMenu(){

        Menu show=navigationView.getMenu();

        if(flag==1) {

            name.setText(full_name);
            login.setVisibility(View.INVISIBLE);
            signup.setVisibility(View.INVISIBLE);
            show.findItem(R.id.nav_send).setVisible(true);
            show.findItem(R.id.nav_share).setVisible(true);
            show.findItem(R.id.advertise).setVisible(false);
            show.findItem(R.id.loginPage).setVisible(true);
            logout.setVisibility(View.VISIBLE);
        }
        Intent intent = new Intent(Login.this, Navigation.class);
        startActivity(intent);

        return;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int Id = item.getItemId();

        if (Id == R.id.nav_share) {
            if (flag==1){
                Intent about = new Intent(Login.this,Main3BusinessActivity.class);
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
    void getData()
    {
        SharedPreferences loginData=getSharedPreferences("data",0);
        api_url=loginData.getString("api_url","");
        api_token=loginData.getString("api_token","");


    }

}
