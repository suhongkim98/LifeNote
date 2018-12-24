package com.example.kimsuhong.lifenote;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    EditText editTextId, editTextPassword, editTextCheckPassword, editTextName, editTextBirth;
    Button registerButton;

    boolean validate;
    String clientGender = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }

    private void init(){
        editTextId = (EditText) findViewById(R.id.editTextId);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);
        editTextCheckPassword = (EditText)findViewById(R.id.editTextPasswordCheck);
        editTextName = (EditText)findViewById(R.id.editTextName);
        editTextBirth = (EditText) findViewById(R.id.editTextBirth);
        registerButton = (Button) findViewById(R.id.buttonRegister);

        RadioGroup genderGroup = (RadioGroup)findViewById(R.id.genderGroup);
        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { // gender 그룹 체크 변환 시
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton genderButton = (RadioButton) findViewById(i);
                clientGender = genderButton.getText().toString();
            }
        });

    }

    public void onRegisterButtonPressed(View v){
        String clientID = editTextId.getText().toString();
        String clientPassword = editTextPassword.getText().toString();
        String clientName = editTextName.getText().toString();
        String clientBirth = editTextBirth.getText().toString();

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
        RegisterRequest registerRequest = new RegisterRequest(clientID,clientPassword,clientName,clientBirth,clientGender,responseListener);
        RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
        queue.add(registerRequest);
    }
    private boolean canYouRegister(){ // 가입 가능하면 true, 불가 시 false
        if(editTextPassword.getText().toString().equals("")) {//비밀번호가 공백이면 return false
            negativeAlertDialog("비밀번호가 공백입니다.");
            return false;
        }
        if(!(editTextPassword.getText().toString().equals(editTextCheckPassword.getText().toString()))) {//비밀번호가 체크비번하고 다르면 return false
            negativeAlertDialog("비밀번호가 서로 다릅니다.");
            return false;
        }
        if(editTextName.getText().toString().equals("")){ //이름이 공백이면
            negativeAlertDialog("이름이 공백입니다.");
            return false;
        }
        //// ******************나중에 생년월일 입력 칸은 스피너로 바꿀 예정임!******************
        if(editTextBirth.getText().toString().length() == 10){
            int year = Integer.parseInt(editTextBirth.getText().toString().substring(0,4));
            int month = Integer.parseInt(editTextBirth.getText().toString().substring(5,7));
            int day = Integer.parseInt(editTextBirth.getText().toString().substring(8));
            if(month < 1 || month > 12){
                negativeAlertDialog("1월부터 12월 사이 값을 입력해주세요");
                return false;
            }
            else if(day < 1 || day > 31){
                negativeAlertDialog("1일부터 31일 사이 값을 입력해주세요");;
                return false;
            }
        }
        else {
            negativeAlertDialog("생년월일이 올바르지 않습니다 ex)1998/08/05 형식을 맞춰주세요");
            return false;
        }
        //// ******************나중에 생년월일 입력 칸은 스피너로 바꿀 예정임!******************

        if(clientGender.equals("")){
            negativeAlertDialog("성별 선택을 하지 않았습니다.");
            return false;
        }
        return true;
    }
    private void negativeAlertDialog(String text){
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setMessage(text);
        builder.setNegativeButton("확인",null);
        builder.create();
        builder.show();
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
}
