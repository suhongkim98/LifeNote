package com.example.kimsuhong.lifenote;

import com.example.kimsuhong.lifenote.BucketList;

public class SingerMyFinishListItem {
    private BucketList bucketList;
    public SingerMyFinishListItem(){
        bucketList = new BucketList();
    }

    public SingerMyFinishListItem(String title){
        bucketList = new BucketList(title);
    }

    public SingerMyFinishListItem(String title, String content){
        bucketList = new BucketList(title,content);
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
}
