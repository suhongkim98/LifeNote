package com.example.kimsuhong.lifenote;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    Button buketButton,gguButton,albumButton,conButton;
    TextView nameText;
    LinearLayout nameLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ini();
    }

    private void ini(){
        buketButton = (Button)findViewById(R.id.toBuketButton);
        gguButton = (Button)findViewById(R.id.toGguButton);
        albumButton = (Button)findViewById(R.id.toAlbumButton);
        conButton = (Button)findViewById(R.id.toConfigurationButton);
        nameText = (TextView) findViewById(R.id.nameText);
        nameText.setText(Setting.name);
        nameLayout = (LinearLayout)findViewById(R.id.nameLayout);
        if(Setting.loginKey == -1)
            nameLayout.setVisibility(View.INVISIBLE);

        try {
            File dir = new File(Setting.pictureFolerPath);
            File output = new File(dir, ".nomedia");// nomedia 추가해서 이미지가 갤러리에 안보이게 함
            boolean fileCreated = output.createNewFile();
            if(fileCreated) Log.v("nomedia","nomedia 생성 됨"); // 파일 생성에 성공하였다면
            else Log.v("nomedia","nomedia 가 이미 있음"); // 파일이 이미 있으면
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onToBuketButtonClicked(View v){
        Intent intent = new Intent(getApplicationContext(),BuketActivity.class);
        startActivity(intent);
    }

    public void onToGguButtonClicked(View v){
        Intent intent = new Intent(getApplicationContext(),GguActivity.class);
        startActivity(intent);

    }
    public void onToAlbumButtonClicked(View v){
        Intent intent = new Intent(getApplicationContext(),AlbumAcitivity.class);
        startActivity(intent);

    }
    public void onToConfigurationButtonClicked(View v){
        Intent intent = new Intent(getApplicationContext(),ConfigurationActivity.class);
        startActivity(intent);
    }
}
