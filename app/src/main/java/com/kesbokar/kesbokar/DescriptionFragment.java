package com.kesbokar.kesbokar;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class DescriptionFragment extends Fragment {

    EditText etDescription;
    Button btnBack, btnSubmit;
    ViewPager viewPager;
    TabLayout tabLayout;
    private String loginId, loginPass, full_name, email, image, phone_no,created,updated,product_id,product_name;
    private int id,flag;
    String make_id,model_id1,year1,variant_id1,vehicle_id,colour,airconditioning,registered,registration_state,registration_number,registration_expiry,name_title,product_condition,product_section,category_id1,price1,phone1,address1,description1,status1,pro_id,model_name,variant_name;
    int edit1;
    String api_url,api_token;

public DescriptionFragment(){super();}
    public DescriptionFragment(ViewPager viewPager, TabLayout tabLayout)
    {
        this.viewPager=viewPager;
        this.tabLayout=tabLayout;
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_description, container, false);
        //getSupportFragmentManager().beginTransaction().add(R.id.fragment_holder, new AwesomeFragment()).commit();
        getData();
        etDescription = view.findViewById(R.id.etDescription);
        btnBack = view.findViewById(R.id.btnBack);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        if (edit1==1)
        {
            etDescription.setText(description1);
        }
etDescription.addTextChangedListener(new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    etDescription.setError(null);
    }

    @Override
    public void afterTextChanged(Editable editable) {
        description1=etDescription.getText().toString();
    }
});
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int item=viewPager.getCurrentItem();
                View tab=tabLayout.getTabAt(item-1).view;
                tab.setEnabled(true);
                viewPager.setCurrentItem(item-1);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // getData();
                SharedPreferences business_edit = getActivity().getSharedPreferences("market_edit", 0);
                SharedPreferences.Editor edit2=business_edit.edit();
                edit2.putString("description",description1);
                edit2.apply();
                RequestQueue queue= Volley.newRequestQueue(getActivity());
                String url;


                if (edit1==1)
                {
                    url=api_url+"v1/product/"+pro_id;
                }
                else {
                    url = api_url+"v1/product/" + product_id;
                }
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Response1_Description",response);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error",error.toString());
                    }
                }){
                    @Override
                    protected Map<String, String> getParams()
                    {
                       // getData();
                        Map<String, String>  params = new HashMap<String, String >();
//                        params.put("name",edtProductTitle.getText().toString());
//                        params.put("product_condition",);
//                        params.put("product_section",);
//                        params.put("topcat_id",);
//                        params.put("parentcat_id",);
//                        params.put("category_id",);
//                        params.put("tags",);
//                        params.put("price",);
                        String user_id=""+id;
                        params.put("user_id",user_id);
                     //   params.put("description",etDescription.getText().toString());
                        params.put("description", description1);
                        params.put("api_token",api_token);
                        return params;
                    }
                };
                queue.add(stringRequest);
                if(etDescription.getText().toString().equals(""))
                {
                    etDescription.setError("Required Field");
                }
                else {
                                int item = viewPager.getCurrentItem();
                                View tab = tabLayout.getTabAt(item + 1).view;
                                tab.setEnabled(true);
                                viewPager.setCurrentItem(item + 1);



                }


            }
        });

        return view;
    }

    public void getData()
    {
    SharedPreferences loginData = getActivity().getSharedPreferences("data", 0);
    SharedPreferences business_edit = getActivity().getSharedPreferences("market_edit", 0);
    SharedPreferences get_product_detail = getActivity().getSharedPreferences("product_detail", 0);


        flag = loginData.getInt("Flag", 0);
        full_name = loginData.getString("Name", "");
        email = loginData.getString("mail", "");
        // image=loginData.getString("image","");
        phone_no = loginData.getString("phone", "");
        id = loginData.getInt("id", 0);
        created = loginData.getString("create", "");
        updated = loginData.getString("update", "");
        api_url = loginData.getString("api_url", "");
        api_token = loginData.getString("api_token", "");

        product_id = get_product_detail.getString("product_id", "");
        product_name = get_product_detail.getString("product_name", "");

        edit1 = business_edit.getInt("edit", 0);
        if(edit1==1)
        {

        name_title = business_edit.getString("name", "");
        product_condition = business_edit.getString("product_condition", "");
        product_section = business_edit.getString("product_section", "");
        category_id1 = business_edit.getString("category_id", "");
        price1 = business_edit.getString("price", "");
        phone1 = business_edit.getString("phone", "");
        address1 = business_edit.getString("address", "");
        description1 = business_edit.getString("description", "");
        status1 = business_edit.getString("status", "");
        pro_id = business_edit.getString("product_id", "");

        }
        Log.i("DESCRIPTION",product_name);
    }



}
