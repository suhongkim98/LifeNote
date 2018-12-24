package com.example.kimsuhong.lifenote;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ValidateRequest extends StringRequest {
    private static String URL = Setting.serverIp+"/healthCare/clientValidate.php";
    private Map<String, String> parameters;

    public ValidateRequest(String clientID, Response.Listener<String> listener){
        super(Method.POST,URL,listener,null);
        parameters = new HashMap<>();
        parameters.put("clientID",clientID);
    }
    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
