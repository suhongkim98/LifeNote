package com.example.kimsuhong.lifenote;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

public class SingerDiaryDateListItem {
    private String idx;
    private String date;
    private Uri photoUri;
    private String[] questions = new String[2];
    private String[] contents = new String[2];
    private String[] records = new String[2];
    public SingerDiaryDateListItem(String idx, String date, String uri,String question1, String content1,String question2, String content2){
        this.idx = idx; // PRIMARY KEY
        this.date = date; this.photoUri = Uri.parse(uri); this.contents[0] = content1; this.contents[1] = content2; this.questions[0] = question1; this.questions[1] = question2;
    }
    public SingerDiaryDateListItem(String idx, String date, String uri,String[] questions, String[] contents){ // 이걸로 나중에 생성자를 갈아치우자
        this.idx = idx; // PRIMARY KEY
        this.date = date; this.photoUri = Uri.parse(uri); this.contents = contents; this.questions = questions;

    }
    public void setDate(String date){
        this.date = date;
    }
    public String getDate(){
        return date;
    }
    public void setUri(Uri uri){
        this.photoUri = uri;
    }
    public Uri getUri(){
        return photoUri;
    }
    public void setContents(String c1, String c2){
        this.contents[0] = c1;
        this.contents[1] = c2;
    }
    public String getContents(int idx){
        return contents[idx];
    }
    public String getQuestions(int idx){
        return questions[idx];
    }
    public void setRecordPath(int idx, String path){
        records[idx] = path;
    }
    public String getRecordPath(int idx){
        return records[idx];
    }
    public String getIdx(){
        return idx;
    }
}
