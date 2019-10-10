package com.kesbokar.kesbokar;
import android.content.Context;

import android.content.AsyncTaskLoader;
import android.content.SharedPreferences;

import java.util.ArrayList;

public class LoaderMarket extends AsyncTaskLoader<ArrayList<MarketPlaceApi>> {
    ArrayList<MarketPlaceApi> market;
    Context context;
    String api_url,api_token;
    public LoaderMarket(Context context) {
        super(context);
        this.context=context;
    }
    protected void onStartLoading(){
        super.onStartLoading();
        if (market != null) {
            // Use cached data
            deliverResult(market);
        } else {
            // We have no data, so kick off loading it
            forceLoad();
        }
    }

    @Override
    public ArrayList<MarketPlaceApi> loadInBackground() {
        getData();
        market=new ArrayList<>();
        String data=(new SetHttpConnection(api_url+"v2/product-featured?api_token="+api_token,getContext())).getInputStreamData();
        if(data != null){
            try {
                JsonParser json_news_parser = new JsonParser();
                market = json_news_parser.getMarket(data);
            }catch (Throwable t){
                t.printStackTrace();
            }
            return market;
        }
        return null;
    }
    void getData()
    {
        SharedPreferences loginData=context.getSharedPreferences("data",0);
        api_url=loginData.getString("api_url","");
        api_token=loginData.getString("api_token","");


    }
}
