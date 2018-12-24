package com.example.kimsuhong.lifenote;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ConfigurationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        ini();
    }

    private void ini(){

    }
    public void onBackButtonPressed(View v){
        finish();
    }
}
