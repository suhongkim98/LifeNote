package com.example.kimsuhong.lifenote;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddPrivateBuketActivity extends AppCompatActivity {
    EditText title;
    EditText content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_private_buket);
        init();
    }
    private void init(){
        title = (EditText) findViewById(R.id.editTextTitle);
        content = (EditText)findViewById(R.id.editTextContent);
    }
    public void onBackButtonPressed(View v){
        finish();
    }
    public void onAddButtonPressed(View v){
        if(addBuketList()) { // 버킷리스트 추가 성공 시
            Intent intent = new Intent(getApplicationContext(), BuketActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }
    private boolean addBuketList(){ // 버킷리스트 추가 성공 시 true, 실패 시 false;
        if(title.getText().toString().length() == 0 || content.getText().toString().length() == 0) {
            Toast.makeText(getApplicationContext(), "제목이나 텍스트를 입력하지 않았습니다.", Toast.LENGTH_SHORT).show();
            return false;
        }
        String SQL = null;
        if(BuketActivity.nDataBase != null){
            BuketActivity.nDataBase.execSQL("insert into " + NoteDataBase.BUCKETLIST_TABLE_NAME+ "(title, content) values ('"+title.getText()+"','"+content.getText()+"');");
            BuketActivity.nDataBase.execSQL(SQL);
        }
        return true;
    }
}
