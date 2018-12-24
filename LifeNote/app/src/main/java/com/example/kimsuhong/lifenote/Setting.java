package com.example.kimsuhong.lifenote;

import android.os.Environment;

import java.io.File;

public class Setting {
    public static String serverIp = "http://192.168.0.9"; //http://183.99.228.56
    public static String pictureFolerPath = Environment.getExternalStorageDirectory()+"/LifeNote/pictures";
    public static int loginKey = -1; // DEFAULT = -1
    public static String name = "사용자";
}
