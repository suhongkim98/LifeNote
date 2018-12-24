package com.example.kimsuhong.lifenote;

import android.net.Uri;

public class SingerAlbumItem {
    private static final int IS_TYPE_BUCKETLIST = 0;
    private static final int IS_TYPE_FINISHLIST = 1;
    private static final int IS_TYPE_DIARY = 2;

    ///////////////////////공통
    private int type; // 일기장인지 버킷리스트인지 피니시리스트인지
    private String date;
    ////////////////////////버킷리스트, 피니시리스트
    private String bucketTitle;
    private String bucketContent;
    /////////////////////////// 일기장
    private String[] questions = new String[2];
    private String[] contents = new String[2];
    private String[] records = new String[2];
    private Uri photoUri;

    public SingerAlbumItem(int type,String date,String bucketTitle, String bucketContent){ // BUCKETLIST, FINISHLIST
        this.type = type; this.bucketTitle = bucketTitle; this.bucketContent = bucketContent; this.date = date;
    }
    public SingerAlbumItem(int type, String date,Uri photoUri, String question1, String content1, String question2, String content2,String record1, String record2){ // DIARY
        this.type = type; this.questions[0] = question1; this.contents[0] = content1; this.questions[1] = question2; this.contents[1] = content2;
        this.date = date; this.photoUri = photoUri; this.records[0] = record1; this.records[1] = record2;
    }

    public int getType(){
        return type;
    }

    public Uri getPhotoUri(){
        return photoUri;
    }
    public String getQuestions(int idx){
        if(type != IS_TYPE_DIARY)
            return "ERROR IN SINGERALBUMITEM";
        return questions[idx];
    }
    public String getContents(int idx) {
        if(type != IS_TYPE_DIARY)
            return "ERROR IN SINGERALBUMITEM";
        return contents[idx];
    }
    public String getTitle(){
        if(type != IS_TYPE_BUCKETLIST && type != IS_TYPE_FINISHLIST)
            return "ERROR IN SINGERALBUMITEM";
        return bucketTitle;
    }
    public String getBucketContent(){
        if(type != IS_TYPE_BUCKETLIST && type != IS_TYPE_FINISHLIST)
            return "ERROR IN SINGERALBUMITEM";
        return bucketContent;
    }
    public String getDate(){
        return date;
    }
    public String getRecord(int idx){return records[idx];}
    public boolean isHaveAudio(){ // 오디오에 널이 있는지 검사 // 있으면 false반환 없으면 true 반환
        for(int i = 0 ; i < records.length;i++){
            if(records[i] != null)
                return true;
        }
        return false;
    }
}
