package com.example.kimsuhong.lifenote;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends StringRequest {
    private static String URL = Setting.serverIp+"/healthCare/ClientLogin.php"; // 주소
    private Map<String ,String> parameters;

    public LoginRequest(String clientID, String clientPassword, Response.Listener<String> listener){
        super(Method.POST,URL,listener,null);
        parameters = new HashMap<>();
        parameters.put("clientID",clientID);
        parameters.put("clientPassword",clientPassword);
    }

    @Override
    public Map<String,String> getParams(){
        return parameters;
    }
}
