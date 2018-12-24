package com.example.kimsuhong.lifenote;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

public class SingerAlbumItemView extends LinearLayout {
    private static final int IS_TYPE_BUCKETLIST = 0;
    private static final int IS_TYPE_FINISHLIST = 1;
    private static final int IS_TYPE_DIARY = 2;

    ////////////////////////공통
    private TextView dateText;
    ////////////////////////////////// 버킷리스트
    private TextView bucketTitle; // ID는 finishList랑 bucketList랑 같음
    private TextView bucketContent;
    //////////////////////////////// 일기장
    private ImageView imageView;
    private int questionTextIndexArr[] = {R.id.question1Text,R.id.question2Text};
    private int answerTextIndexArr[] = {R.id.answer1Text,R.id.answer2Text};
    private TextView[] questionTextArr = new TextView[questionTextIndexArr.length];
    private TextView[] answerTextArr = new TextView[answerTextIndexArr.length];

    private int singerIDArr[] = {R.layout.singer_album_bucket_item,R.layout.singer_album_finish_item,R.layout.singer_album_diary_date_item}; // IS_TYPE순으로 배열
    private int type;
    public SingerAlbumItemView(Context context,int type){
        super(context);
        this.type = type;
        init(context);
    }
    public SingerAlbumItemView(Context context,int type, AttributeSet attrs){
        super(context,attrs);
        this.type = type;
        init(context);
    }
    private  void init(Context context){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(singerIDArr[type],this,true);
        dateText = (TextView)findViewById(R.id.textViewDate);
        switch (type){
            case IS_TYPE_BUCKETLIST:
            case IS_TYPE_FINISHLIST:
                bucketTitle = (TextView)findViewById(R.id.bucketTitle);
                bucketContent = (TextView)findViewById(R.id.bucketContent);
                break;
            case IS_TYPE_DIARY:
                imageView = (ImageView)findViewById(R.id.imageView);
                for(int i = 0 ; i < questionTextIndexArr.length;i++){
                    questionTextArr[i]=(TextView)findViewById(questionTextIndexArr[i]);
                }
                for(int i = 0 ; i < answerTextIndexArr.length;i++){
                    answerTextArr[i] = (TextView)findViewById(answerTextIndexArr[i]);
                }
                initImageLoader(context);
                break;
            default:
                break;
        }
    }
    private void initImageLoader(Context context){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(context);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    public void setDate(String date){
        String year = date.substring(0,4);
        String month = date.substring(5,7);
        String day = date.substring(8);
        dateText.setText(year+"년"+month+"월 "+day+"일");
    }
    public void setImage(Uri uri){
        UniversalImageLoader.setImage(uri.toString(),imageView,null);
    }
    public void setQuestions(String question,int idx){
        try{
            questionTextArr[idx].setText(question);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void setAnswers(String answer,int idx){
        try {
            answerTextArr[idx].setText(answer);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void setBucketTitle(String title){
        bucketTitle.setText(title);
    }
    public void setBucketContent(String content){
        bucketContent.setText(content);
    }
    public int questionCount(){
        return questionTextIndexArr.length;
    }
}
