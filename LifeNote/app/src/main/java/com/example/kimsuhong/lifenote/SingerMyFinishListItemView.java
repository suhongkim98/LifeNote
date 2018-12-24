package com.example.kimsuhong.lifenote;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SingerMyFinishListItemView extends LinearLayout {
    TextView titleText;

    public SingerMyFinishListItemView(Context context){
        super(context);
        init(context);
    }
    public SingerMyFinishListItemView(Context context, AttributeSet attrs){
        super(context,attrs);
        init(context);
    }
    private  void init(Context context){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.singer_myfinishlist_item,this,true);

        titleText = (TextView)findViewById(R.id.buketTitle);
    }
    public void setTitle(String title){
        titleText.setText(title);
    }
}
