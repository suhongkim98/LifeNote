package com.example.kimsuhong.lifenote;

public class BucketList {
    private String title;
    private String content;
    public BucketList(){
        init("없음","없음");
    }
    public BucketList(String title){
        init(title,"없음");
    }
    public BucketList(String title,String content){
        init(title,content);
    }

    private void init(String title, String content){
        this.title = title;
        this.content = content;
    }
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getContent(){
        return content;
    }
    public void setContent(String content){
        this.content = content;
    }
}
