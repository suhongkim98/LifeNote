package com.example.kimsuhong.lifenote;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class BuketAddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buket_add);
    }
    public void onBackButtonPressed(View v){
        finish();
    }
    public  void onPrivateBuketButtonPressed(View v){
        Intent intent = new Intent(getApplicationContext(),AddPrivateBuketActivity.class);
        startActivity(intent);
    }
    public void onGroupBucketButtonPressed(View v){
        Toast.makeText(getApplicationContext(),"준비 중인 기능입니다.",Toast.LENGTH_SHORT).show();
    }
}
