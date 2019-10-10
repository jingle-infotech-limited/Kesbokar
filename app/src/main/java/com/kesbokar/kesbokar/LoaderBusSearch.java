package com.kesbokar.kesbokar;


import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;

public class LoaderBusSearch extends AsyncTaskLoader<ArrayList<String>> {
    private String Query;
    private String base_url1;
    public LoaderBusSearch(Context context, String Query,String url){
        super(context);
        this.Query = Query;
        base_url1 = url;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public ArrayList<String> loadInBackground() {
        ArrayList<String> businessSearchBarsValues = new ArrayList<>();
        String data = (new SetHttpPost(getContext())).sendPostMarkAndBus(Query,base_url1);
        //call jsonParser only if the data is not null
        if(data != null){
            try {
                JsonParser jsonBusSrch = new JsonParser();
                businessSearchBarsValues = jsonBusSrch.getBusinessSearch(data);
            }catch (Throwable t){
                t.printStackTrace();
            }
            return businessSearchBarsValues;
        }
        return null;
    }
}
