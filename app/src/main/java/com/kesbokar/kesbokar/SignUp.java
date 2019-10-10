package com.kesbokar.kesbokar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.internal.TaskUtil;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.text.Html;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    private EditText etFirstName, etLastName, etEmail, etPhoneNumber, etPassword, etConfirmPassword;
    private Button btnSignUp;

    String data,api_url,api_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        final Button btnSignUp = findViewById(R.id.btnSignUp);

        getData();
        btnSignUp.setVisibility(View.VISIBLE);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.kesbokar));
        }

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData();

                if (etEmail.getText().toString().equals("") && etFirstName.getText().toString().equals("") && etLastName.getText().toString().equals("") && etPassword.getText().toString().equals(""))
                {
                    etEmail.setError("Can't Submit empty fields");
                    etFirstName.setError("Can't Submit empty fields");
                    etLastName.setError("Can't Submit empty fields");
                    etPassword.setError("Can't Submit empty fields");
                    etConfirmPassword.setError("Can't Submit empty fields");


                } else {
                    etEmail.setError("");
                    etFirstName.setError("");
                    etLastName.setError("");
                    etPassword.setError("");
                    etConfirmPassword.setError("");
                    btnSignUp.setVisibility(View.VISIBLE);
                    if (etPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
                        setContentView(R.layout.sign_up_reply);
                        TextView txtSignUpReply = findViewById(R.id.txtSignUpReply);

                        txtSignUpReply.setText("Thank you for creating an account.\n\n" +
                                "Your account is successfully created. We have sent you an activation link e-mail to \n");
                        String sourceString = "<b>"+ etEmail.getText().toString()+"</b> ";
                      //  mytextview.setText(Html.fromHtml(sourceString));
                        txtSignUpReply.append(Html.fromHtml(sourceString));
                       // txtSignUpReply.append(etEmail.getText().toString());
                        txtSignUpReply.append(". \nIf clicking the link does not work, please copy and paste the URL into your browser instead. " +
                                "\nIf you do not receive an email within 5 minutes, " +
                                "Please check your spam / junk email folder in case the email has been received there and click Report not spam.");
                        Snackbar.make(view, "Kindly Check Your Email", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent i = new Intent(SignUp.this, Navigation.class);
                                startActivity(i);
                                finish();
                            }
                        }, 8000);

                        String url = api_url+"auth/register";

                        RequestQueue requestQueue = Volley.newRequestQueue(SignUp.this);

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.i("Response", response);
                                try {
                                    JSONObject jsonObject = new JSONObject(response);

                                    SharedPreferences get_sign_up_data = SignUp.this.getSharedPreferences("data", 0);
                                    SharedPreferences.Editor editor = get_sign_up_data.edit();
                                    editor.putString("data", data);
                                    editor.apply();


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() {
                                getData();
                                Map<String, String> params = new HashMap<String, String>();
                                JSONObject onj1 = new JSONObject();

                                try {
                                    onj1.put("first_name", etFirstName.getText().toString());
                                    onj1.put("last_name", etLastName.getText().toString());
                                    onj1.put("email", etEmail.getText().toString());
                                    onj1.put("phone", etPhoneNumber.getText().toString());
                                    onj1.put("password", etConfirmPassword.getText().toString());
                                    onj1.put("image", "avatar.png");


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                params.put("data", onj1.toString());

                                params.put("api_token", api_token);
                                return params;
                            }
                        };
                        requestQueue.add(stringRequest);
                    } else
                        {
                        Toast.makeText(SignUp.this, "Password Does Not Match", Toast.LENGTH_SHORT).show();
                        }
                }

                }


        });
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(SignUp.this,Navigation.class);
        startActivity(intent);
        finish();
    }

    private void getData() {
        SharedPreferences loginData=getSharedPreferences("data",0);
        api_url=loginData.getString("api_url","");
        api_token=loginData.getString("api_token","");

    }
}
