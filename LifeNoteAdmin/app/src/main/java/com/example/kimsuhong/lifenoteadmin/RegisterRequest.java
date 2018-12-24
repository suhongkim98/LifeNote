package com.example.kimsuhong.lifenoteadmin;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {
    private static String URL = Setting.serverIp+"/healthCare/AdminRegister.php"; // 주소
    private Map<String ,String> parameters;

    public RegisterRequest(String adminID, String adminPassword, String adminName, Response.Listener<String> listener){
        super(Method.POST,URL,listener,null);
        parameters = new HashMap<>();
        parameters.put("adminID",adminID);
        parameters.put("adminPassword",adminPassword);
        parameters.put("adminName",adminName);
    }

    @Override
    public Map<String,String> getParams(){
        return parameters;
    }
}
