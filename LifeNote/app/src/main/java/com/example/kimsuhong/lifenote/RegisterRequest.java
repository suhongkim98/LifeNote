package com.example.kimsuhong.lifenote;

import android.os.Debug;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {
    private static String URL = Setting.serverIp+"/healthCare/ClientRegister.php"; // 주소
    private Map<String ,String> parameters;

    public RegisterRequest(String clientID, String clientPassword, String clientName, String clientBirth, String clientGender, Response.Listener<String> listener){
        super(Method.POST,URL,listener,null);
        parameters = new HashMap<>();
        parameters.put("clientID",clientID);
        parameters.put("clientPassword",clientPassword);
        parameters.put("clientName",clientName);
        parameters.put("clientBirth",clientBirth);//Date로 해야하나?
        parameters.put("clientGender",clientGender);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd"); // 현재 시간을 구함
        String getTime = dateFormat.format(date);

        parameters.put("joinDate",getTime);

        Log.d("db",clientID + "," + clientPassword+ "," + clientName+ "," + clientBirth+ "," + clientGender+ "," + getTime);
    }

    @Override
    public Map<String,String> getParams(){
        return parameters;
    }
}
