package com.example.kimsuhong.lifenoteadmin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    TextView testName,testId,testPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
    private void init(){
        testName = (TextView)findViewById(R.id.textViewName);
        testId = (TextView)findViewById(R.id.textViewId);
        testPassword = (TextView)findViewById(R.id.textViewPassword);

    }
}
