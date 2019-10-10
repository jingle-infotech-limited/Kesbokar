package com.kesbokar.kesbokar;

import android.content.Context;

import android.content.AsyncTaskLoader;
import android.content.SharedPreferences;

import java.util.ArrayList;

public class LoaderServices extends AsyncTaskLoader<ArrayList<ServiceExpertSpace>> {
    ArrayList<ServiceExpertSpace> serviceDetails;
    Context context;
    String api_url,api_token;
    public LoaderServices(Context context){
        super(context);
        this.context=context;

    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (serviceDetails != null) {
            // Use cached data
            deliverResult(serviceDetails);
        } else {
            // We have no data, so kick off loading it
            forceLoad();
        }
    }

    @Override
    public ArrayList<ServiceExpertSpace> loadInBackground() {
        getData();
        serviceDetails = new ArrayList<>();
        String data = (new SetHttpConnection(api_url+"v2/yellowpage-featured?api_token="+api_token,getContext())).getInputStreamData();

        //call jsonParser only if the data is not null
        if(data != null){
            try {
                JsonParser json_news_parser = new JsonParser();
                serviceDetails = json_news_parser.getServiceSpace(data);
            }catch (Throwable t){
                t.printStackTrace();
            }
            return serviceDetails;
        }
        return null;
    }

    @Override
    public void deliverResult(ArrayList<ServiceExpertSpace> data) {
        super.deliverResult(data);
    }
    void getData()
    {
        SharedPreferences loginData=context.getSharedPreferences("data",0);
        api_url=loginData.getString("api_url","");
        api_token=loginData.getString("api_token","");


    }
}

