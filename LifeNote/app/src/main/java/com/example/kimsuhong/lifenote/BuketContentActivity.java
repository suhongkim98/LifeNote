package com.example.kimsuhong.lifenote;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class BuketContentActivity extends AppCompatActivity {
    TextView textViewTitle, textViewContent;
    public static NoteDataBase nDataBase;
    String idx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buket_content);
        init();
    }

    @Override
    protected void onStart(){
        Intent intent = getIntent();
        String title = intent.getExtras().getString("buckettitle");
        String content = intent.getExtras().getString("bucketcontent");
        idx = intent.getExtras().getString("idx");

        textViewTitle.setText(title);
        textViewContent.setText(content);

        openDatabase();
        super.onStart();
    }

    private void init(){
        textViewTitle = (TextView)findViewById(R.id.textViewTitle);
        textViewContent = (TextView) findViewById(R.id.textViewContent);
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

    public void onFinishButtonPressed(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("정말 버킷리스트를 완료하셨나요?");
        builder.setTitle("알림 창");
        builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String SQL = "insert into "+ nDataBase.FINISHLIST_TABLE_NAME
                        + "(title, content) values "
                        +"('"+ textViewTitle.getText()+"','"+textViewContent.getText()+"');";
                nDataBase.execSQL(SQL); // finish리스트에 데이터 삽입

                deleteDiary(); // 일기장 삭제
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
    private boolean deleteDiary(){
        try {
            String SQL = "delete from " + nDataBase.BUCKETLIST_TABLE_NAME
                    + " where _idx = '" + idx + "';";
            nDataBase.execSQL(SQL); // 버킷리스트 데이터 삭제
        } catch (Exception e){
            Toast.makeText(getApplicationContext(),"버킷리스트 삭제 과정에서 오류가 발생했습니다, 관리자에게 문의해주세요",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public void onDeleteButtonPressed(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("정말 버킷리스트를 삭제하실건가요? 복구가 불가능해요.");
        builder.setTitle("알림 창");
        builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(deleteDiary()) { // 일기장 삭제
                    Toast.makeText(getApplicationContext(),"버킷리스트를 삭제하였습니다.",Toast.LENGTH_SHORT).show();
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
    public void onBackButtonPressed(View v){
        finish();
    }
}
