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

public class RegisterActivity extends AppCompatActivity {
    EditText editTextId, editTextPassword, editTextCheckPassword, editTextName;
    Button registerButton;
    boolean validate = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }
    private void init(){
        editTextId = (EditText) findViewById(R.id.editTextId);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextCheckPassword = (EditText) findViewById(R.id.editTextPasswordCheck);
        editTextName = (EditText) findViewById(R.id.editTextName);
        registerButton = (Button) findViewById(R.id.buttonRegister);
    }

    private boolean canYouRegister(){ // 가입 가능하면 true, 불가 시 false
        if(editTextPassword.getText().toString().equals("")) {//비밀번호가 공백이면 return false
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
            builder.setMessage("비밀번호가 공백입니다.");
            builder.setNegativeButton("확인",null);
            builder.create();
            builder.show();
            return false;
        }
        if(!(editTextPassword.getText().toString().equals(editTextCheckPassword.getText().toString()))) {//비밀번호가 체크비번하고 다르면 return false
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
            builder.setMessage("비밀번호가 서로 다릅니다.");
            builder.setNegativeButton("확인",null);
            builder.create();
            builder.show();
            return false;
        }
        if(editTextName.getText().toString().equals("")){ //이름이 공백이면
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
            builder.setMessage("이름이 공백입니다.");
            builder.setNegativeButton("확인",null);
            builder.create();
            builder.show();
            return false;
        }
        return true;
    }
    public void onIDValidateButtonPressed(View v){
        String id = editTextId.getText().toString();

        if(id.equals("")) {
            Toast.makeText(getApplicationContext(),"아이디가 공백입니다.",Toast.LENGTH_SHORT).show();
            return; // 공백이면 함수 종료
        }
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success"); // success가 true면 기존아이디가 없다는 뜻이고 false면 기존아이디가 존재한다
                    if(success) {
                        Button buttonValidate = (Button)findViewById(R.id.buttonValidate);
                        editTextId.setEnabled(false); // 아이디 못바꾸게
                        buttonValidate.setText("사용 가능");
                        validate = true;
                        Toast.makeText(getApplicationContext(),"사용 가능한 아이디입니다.",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(RegisterActivity.this, "사용 불가능한 아이디입니다.", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    validate = false;
                    Toast.makeText(getApplicationContext(),"에러",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();;
                }
            }
        };
        ValidateRequest validateRequest = new ValidateRequest(id,responseListener);
        RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
        queue.add(validateRequest);
    }

    public void onRegisterButtonPressed(View v){
        String adminID = editTextId.getText().toString();
        String adminPassword = editTextPassword.getText().toString();
        String adminName = editTextName.getText().toString();

        if(!canYouRegister()){ //텍스트가 빈칸인지 등 검사
            return; // 함수를 강제로 종료시킴
        }
        if(!validate){
            Toast.makeText(getApplicationContext(),"아이디 중복 검사를 하지 않았습니다.",Toast.LENGTH_SHORT).show();
            return;
        }
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if(success){
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                        builder.setMessage("회원등록에 성공하였습니다.");
                        builder.setPositiveButton("확인",null);
                        builder.create();
                        builder.show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                        builder.setMessage("회원등록에 실패하였습니다.");
                        builder.setNegativeButton("확인",null);
                        builder.create();
                        builder.show();
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        RegisterRequest registerRequest = new RegisterRequest(adminID,adminPassword,adminName,responseListener);
        RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
        queue.add(registerRequest);
    }
}
