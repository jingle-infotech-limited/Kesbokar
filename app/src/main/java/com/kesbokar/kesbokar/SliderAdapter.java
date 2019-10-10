package com.kesbokar.kesbokar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.net.URISyntaxException;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterVH> {

    private Context context;
    private String slider_array[]=new String[5];

    public SliderAdapter(Context context) {
        this.context = context;
        getData();
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout_item, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {
        viewHolder.textViewDescription.setText("This is slider item " + position);
        getData();

        switch (position) {

            case 0:
                Glide.with(viewHolder.itemView)
                        .load(slider_array[0])
                        .into(viewHolder.imageViewBackground);
                break;
            case 1:
                Glide.with(viewHolder.itemView)
                        .load(slider_array[1])
                        .into(viewHolder.imageViewBackground);
                break;
            case 2:
                Glide.with(viewHolder.itemView)
                        .load(slider_array[2])
                        .into(viewHolder.imageViewBackground);
                break;
//            default:
//                Glide.with(viewHolder.itemView)
//                        .load("https://images.pexels.com/photos/218983/pexels-photo-218983.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260")
//                        .into(viewHolder.imageViewBackground);
//                break;

        }

    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return 3;
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {


        View itemView;
        ImageView imageViewBackground;
        TextView textViewDescription;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            textViewDescription = itemView.findViewById(R.id.tv_auto_image_slider);
            this.itemView = itemView;
        }
    }

    void getData()
    {
        SharedPreferences sharedPreferences=context.getSharedPreferences("data",0);
        int size = sharedPreferences.getInt("slider_size",0);
        for (int i =0; i<size;i++){
            slider_array[i]=sharedPreferences.getString("slider_"+i+"", "");
            Log.i("slider_value",slider_array[i].toString());
        }

    }
}