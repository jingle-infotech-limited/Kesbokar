package com.kesbokar.kesbokar;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class PhotosFragment extends Fragment {
    String cdn_url,base_url,api_url,api_token;

    String myurl;

    Button btnChoose, btnUpload, btnBack,btnSubmit;
Uri uri;
    private static final int PERMISSION_REQUEST_CODE = 1;
    Bitmap[] bitmap;
    int PICK_IMAGE_MULTIPLE = 1;
    String imageEncoded;
    List<String> imagesEncodedList;
    int count;
ProgressBar progressBar;
    JSONArray json_name_array;
    JSONObject json_name;
    String pic_name;
    private GridView gvGallery;
    private GalleryAdapter galleryAdapter;
 //   ProgressDialog progressDialog = new ProgressDialog(getActivity());

    private int id,flag;
    String full_name, email, image, phone_no, created, updated, product_id, product_name, name_title, product_condition,
            product_section, category_id1, price1, phone1, address1, description1, status1, pro_id;
//ProgressBar PB;
    int edit1;


    ViewPager viewPager;
    TabLayout tabLayout;
Context context;
//public   PhotosFragment(Context context){this.context=context; }
    public PhotosFragment(ViewPager viewPager, TabLayout tabLayout,Context context)
    {
        this.viewPager=viewPager;
        this.tabLayout=tabLayout;
      //  getData();
        // Required empty public constructor
        this.context=context;
    }
public  PhotosFragment(){super();}

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos, container, false);

        getData();

