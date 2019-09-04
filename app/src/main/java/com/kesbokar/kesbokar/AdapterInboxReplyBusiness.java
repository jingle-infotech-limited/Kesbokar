package com.kesbokar.kesbokar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterInboxReplyBusiness extends BaseAdapter {
    ArrayList<inbox_reply_business> get_for_replies;
    TextView date,replyby1,reply,sno;
    LayoutInflater layoutInflater;
    Context context;
    Activity activity;
    public AdapterInboxReplyBusiness(Context context ,InboxReplyBusiness activity ,ArrayList<inbox_reply_business> get_for_replies)
    {
        this.context=context;
        this.activity=activity;
        this.get_for_replies=get_for_replies;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return get_for_replies.size();
    }

    @Override
    public Object getItem(int position) {
        return get_for_replies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int pos=position+1;
        String i=""+pos;
        convertView=layoutInflater.inflate(R.layout.adapter_inbox_business_reply,null);
        reply=convertView.findViewById(R.id.reply);
        sno=convertView.findViewById(R.id.Sno);
        replyby1=convertView.findViewById(R.id.replyby);
        date=convertView.findViewById(R.id.date);
        date.setText(get_for_replies.get(position).getDate());
        replyby1.setText(get_for_replies.get(position).getReplyBy());
        reply.setText(get_for_replies.get(position).getReplyMessage());
        final int id1=get_for_replies.get(position).getId1();
        sno.setText(i);
        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,InboxReplyBusiness.class);
                intent.putExtra("id",id1);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activity.startActivityForResult(intent, 0);
                activity.overridePendingTransition(0, 0);
            }
        });

        return convertView;
    }
}