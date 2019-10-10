package com.kesbokar.kesbokar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import android.app.Activity;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class DataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Activity mActivity;
    private Context context;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    EditText name,email,phone,details;
    String loginId, loginPass, full_name, email1, image, phone_no,created,updated,cdn_url,base_url,api_url,api_token;
    int Id,flag;
    private ArrayList<ExampleItem> exampleItems;
    String ip;
    int id;
    private ProgressDialog progressDialog;
    SharedPreferences loginData;
    String url;
    private int isLoggedIn;
    public DataAdapter(Buisness_Listing activity , ArrayList<ExampleItem> exampleList, int flag, SharedPreferences logindata) {
        this.mActivity = activity;
        exampleItems = exampleList;
        isLoggedIn = flag;
        loginData=logindata;
        getData();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setTitle("Loading...");

        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_business_listing, parent, false);
            return new MyViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof MyViewHolder) {

            populateItemRows((MyViewHolder) viewHolder, position);
            progressDialog.dismiss();

            //Request A Quote

        } else if (viewHolder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) viewHolder, position);
        }

    }

    private View.OnClickListener onCancelListener(final Dialog dialog) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        };
    }



    private View.OnClickListener onConfirmListener(final EditText name, final EditText email, final EditText phone, final EditText details, final String[] preference, final Dialog dialog , final int id2, final String url_name) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
                final String Name=name.getText().toString();
                final String Email=email.getText().toString();
                final String Phone=phone.getText().toString();
                final String Quotes=details.getText().toString();
                RequestQueue queue= Volley.newRequestQueue(mActivity);
                final String url=api_url+"v2/yellowpages/enquiry";
                try {
                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                    NetworkInterface intf = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            ip = Formatter.formatIpAddress(inetAddress.hashCode());
                           // Log.i("response", "***** IP="+ ip);
                        }
                    }
                }
            } catch (SocketException ex) {
                //Log.i("error", ex.toString());
                ip="000.000.000.000";
            }

                StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(mActivity, "Response"+"Query submitted", Toast.LENGTH_SHORT).show();
                       // Log.i("Response",response);

                    }
                }, new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // errorLog.d("Error.Response", String.valueOf(error));
                            Toast.makeText(mActivity, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams()
                    {
                        getData();
                        Map<String, String> params = new HashMap<String, String>();
                        if(flag==0) {
                            String id1 = "" + id2;
                            params.put("name", Name);
                            params.put("email", Email);
                            params.put("mobile", Phone);
                            params.put("message", Quotes);
                            params.put("ipaddress", ip);
                            params.put("urlname", url_name);
                            params.put("yellowpage_id", id1);
                            params.put("user_id", "");
                            if(preference[0]!=null){
                                params.put("preferred_method", preference[0]);}
                            else {
                                params.put("preferred_method", "no preference");
                            }
                            params.put("api_token", api_token);
                        }
                        if(flag==1){
                            String id1 = "" + id2;
                            String user_id=""+Id;
                            params.put("name", full_name);
                            params.put("email",email1);
                            params.put("mobile", phone_no);
                            params.put("message", Quotes);
                            params.put("ipaddress", ip);
                            params.put("urlname", url_name);
                            params.put("yellowpage_id", id1);
                            params.put("user_id", user_id);
                            if(preference[0]!=null){
                            params.put("preferred_method", preference[0]);}
                            else {
                                params.put("preferred_method", "no preference");
                            }
                            params.put("api_token", api_token);

                        }

                        return params;
                    }
                };
                RequestQueue requestQueue= Volley.newRequestQueue(mActivity);
                requestQueue.add(stringRequest);
                dialog.dismiss();
            }

        };
        
    }


    @Override
    public int getItemViewType(int position) {
        return exampleItems.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
       // private TextView bln,bls,bld,heading_text;
        private TextView business_name,business_city,business_category,business_intro;
        RatingBar blr;
        ImageView bli;
        LinearLayout clickable;
        private Button blw;
        Button blrq;
        public MyViewHolder(@NonNull View view) {
            super(view);
            business_name=view.findViewById(R.id.business_name);
            business_city=view.findViewById(R.id.business_city);
            business_category= view.findViewById(R.id.business_category);
            business_intro=view.findViewById(R.id.business_intro);
            progressBar=view.findViewById(R.id.progressBar);
            clickable=view.findViewById(R.id.clickable);
            //url1=view.findViewById(R.id.url);
//            bld=view.findViewById(R.id.bld);
            bli=view.findViewById(R.id.bli);
            //heading_text=view.findViewById(R.id.heading);
            blr=view.findViewById(R.id.blr);
            blrq=view.findViewById(R.id.blrq);
//            blw=view.findViewById(R.id.blw);
        }
    }
    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }


    @Override
    public int getItemCount() {
       // Log.i("exampleItems.size()",exampleItems.size()+"");
        return exampleItems == null ? 0 : exampleItems.size();

    }
    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed

    }
    private void populateItemRows(MyViewHolder holder, int position) {
        final ExampleItem current=exampleItems.get(position);
        getData();
       // Log.i("current.getUrl()",current.getUrl());
     //   Log.i("DA: ",current.getBusi_name()+ ",,"+current.getBusi_synop()+",,"+current.getCity()+",,"+current.getHeading());
        String image=cdn_url+"uploads/yellowpage/"+current.getImg();
        String bName=current.getBusi_name();
        String bSynop=current.getBusi_synop();
        String city=current.getCity();
    String category=current.getCategory();
    String state=current.getState();
        String heading=current.getHeading();
        url=current.getUrl();
        id=current.getId();
        holder.business_name.setText(bName);
        holder.business_city.setText(city +","+state);
        holder.business_category.setText(category);
        holder.business_intro.setText(bSynop);
        float ratings=(float)current.getratings();
        holder.blr.setRating(ratings);
        if(current.getImg()==null)
        {
            holder.bli.setImageResource(R.drawable.def);
        }
        else if (image.equals(cdn_url+"uploads/yellowpage/null"))
        {
            holder.bli.setImageResource(R.drawable.def);
        }
        else if(current.getImg()!=null) {
            Picasso.with(mActivity).load(image).fit().centerInside().into(holder.bli);
        }
        else {
            holder.bli.setImageResource(R.drawable.def);
        }
        holder.bli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
                String finalUrl=base_url+"business/"+current.getCity()+"/"+current.getUrl()+"/"+current.getId();
                SharedPreferences get_product_detail= mActivity.getSharedPreferences("entry",0);
                SharedPreferences.Editor editor=get_product_detail.edit();
                editor.putString("entry_level","1");
                editor.apply();
                Intent intent = new Intent(mActivity, WebViewActivity.class);
                intent.putExtra("URL", finalUrl);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                mActivity.startActivityForResult(intent,0);
                mActivity.overridePendingTransition(0,0);

            }
        });
        holder.business_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
                SharedPreferences get_product_detail= mActivity.getSharedPreferences("entry",0);
                SharedPreferences.Editor editor=get_product_detail.edit();
                editor.putString("entry_level","1");
                editor.apply();
                String finalUrl=base_url+"business/"+current.getCity()+"/"+current.getUrl()+"/"+current.getId();
                Intent intent = new Intent(mActivity, WebViewActivity.class);
                intent.putExtra("URL", finalUrl);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                mActivity.startActivityForResult(intent,0);
                mActivity.overridePendingTransition(0,0);
            }
        });
        holder.blrq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences get_product_detail= mActivity.getSharedPreferences("entry",0);
                SharedPreferences.Editor editor=get_product_detail.edit();
                editor.putString("entry_level","1");
                editor.apply();
                final String[] preference = new String[1];
                final Dialog dialog = new Dialog(mActivity);
                dialog.setContentView(R.layout.request_quote);
                dialog.setTitle("Request A Quote");
                dialog.setCancelable(true); //none-dismiss when touching outside Dialog
                name = dialog.findViewById(R.id.etApiName);
                email = dialog.findViewById(R.id.etEmail);
                phone = dialog.findViewById(R.id.etPhone);
                details = dialog.findViewById(R.id.etDetails);
                TextView LoggedInName = dialog.findViewById(R.id.etName);
                TextView FromName = dialog.findViewById(R.id.etFromName);
                final String[] method = {"No Preference", "Email", "Mobile"};

                final Button[] btn = {dialog.findViewById(R.id.btnOpenDialog)};
                btn[0].setClickable(true);
                btn[0].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                        builder.setTitle("Select Option");
                        builder.setItems(method, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                preference[0] = method[item];
                                btn[0].setText(preference[0]);
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                });

                View btnSubmit = dialog.findViewById(R.id.btnSubmit);
                View btnClose = dialog.findViewById(R.id.btnClose);
                if (isLoggedIn == 0) {
                    LoggedInName.setVisibility(View.GONE);
                    FromName.setVisibility(View.GONE);

                }
                else {
                    name.setVisibility(View.GONE);
                    email.setVisibility(View.GONE);
                    phone.setVisibility(View.GONE);
                    LoggedInName.setText(full_name);
                }


                btnSubmit.setOnClickListener(onConfirmListener(name, email, phone, details, preference, dialog,id,url));
                btnClose.setOnClickListener(onCancelListener(dialog));
                dialog.show();
                Window window = dialog.getWindow();
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            }
        });
        holder.clickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
                String finalUrl=base_url+"business/"+current.getCity()+"/"+current.getUrl()+"/"+current.getId();
                SharedPreferences get_product_detail= mActivity.getSharedPreferences("entry",0);
                SharedPreferences.Editor editor=get_product_detail.edit();
                editor.putString("entry_level","1");
                editor.apply();
                Intent intent = new Intent(mActivity, WebViewActivity.class);
                intent.putExtra("URL", finalUrl);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                mActivity.startActivityForResult(intent,0);
                mActivity.overridePendingTransition(0,0);

            }
        });

    }

    public void getData()
    {
        flag = loginData.getInt("Flag",0);
        full_name=loginData.getString("Name","");
        email1=loginData.getString("mail","");
        image=loginData.getString("image","");
        phone_no=loginData.getString("phone","");
        Id=loginData.getInt("id",0);
        created=loginData.getString("create","");
        updated=loginData.getString("update","");
        base_url=loginData.getString("base_url","");
        cdn_url=loginData.getString("cdn_url","");
        api_url=loginData.getString("api_url","");
        api_token=loginData.getString("api_token","");

    }
}



