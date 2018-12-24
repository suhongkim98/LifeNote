package com.example.kimsuhong.lifenote;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.w3c.dom.Text;

public class SingerDiaryDateListItemView extends LinearLayout {
    private ImageView imageView;
    private TextView dateText;

    public SingerDiaryDateListItemView(Context context){
        super(context);
        init(context);
    }
    public SingerDiaryDateListItemView(Context context, AttributeSet attrs){
        super(context,attrs);
        init(context);
    }

    private  void init(Context context){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.singer_diary_date_item,this,true);

        imageView = (ImageView)findViewById(R.id.image);
        dateText = (TextView) findViewById(R.id.textDate);

        initImageLoader(context);
    }
    private void initImageLoader(Context context){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(context);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    public void setDate(String date){
        String month = date.substring(5,7);
        String day = date.substring(8);
        dateText.setText(month+"월 "+day+"일");
    }

    public void setImage(Uri uri){
        UniversalImageLoader.setImage(uri.toString(),imageView,null);
    }
}
