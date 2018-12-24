package com.example.kimsuhong.lifenoteadmin;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    Button buttonLogin, buttonRegister;

    EditText serverText; // 테스트용
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init(){
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);

        serverText = (EditText)findViewById(R.id.editTextServerIp);
    }

    public void onLoginButtonPressed(View v){
        final EditText editTextId = (EditText)findViewById(R.id.editTextId);
        final EditText editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        String loginID = editTextId.getText().toString();
        String loginPassword = editTextPassword.getText().toString();
        if(serverText.getText().toString().length() != 0){
            Setting.serverIp = "http://"+serverText.getText().toString();
        }
        Log.d("db","현재 테스트 서버 아이피는" + Setting.serverIp);

        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if(success){
                        Setting.loginKey = jsonResponse.getInt("idx");
                        Toast.makeText(getApplicationContext(), "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);

                        finish();
                        overridePendingTransition(0, 0);
                    }
                    else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage("로그인에 실패하였습니다.");
                        builder.setNegativeButton("다시 시도",null);
                        builder.create();
                        builder.show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        LoginRequest loginRequest = new LoginRequest(loginID,loginPassword,responseListener);
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        queue.add(loginRequest);
    }

    public void onRegisterButtonPressed(View v){
        if(serverText.getText().toString().length() != 0){
            Setting.serverIp = "http://"+serverText.getText().toString();
        }
        Log.d("db","현재 테스트 서버 아이피는" + Setting.serverIp);
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
    }

}
