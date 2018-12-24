package com.example.kimsuhong.lifenote;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kimsuhong.lifenote.R;

public class SingerMyBuketlistItemView extends LinearLayout {
    TextView titleText;

    public SingerMyBuketlistItemView(Context context){
        super(context);
        init(context);
    }
    public SingerMyBuketlistItemView(Context context, AttributeSet attrs){
        super(context,attrs);
        init(context);
    }
    private  void init(Context context){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.singer_mybuketlist_item,this,true);

        titleText = (TextView)findViewById(R.id.buketTitle);
    }
    public void setTitle(String title){
        titleText.setText(title);
    }
}
