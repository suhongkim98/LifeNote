package com.example.kimsuhong.lifenote;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.w3c.dom.Text;

import java.io.File;

public class ShowDiaryActivity extends AppCompatActivity{
    int[] multiTextIndexArr = {R.id.multiText1,R.id.multiText2};
    TextView[] multiTextArr = new TextView[multiTextIndexArr.length];

    int[] buttonAudioArr = {R.id.buttonAudio1,R.id.buttonAudio2};
    Button[] buttonAudio = new Button[multiTextArr.length];
    int[] questionTextIndexArr = {R.id.question1Text,R.id.question2Text};
    TextView[] questionTextArr = new TextView[questionTextIndexArr.length];
    TextView dateText;
    ImageView imageView;
    private String idx;

    String[] audioPath = new String[multiTextArr.length];
    String imageID = null;
    public static NoteDataBase nDataBase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_diary); // add다이어리 레이아웃과 전체적인 스타일은 비슷하다;
        init();
    }
    private void init(){
        for(int i = 0 ; i < questionTextIndexArr.length;i++){
            questionTextArr[i] = (TextView)findViewById(questionTextIndexArr[i]);
            multiTextArr[i] = (TextView) findViewById(multiTextIndexArr[i]);
            buttonAudio[i] = findViewById(buttonAudioArr[i]);
        }

        dateText = (TextView)findViewById(R.id.dateText);
        imageView = (ImageView) findViewById(R.id.imageView);

        initImageLoader(getApplicationContext());
    }
    public void onAudioPlayButtonPressed(View v){
        switch (v.getId()){
            case R.id.buttonAudio1:
                openPlayDialog(audioPath[0],questionTextArr[0].getText().toString());
                break;
            case R.id.buttonAudio2:
                openPlayDialog(audioPath[1],questionTextArr[1].getText().toString());
                break;

        }
        //
    }
    private void openPlayDialog(String audioPath,String question){
        PlayAudioDialog playAudioDialog = PlayAudioDialog.newInstance(audioPath,question);
        playAudioDialog.show(getSupportFragmentManager(),"select audio dialog");
    }
    private void initImageLoader(Context context){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(context);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    @Override
    protected void onStart(){
        openDatabase();
        Intent intent = getIntent();

        String question1 = intent.getExtras().getString("question1");
        String question2 = intent.getExtras().getString("question2");
        String content1 = intent.getExtras().getString("content1");
        String content2 = intent.getExtras().getString("content2");
        String date = intent.getExtras().getString("date");

        if(!intent.getExtras().getString("imageID").equals("null"))
            imageID = intent.getExtras().getString("imageID");

        String idx = intent.getExtras().getString("idx");
        if(!intent.getExtras().getString("record1").equals("null"))
            audioPath[0] = intent.getExtras().getString("record1");
        if(!intent.getExtras().getString("record2").equals("null"))
            audioPath[1] = intent.getExtras().getString("record2");

        dateText.setText(date);
        multiTextArr[0].setText(content1);
        multiTextArr[1].setText(content2);

        questionTextArr[0].setText(question1);
        questionTextArr[1].setText(question2);
        this.idx = idx; // PMKEY
        UniversalImageLoader.setImage(imageID,imageView,null);

        for (int i = 0 ; i < audioPath.length;i++){
            if(audioPath[i]!=null){
                multiTextArr[i].setVisibility(View.GONE);
            }
            else{
                buttonAudio[i].setVisibility(View.GONE);
            }
        }
        super.onStart();
    }
    public void openDatabase(){
        if(nDataBase != null){
            nDataBase.close();
            nDataBase = null;
        }

        nDataBase = NoteDataBase.getInstance(this);
        boolean isOpen = nDataBase.open();
        if(isOpen){
            Log.d("db",NoteDataBase.DATABASE_NAME + " is open");
        }else{

            Log.d("db",NoteDataBase.DATABASE_NAME + " is not open");
        }
    }
    public void onBackButtonPressed(View v){
        finish();
    }
    private boolean deleteDiary(){
        try {

            deleteDiaryFile();
            //이미지를 삭제하고
            //음성파일이 존재하면 음성파일을 삭제한다.

            //다음 일기장데이터를 테이블에서 삭제한다.
            String SQL = "delete from " + nDataBase.DIARY_TABLE_NAME
                    + " where _idx = '" + idx + "';";
            nDataBase.execSQL(SQL); // 일기장 데이터 삭제
        } catch (Exception e){
            Toast.makeText(getApplicationContext(),"일기장 삭제 과정에서 오류가 발생했습니다, 관리자에게 문의해주세요",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }
        return true;
    }
    private void deleteDiaryFile(){
        for(int i = 0 ; i < audioPath.length;i++){
            if(audioPath[i] != null){
                File f = new File(audioPath[i]);
                if(f.delete()){
                    Log.d("diary",audioPath[i]+" 삭제 성공");
                }
                else{
                    Log.d("diary",audioPath[i]+" 삭제 실패");
                }
            }
        }
        if(imageID != null){ // 파일 삭제
            File f = new File(imageID);
            if(f.delete()){
                Log.d("diary","photo 삭제 성공");
            }
            else{
                Log.d("diary","photo 삭제 실패");
            }
            getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(f)));
        }
    }
    public void onDeleteButtonPressed(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("정말 일기장을 삭제하실건가요? 복구가 불가능해요.");
        builder.setTitle("알림 창");
        builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(deleteDiary()) { // 일기장 삭제
                    Toast.makeText(getApplicationContext(),"일기장을 삭제하였습니다.",Toast.LENGTH_SHORT).show();
                }
                finish(); // 액티비티 종료
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel(); // 팝업창 닫음
            }
        });
        builder.create();
        builder.show();
    }
}
