package com.kesbokar.kesbokar;


import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;

public class BusinessListDetailsPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    SliderView sliderView;

    String base_URL = "https://www.kesbokar.com.au/uploads/yellowpage/slider/";

    String business_id;
    String business_name;
    String finalURL;
    String business_detail;

    Context context;

    DrawerLayout drawer;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;

    Button logout,login,signup;
    TextView name;

    String loginId, loginPass, full_name, email, image, phone_no,created,updated;
    int id,flag;

    ImageView imgKesbokarLogo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_list_details_page);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);

        logout=header.findViewById(R.id.logout);
        login =header.findViewById(R.id.login);
        signup =header.findViewById(R.id.signup);
        name=header.findViewById(R.id.name_user);

        if(flag==1)
        {
            name.setText(full_name);
            logout.setVisibility(View.VISIBLE);
            login.setVisibility(View.INVISIBLE);
            signup.setVisibility(View.INVISIBLE);

        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag=0;
                SharedPreferences loginData= getSharedPreferences("data",0);
                SharedPreferences.Editor editor=loginData.edit();
                editor.putInt("Flag",flag);
                editor.apply();
                Intent intent=new Intent(BusinessListDetailsPage.this,Navigation.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        imgKesbokarLogo = findViewById(R.id.imgKesbokarLogo);
        imgKesbokarLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BusinessListDetailsPage.this, Navigation.class);
                startActivity(intent);
            }
        });


        sliderView = findViewById(R.id.imageSlider);

        Bundle intent=getIntent().getExtras();
        finalURL = intent.getString("URL");
        business_id = intent.getString("business_id");
        business_name =  intent.getString("business_name");


        //for business details list
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = "https://serv.kesbokar.com.au/jil.0.1/v2/yellowpages/?urlname="+business_name+"&id="+business_id+"&api_token=FSMNrrMCrXp2zbym9cun7phBi3n2gs924aYCMDEkFoz17XovFHhIcZZfCCdK";
        Log.i("Business api url", url.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    Log.i("Business Details",  response.toString());

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);


        //for auto-image slider related class slider adapter
        requestQueue = Volley.newRequestQueue(this);

        url = "http://serv.kesbokar.com.au/jil.0.1/v2/yellowpages/slider?urlname="+business_name+"&id="+business_id+"&api_token=FSMNrrMCrXp2zbym9cun7phBi3n2gs924aYCMDEkFoz17XovFHhIcZZfCCdK";
        Log.i("Response_url", url);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response)
            {
                Integer sliderLength=response.length();

                if (sliderLength>0) {

                    for (int i = 0; i < response.length(); i++){

                        Log.i("Response_image",  response.toString());

                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            Log.i("Response", jsonObject.getString("image"));

                            SharedPreferences sharedPreferences=getSharedPreferences("data",0);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putInt("slider_size",response.length());

                            for(int k=0;k<response.length();k++)
                            {
                                JSONObject obj=response.getJSONObject(k);
                                editor.putString("slider_"+k,base_URL+obj.getString("image")+"");
//                                Log.i("mySharedvalues",base_URL+obj.getString("image")+"");

                            }
                            editor.apply();

                            SliderAdapter adapter = new SliderAdapter(getApplicationContext());
                            sliderView.setSliderAdapter(adapter);

                            sliderView.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                            sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                            sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
                            sliderView.setIndicatorSelectedColor(Color.WHITE);
                            sliderView.setIndicatorUnselectedColor(Color.GRAY);
                            sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
                            sliderView.startAutoCycle();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                } else {

                    try {

                        SharedPreferences sharedPreferences = getSharedPreferences("data", 0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("slider_size", 3);
                        editor.putString("slider_0", "https://www.kesbokar.com.au/uploads/yellowpage/slider/defult-slider-730x410.jpg");
                        editor.putString("slider_1", "https://www.kesbokar.com.au/uploads/yellowpage/slider/defult-slider2-730x410.jpg");
                        editor.putString("slider_2", "https://www.kesbokar.com.au/uploads/yellowpage/slider/defult-slider3-730x410.jpg");
                        editor.apply();


                        SliderAdapter adapter = new SliderAdapter(getApplicationContext());
                        sliderView.setSliderAdapter(adapter);

                        sliderView.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
                        sliderView.setIndicatorSelectedColor(Color.WHITE);
                        sliderView.setIndicatorUnselectedColor(Color.GRAY);
                        sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
                        sliderView.startAutoCycle();

                    } catch (Exception e) { e.printStackTrace(); }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(jsonArrayRequest);

    }



    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int Id = item.getItemId();

        if (Id == R.id.nav_share) {
            if (flag==1){
                Intent about=new Intent(BusinessListDetailsPage.this,Main3BusinessActivity.class);
                startActivity(about);
            } else {
                Intent intent = new Intent(BusinessListDetailsPage.this, Login.class);
                startActivity(intent);
            }

        } else if (Id == R.id.business_lg_page) {
            if (flag==1) {
                Intent intent=new Intent(BusinessListDetailsPage.this,ProfileBusinessListing.class);
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
                Intent intent = new Intent(BusinessListDetailsPage.this, Login.class);
                startActivity(intent);
            }

        } else if (Id == R.id.nav_send) {

            if (flag==1){
                Intent about=new Intent(BusinessListDetailsPage.this,ProductManagementActivity.class);
                startActivity(about);
            } else {
                Intent intent = new Intent(BusinessListDetailsPage.this, Login.class);
                startActivity(intent);
            }

        } else if (Id == R.id.market_lg_page) {

            if (flag==1) {
                Intent intent=new Intent(BusinessListDetailsPage.this,ProfileMarket.class);
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
                Intent intent = new Intent(BusinessListDetailsPage.this, Login.class);
                startActivity(intent);
            }


        } else if (Id == R.id.business_in) {

            if (flag==1){
                Intent intent=new Intent(BusinessListDetailsPage.this,inbox_business.class);
                startActivity(intent);

            } else {
                Intent intent = new Intent(BusinessListDetailsPage.this, Login.class);
                startActivity(intent);
            }


        } else if (Id == R.id.market_in) {

            if (flag==1){
                Intent intent=new Intent(BusinessListDetailsPage.this,inbox_market.class);
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
                Intent intent = new Intent(BusinessListDetailsPage.this, Login.class);
                startActivity(intent);
            }

        } else if (Id == R.id.profile) {

            if (flag==1) {
                Intent intent = new Intent(BusinessListDetailsPage.this, Profile.class);
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
                Intent intent = new Intent(BusinessListDetailsPage.this, Login.class);
                startActivity(intent);
            }

        } else if(Id == R.id.manage_help_desk) {

            if (flag==1) {
                Intent intent = new Intent(BusinessListDetailsPage.this, ManageHelpDeskActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(BusinessListDetailsPage.this, Login.class);
                startActivity(intent);
            }

        } else if (Id == R.id.about)
        {


            Intent intent = new Intent(BusinessListDetailsPage.this, About.class);
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




        } else if (Id == R.id.career)
        {

            Intent intent = new Intent(BusinessListDetailsPage.this, Career.class);
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


        } else if (Id == R.id.advertise) {

        } else if (Id == R.id.loginPage) {

            if (flag==1){
                Intent intent=new Intent(BusinessListDetailsPage.this,LoginData.class);
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
                Intent intent = new Intent(BusinessListDetailsPage.this, Login.class);
                startActivity(intent);
            }

        } else if(Id == R.id.dashboard) {

            if (flag==1) {
                Intent intent = new Intent(BusinessListDetailsPage.this, Navigation.class);
                startActivity(intent);
            }

            else {
                Intent intent = new Intent(BusinessListDetailsPage.this, Login.class);
                startActivity(intent);
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
