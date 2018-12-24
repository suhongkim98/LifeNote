package com.example.kimsuhong.lifenote;

public class SingerMyBuketlistItem {
    private BucketList bucketList;
    private String idx;
    public SingerMyBuketlistItem(String idx){
        bucketList = new BucketList();
        this.idx=idx;
    }

    public SingerMyBuketlistItem(String idx, String title){
        this.idx=idx;
        bucketList = new BucketList(title);
    }

    public SingerMyBuketlistItem(String idx, String title, String content){
        bucketList = new BucketList(title,content);
        this.idx = idx;
    }

    public String getTitle(){
        return bucketList.getTitle();
    }
    public void setTitle(String title){
        bucketList.setTitle(title);
    }
    public String getContent(){
        return bucketList.getContent();
    }
    public void setContent(String content){
        bucketList.setContent(content);
    }
    public String getIdx(){
        return idx;
    }
}
