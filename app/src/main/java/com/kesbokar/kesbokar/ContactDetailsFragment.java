package com.kesbokar.kesbokar;



import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.viewpager.widget.ViewPager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactDetailsFragment extends Fragment {

    private TextView tvEmail;
    private EditText etPhone, etStreet;
    private AutoCompleteTextView etState,etSuburb;
    private Button btnBack, btnSubmit;
    private String loginId, loginPass, full_name, email, image, phone_no,created,updated,api_url,api_token;
    private int id,flag;
    String make_id,model_id,year,variant_id,vehicle_id,colour,airconditioning,registered,registration_state,registration_number,registration_expiry,name_title,product_condition,product_section,category_id1,price1,phone1,address1,description1,status1,pro_id;
    int edit1;
    String state,street,suburb,phone_;
    private String querySub;
    private int stateid,state_et_clicked=0;
    private int subUrbID;
    private String subType;

    private String q, subV,url;

    private String product_id, product_name;

    private static final int LOADER_ID_BUSVAL = 10101;
    private ArrayList<StateAndSuburb> valsBus;
    private androidx.loader.app.LoaderManager.LoaderCallbacks<ArrayList<StateAndSuburb>> businessSuburb;
    ViewPager viewPager;
    TabLayout tabLayout;


    public ContactDetailsFragment(ViewPager viewPager, TabLayout tabLayout)
    {
        this.viewPager=viewPager;
        this.tabLayout=tabLayout;
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_contact_details, container, false);
        getData();
        tvEmail = view.findViewById(R.id.tvEmail);
        etPhone = view.findViewById(R.id.etPhone);
        etStreet = view.findViewById(R.id.etStreet);
        etState = view.findViewById(R.id.etState);
        etSuburb = view.findViewById(R.id.etSuburb);
        btnBack = view.findViewById(R.id.btnBack);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        etPhone.setText(phone_);
        etSuburb.setText(suburb);
        etState.setText(state);
        etStreet.setText(street);

        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                etPhone.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        valsBus = new ArrayList<>();

        querySub = "";

        q = subV = querySub = "au";


        tvEmail.setText(email);

        if (edit1 == 1) {
            etPhone.setText(phone_);
            etStreet.setText(street);
        }
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int item = viewPager.getCurrentItem();
                View tab = tabLayout.getTabAt(item - 1).view;
                tab.setEnabled(true);
                viewPager.setCurrentItem(item - 1);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
                try {

                    if (etPhone.getText().toString().equals("")) {
                        etPhone.setError("Required Field");
                      //  Log.i("etphone","etphone");
                    }

                    if (etState.getText().toString().equals("")) {
                        etState.setError("Required Field");
                    }
                    if (etSuburb.getText().toString().equals("")) {
                        etSuburb.setError("Required Field");
                    }
                    if (!etState.getText().toString().equals("") &&!etPhone.getText().toString().equals("") &&!etSuburb.getText().toString().equals("")  ){//&& etSuburb.getError().toString().equals("") && etPhone.getError().toString().equals("")) {
                        int item = viewPager.getCurrentItem();
                        View tab = tabLayout.getTabAt(item + 1).view;
                        tab.setEnabled(true);
                        viewPager.setCurrentItem(item + 1);
                    }
//                String  patterntomatch ="/^((\\+[1-9]{1,4}[ \\-]*)|(\\([0-9]{2,3}\\)[ \\-]*)|([0-9]{2,4})[ \\-]*)*?[0-9]{3,4}?[ \\-]*[0-9]{3,4}?$/";
//                Pattern pattern=Pattern.compile(patterntomatch);


                    RequestQueue queue = Volley.newRequestQueue(getActivity());
                    String url;
                    if (edit1 == 1) {
                        url = api_url + "v1/product/" + pro_id;
                    } else {
                        url = api_url + "v1/product/" + product_id;
                    }
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("Response", response);

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("Error", error.toString());
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            getData();
                            Map<String, String> params = new HashMap<String, String>();
//                        params.put("name",edtProductTitle.getText().toString());
//                        params.put("product_condition",);
//                        params.put("product_section",);
//                        params.put("topcat_id",);
//                        params.put("parentcat_id",);
//                        params.put("category_id",);
//                        params.put("tags",);
//                        params.put("price",);
                            Log.i("state", id + "  " + stateid + "  " + subUrbID + "  " + product_id);
                            String st_Id = "" + stateid;
                            String subUrb = "" + subUrbID;
                            params.put("state_id", st_Id);
                            params.put("city_id", subUrb);
                            String user_id = "" + id;
                            params.put("user_id", user_id);
                            params.put("phone", etPhone.getText().toString());
                            params.put("zipcode", "");
                            params.put("address", etStreet.getText().toString());
                            params.put("api_token", api_token);
                            return params;
                        }
                    };
                    queue.add(stringRequest);
                    SharedPreferences business_edit=getActivity().getSharedPreferences("market_edit",0);
        SharedPreferences.Editor editor=business_edit.edit();
        editor.putString("phone2",etPhone.getText().toString());
        editor.putString("state",etState.getText().toString());
        editor.putString("street",etStreet.getText().toString());
        editor.putString("suburb",etSuburb.getText().toString());
        editor.apply();


                } catch (Exception e) {            e.printStackTrace();                }
            }
        });


        etSuburb.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StateAndSuburb stateAndSuburb = (StateAndSuburb) parent.getAdapter().getItem(position);
                subV = stateAndSuburb.getValue();
                stateid = stateAndSuburb.getId();
                subType = stateAndSuburb.getType();
            }
        });

        etSuburb.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                querySub = s.toString();
                url = api_url + "v2/product/search/cities";
                getLoaderManager().initLoader(LOADER_ID_BUSVAL, null, businessSuburb);
                etSuburb.setError(null);


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etState.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                querySub = "";
                getLoaderManager().initLoader(LOADER_ID_BUSVAL, null, businessSuburb);
                etState.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        businessSuburb = new androidx.loader.app.LoaderManager.LoaderCallbacks<ArrayList<StateAndSuburb>>() {

            @NonNull
            @Override
            public Loader<ArrayList<StateAndSuburb>> onCreateLoader(int id, @Nullable Bundle args) {
                getData();
                LoaderBusSuburb loaderBusSuburb = new LoaderBusSuburb(getContext(), querySub, api_url + "v2/product/search/cities");
                return loaderBusSuburb;
            }

            @Override
            public void onLoadFinished(@NonNull Loader<ArrayList<StateAndSuburb>> loader, ArrayList<StateAndSuburb> data) {
                if (data.size() != 0) {
                    valsBus = data;
                    Log.i("Tag", valsBus + "");
                    ArrayAdapter<StateAndSuburb> adapter = new ArrayAdapter<StateAndSuburb>(getContext(), android.R.layout.simple_dropdown_item_1line, valsBus);
                    etState.setAdapter(adapter);
                    getLoaderManager().destroyLoader(LOADER_ID_BUSVAL);
                    etSuburb.setAdapter(adapter);
                    getLoaderManager().destroyLoader(LOADER_ID_BUSVAL);
                    etState.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            etState.showDropDown();
                        }
                    });