progressBar=view.findViewById(R.id.progressBar_cyclic);
        btnUpload = view.findViewById(R.id.btnUpload);
        btnBack=view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int item=viewPager.getCurrentItem();
                View tab=tabLayout.getTabAt(item-1).view;
                tab.setEnabled(true);
                viewPager.setCurrentItem(item-1);
            }
        });



        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                uploaduserimage();


            }
        });

        btnChoose = view.findViewById(R.id.btnChoose);
        gvGallery = view.findViewById(R.id.gv);



        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showFileChooser();
            }
        });

        btnSubmit = view.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getData();
                uploaduserimage();



            }
        });

        if (Build.VERSION.SDK_INT >= 23){
            if (checkPermission()){

            }
            else {
                requestPermissions();
            }
        }
        return view;
    }

    private void requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(getContext(), " Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "Permission Granted Successfully! ", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Permission Denied 🙁 ", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {

            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && data!=null) {
                // Get the Image from data
                ArrayList<Uri>  mArrayUri = new ArrayList<Uri>();
                String filePathColumn[] = {MediaStore.Images.Media.DATA};
                  imagesEncodedList = new ArrayList<String>();
                if (data.getData() != null) {
                    try {


                        Uri mImageUri = data.getData();
                        mArrayUri.add(mImageUri);
                        count=1;
                        // Get the cursor
                        Cursor cursor = getActivity().getContentResolver().query(mImageUri, filePathColumn, null, null, null);
                        // Move to first row
                        cursor.moveToFirst();
                        // bitmap = new Bitmap[mClipData.getItemCount()];
                        bitmap = new Bitmap[count];
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        bitmap[columnIndex] = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), mImageUri);
                       // Log.i("Bitmap"+columnIndex,bitmap[columnIndex].toString());
                    //    Toast.makeText(getContext(), "hi" + bitmap, Toast.LENGTH_SHORT).show();
                      try  {
                         // imageEncoded  = cursor.getString(columnIndex);
                            imageEncoded = cursor.getString(columnIndex);
                            //   imageEncoded = cursor.getString(columnIndex);
                            imagesEncodedList.add(imageEncoded);
                         //   Log.i("imageencoded", imageEncoded);
                        }catch(Exception e){e.printStackTrace();}
                        cursor.close();




                        galleryAdapter = new GalleryAdapter(getContext().getApplicationContext(), mArrayUri);
                        gvGallery.setAdapter(galleryAdapter);
                        gvGallery.setVerticalSpacing(gvGallery.getHorizontalSpacing());
                        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery
                                .getLayoutParams();
                        mlp.setMargins(0, gvGallery.getHorizontalSpacing(), 0, 0);

                        //-------

                        //Log.i("Photos_if", "hi");
                    }catch(Exception e){e.printStackTrace();}
                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();

                        bitmap=new Bitmap[mClipData.getItemCount()];
                        count=mClipData.getItemCount();
                       // Uri filePath=data.getData();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {


                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            bitmap[i] = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                           // Log.i("Bitmap",bitmap[i].toString());
                        //    Toast.makeText(getContext(), ""+bitmap, Toast.LENGTH_SHORT).show();
                            //Setting the Bitmap to ImageView
                            mArrayUri.add(uri);
                            // Get the cursor
                            Cursor cursor = getContext().getContentResolver().query(uri, filePathColumn, null, null, null);
                            // Move to first row

                            cursor.moveToFirst();


                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String fileName = cursor.toString();
                            imageEncoded  = cursor.getString(columnIndex);
                            imagesEncodedList.add(imageEncoded);
                            cursor.close();

                            galleryAdapter = new GalleryAdapter(getContext().getApplicationContext(),mArrayUri);
                            gvGallery.setAdapter(galleryAdapter);
                            gvGallery.setVerticalSpacing(gvGallery.getHorizontalSpacing());
                            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery
                                    .getLayoutParams();
                            mlp.setMargins(0, gvGallery.getHorizontalSpacing(), 0, 0);

                        }

                    }
                    //Log.i("LOG_TAG", "Selected Images" + mArrayUri.size());


                }

            } else {
                Toast.makeText(getContext(), "You haven't picked Image",Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG)
                    .show();
            //Log.i("error",e.toString());
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    public void uploaduserimage(){
//        progressDialog.setTitle("Processing...");
//        progressDialog.setMessage("Processing...");
//        progressDialog.show();
      progressBar.setVisibility(View.VISIBLE);
        final RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        getData();
         myurl=base_url+"jil.0.1/api/v1/product/gallery/upload";
      //  progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, myurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressBar.setProgress(100);
            //    progressDialog.dismiss();
                String url;

         //       Log.i("Myresponse_phots",""+response);
                //Toast.makeText(getContext(), ""+response, Toast.LENGTH_SHORT).show();
                try {
                    json_name=new JSONObject(response);
                    pic_name=json_name.getString("image");
                 //   imageEncoded=json_name.getString("image");
                  //  Log.i("Response pic_name", pic_name );

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (edit1==1){
                    url = api_url+"v1/product/"+pro_id+"/gallery";
                }
                else {
                    url = api_url+"v1/product/"+product_id+"/gallery";
                }
                StringRequest stringRequest_name = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {

                      Toast.makeText(getContext(), "Image Uploaded successfully", Toast.LENGTH_SHORT).show();


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                      //  Log.i("Mysmart1",""+error);
                   //     progressDialog.dismiss();
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getContext(), "Uploading unsuccessful", Toast.LENGTH_SHORT).show();

                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        getData();
                        Map<String,String> param = new HashMap<>();
                      // param.put("images",imageEncoded+"");
                        param.put("image",pic_name+"");
                        //Log.i("Mysmart3",""+imageEncoded+" "+pic_name);
                      //  Log.i("FILENAME1:",name_title+" "+product_name);
                        param.put("api_token",api_token);
                        return param;
                    }
                };

                requestQueue.add(stringRequest_name);

                requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
                    @Override
                    public void onRequestFinished(Request<String> request) {
                        if (progressBar !=  null && progressBar.isShown())
                        {
                            progressBar.setVisibility(View.INVISIBLE);
                            int item = viewPager.getCurrentItem();
                            View tab = tabLayout.getTabAt(item + 1).view;
                            tab.setEnabled(true);
                            viewPager.setCurrentItem(item + 1);
                        }

                    }
                });
             // stringRequest_name.setRetryPolicy(new DefaultRetryPolicy(10000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // Log.i("Mysmart2",""+error);
               // progressDialog.dismiss();
                Toast.makeText(getContext(), "Something went wrong "+error, Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                getData();
                Map<String,String> param = new HashMap<>();
                String[] images=new String[count];
                JSONArray jsonArray=new JSONArray();
                JSONObject[] jsonObjects = new JSONObject[count];
           //     Log.i("count",""+count);
                for (int i=0; i<count; i++) {
                    images[i] = getStringImage(bitmap[i]);

                    //Log.i("image",""+images[i]);
                    try {
                        jsonObjects[i]=new JSONObject();
                       jsonObjects[i].put("name",name_title+i+".png");
                        jsonObjects[i].put("image",images[i]);
                        jsonArray.put(jsonObjects[i]);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
             //   Log.i("JSON Array",jsonArray.toString());
                if (edit1==1)
                {
                    param.put("filename",name_title+"");

                }
                else {
                    getData();
                    param.put("filename", product_name+"");
                }
             //   Log.i("FILENAME2:",name_title+" "+product_name);
                param.put("images",jsonArray.toString());
                param.put("api_token",api_token);
                return param;
            }
        };

        requestQueue.add(stringRequest);
//        SharedPreferences business_edit=getActivity().getSharedPreferences("market_edit",0);
//        SharedPreferences.Editor editor=business_edit.edit();
//        editor.putString("phone",);
//        editor.putString()''
//        edit1=business_edit.getInt("edit",0);
//        if(edit1==1) {
//            name_title = business_edit.getString("name", "");
//            product_condition = business_edit.getString("product_condition", "");
//            product_section = business_edit.getString("product_section", "");
//            category_id1 = business_edit.getString("category_id", "");
//            price1 = business_edit.getString("price", "");
//            phone1 = business_edit.getString("phone", "");
//            address1 = business_edit.getString("address", "");
//            description1 = business_edit.getString("description", "");
//            status1 = business_edit.getString("status", "");
//            pro_id = business_edit.getString("product_id", "");
//        }

       // progressDialog.dismiss();

    }

    public String getStringImage(Bitmap bitmap){
       // Log.i("function bit",""+bitmap);
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public void getData()
    {
        SharedPreferences loginData=getContext().getSharedPreferences("data",0);
        flag = loginData.getInt("Flag",0);
        full_name=loginData.getString("Name","");
        email=loginData.getString("mail","");
        image=loginData.getString("image","");
        phone_no=loginData.getString("phone","");
        id=loginData.getInt("id",0);
        created=loginData.getString("create","");
        updated=loginData.getString("update","");

        SharedPreferences get_product_detail=getContext().getSharedPreferences("product_detail",0);
        product_id =get_product_detail.getString("product_id","");
        product_name=get_product_detail.getString("product_name","");
        base_url=loginData.getString("base_url","");
        cdn_url=loginData.getString("cdn_url","");
        api_url=loginData.getString("api_url","");
        api_token=loginData.getString("api_token","");

        SharedPreferences business_edit=getActivity().getSharedPreferences("market_edit",0);
        edit1=business_edit.getInt("edit",0);
        if(edit1==1) {
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
        //cdn_url=loginData.getString("cdn_url","");


    }
}