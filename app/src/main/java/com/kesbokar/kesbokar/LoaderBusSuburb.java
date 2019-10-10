package com.kesbokar.kesbokar;

import android.content.Context;

import androidx.loader.content.AsyncTaskLoader;

import java.util.ArrayList;

public class LoaderBusSuburb extends AsyncTaskLoader<ArrayList<StateAndSuburb>> {
    String Query;
    String base_url1;
    public LoaderBusSuburb(Context context, String Query, String url){
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
    public ArrayList<StateAndSuburb> loadInBackground() {
        ArrayList<StateAndSuburb> srchSubs = new ArrayList<>();
        String data = (new SetHttpPost(getContext())).sendPostMarkAndBus(Query,base_url1);
        //call jsonParser only if the data is not null
        if(data != null){
            try {
                JsonParser jsonsub = new JsonParser();
                srchSubs = jsonsub.getSuburbs(data);
            }catch (Throwable t){
                t.printStackTrace();
            }
            return srchSubs;
        }
        return null;
    }
}
