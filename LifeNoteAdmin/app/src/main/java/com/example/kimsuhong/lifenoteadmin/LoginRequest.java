package com.example.kimsuhong.lifenoteadmin;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends StringRequest {
    private static String URL = Setting.serverIp+"/healthCare/AdminLogin.php"; // 주소
    private Map<String ,String> parameters;

    public LoginRequest(String adminID, String adminPassword , Response.Listener<String> listener){
        super(Method.POST,URL,listener,null);
        parameters = new HashMap<>();
        parameters.put("adminID",adminID);
        parameters.put("adminPassword",adminPassword);
    }

    @Override
    public Map<String,String> getParams(){
        return parameters;
    }
}