//                    etSuburb.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                                                        etSuburb.showDropDown();
//                        }
//                    });

                    etState.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            StateAndSuburb stateAndSuburb = (StateAndSuburb) parent.getAdapter().getItem(position);
                            stateid = stateAndSuburb.getId();
                        }
                    });
                    etSuburb.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            StateAndSuburb stateAndSuburb = (StateAndSuburb) parent.getAdapter().getItem(position);
                            subUrbID = stateAndSuburb.getId();
                        }
                    });
                }

            }

            @Override
            public void onLoaderReset(@NonNull Loader<ArrayList<StateAndSuburb>> loader) {

            }
        };

        getLoaderManager().initLoader(LOADER_ID_BUSVAL, null, businessSuburb);
        return view;


    }


    public void getData()
    {
        SharedPreferences loginData=getActivity().getSharedPreferences("data",0);
        flag = loginData.getInt("Flag",0);
        full_name=loginData.getString("Name","");
        email=loginData.getString("mail","");
        image=loginData.getString("image","");
        phone_no=loginData.getString("phone","");
        id=loginData.getInt("id",0);
        created=loginData.getString("create","");
        updated=loginData.getString("update","");
        api_url=loginData.getString("api_url","");
        api_token=loginData.getString("api_token","");

        SharedPreferences get_product_detail=getActivity().getSharedPreferences("product_detail",0);
        product_id =get_product_detail.getString("product_id","");
        product_name=get_product_detail.getString("product_name","");

        SharedPreferences business_edit=getActivity().getSharedPreferences("market_edit",0);
        edit1=business_edit.getInt("edit",0);
        if (edit1==1)
        {
            make_id=business_edit.getString("make_id","");
            model_id=business_edit.getString("model_id","");
            year=business_edit.getString("year","");
            variant_id=business_edit.getString("variant_id","");
            vehicle_id=business_edit.getString("vehicle_id","");
            colour=business_edit.getString("colour","");
            airconditioning=business_edit.getString("airconditioning","");
            registered=business_edit.getString("registered","");
            registration_state=business_edit.getString("registration_state","");
            registration_number=business_edit.getString("registration_number","");
            registration_expiry=business_edit.getString("registration_expiry","");
            state=business_edit.getString("state","");
            street=business_edit.getString("street","");
            phone_=business_edit.getString("phone2","");
            suburb=business_edit.getString("suburb","");

        }
        name_title=business_edit.getString("name","");
        product_condition=business_edit.getString("product_condition","");
        product_section=business_edit.getString("product_section","");
        category_id1=business_edit.getString("category_id","");
        price1=business_edit.getString("price","");
        phone1=business_edit.getString("phone","");
        address1=business_edit.getString("address","");
        description1=business_edit.getString("description","");
        status1=business_edit.getString("status","");
        pro_id=business_edit.getString("product_id","");
        Log.i("CONTACT DETAILS",product_name);
    }



}