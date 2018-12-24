package com.example.kimsuhong.lifenote;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NoteDataBase {
    public static final int DATABASE_VERSION = 8;
    public static final String DATABASE_NAME = "lifenotedb";
    public static final String BUCKETLIST_TABLE_NAME = "bucketlist";
    public static final String FINISHLIST_TABLE_NAME = "finishlist";
    public static final String DIARY_TABLE_NAME = "diarytable";
    public static final String IDPHOTO_TABLE_NAME = "idphoto";

    private static NoteDataBase database;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private Context context;

    private NoteDataBase(Context context){
        this.context = context;
    }

    public static NoteDataBase getInstance(Context context){
        if(database == null){
            database = new NoteDataBase(context);
        }
        return database;
    }

    public boolean open(){
        println("closing database [" + DATABASE_NAME + "].");
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        return true;
    }

    public void close(){
        println("closing database [" + DATABASE_NAME + "].");
        db.close();
        database = null;
    }
    public Cursor rawQuery(String SQL){
        Cursor c1 = null;
        try{
            c1 = db.rawQuery(SQL,null);
            println("cursor count"+c1.getCount());
        }catch (Exception e){
            Log.e("db","exception in excutequery",e);
        }
        return c1;
    }
    public  boolean execSQL(String SQL){
        println("\nexecute called.\n");

        try {
            Log.d("db", "SQL : " + SQL);
            db.execSQL(SQL);
        } catch(Exception ex) {
            Log.e("db", "Exception in executeQuery", ex);
            return false;
        }
        return true;
    }

    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context){
            super(context, DATABASE_NAME,null,DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) { // 앱 인스톨 후 한번 실행되고 더이상 실행안됨

            try{ // 이미 테이블이 있을 경우 삭제 시도
                String dropSql = "drop table if exists " + BUCKETLIST_TABLE_NAME;
                sqLiteDatabase.execSQL(dropSql);
            } catch (Exception e) { // 삭제 실패 시 에러 출력
                Log.e("db", "dropSql(bucketlist) 에서 에러 발생 ",e);
            }


            String bucketListSql = "create table "+ BUCKETLIST_TABLE_NAME + "(" // 테이블 생성 SQL
                    + "_idx INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                    + "receiveDate DATE NOT NULL default CURRENT_DATE,"
                    + "title TEXT DEFAULT '', "
                    + "content TEXT DEFAULT '')";

            try{ // 테이블 생성 시도
                sqLiteDatabase.execSQL(bucketListSql);
            }catch (Exception e){ // 생성 실패 시 에러 출력
                Log.e("db","bucketListSql 에서 에러 발생 ",e);
            }

            try{ // 이미 테이블이 있을 경우 삭제 시도
                String dropSql = "drop table if exists " + FINISHLIST_TABLE_NAME;
                sqLiteDatabase.execSQL(dropSql);
            } catch (Exception e) { // 삭제 실패 시 에러 출력
                Log.e("db", "dropSql(finishList) 에서 에러 발생 ",e);
            }
            String finishListSql = "create table " + FINISHLIST_TABLE_NAME + "("
                    + "_idx INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                    + "finishDate DATE NOT NULL default CURRENT_DATE,"
                    + "title TEXT DEFAULT '',"
                    + "content TEXT DEFAULT '')";

            try{ //테이블 생성 시도
                sqLiteDatabase.execSQL(finishListSql);
            } catch (Exception e) {
                Log.e("db","finishListSql에서 에러 발생",e);
            }
///////////
            try{ // 이미 테이블이 있을 경우 삭제 시도
                String dropSql = "drop table if exists " + DIARY_TABLE_NAME;
                sqLiteDatabase.execSQL(dropSql);
            } catch (Exception e) { // 삭제 실패 시 에러 출력
                Log.e("db", "dropSql(diarytable) 에서 에러 발생 ",e);
            }
            String diarySql = "create table " + DIARY_TABLE_NAME + "("
                    + "_idx INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                    + "selectDate DATE NOT NULL default CURRENT_DATE," // 선택한 날짜
                    + "createDate DATE NOT NULL default CURRENT_DATE," // 업로드한 날짜
                    + "id_PHOTO TEXT DEFAULT NULL,"
                    + "question1 TEXT DEFAULT '',"
                    + "content1 TEXT DEFAULT '',"
                    + "id_record1 TEXT DEFAULT NULL,"
                    + "question2 TEXT DEFAULT '',"
                    + "content2 TEXT DEFAULT '',"
                    + "id_record2 TEXT DEFAULT NULL)";

            try{ //테이블 생성 시도
                sqLiteDatabase.execSQL(diarySql);
            } catch (Exception e) {
                Log.e("db","diarySql에서 에러 발생",e);
            }
            ///////////

            try{ // 이미 테이블이 있을 경우 삭제 시도
                String dropSql = "drop table if exists " + IDPHOTO_TABLE_NAME;
                sqLiteDatabase.execSQL(dropSql);
            } catch (Exception e) { // 삭제 실패 시 에러 출력
                Log.e("db", "dropSql(photoTable) 에서 에러 발생 ",e);
            }
/////////////////////////////////////////////////////////////////////////////////////////
            String IDPhotoSql = "create table " + IDPHOTO_TABLE_NAME + "("
                    + "_idx INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                    + "URL TEXT,"
                    + "createDate DATE NOT NULL default CURRENT_DATE)";

            try{ //테이블 생성 시도
                sqLiteDatabase.execSQL(IDPhotoSql);
            } catch (Exception e) {
                Log.e("db","idPhotoSql에서 에러 발생",e);
            }
        }
        public void onOpen(SQLiteDatabase db){
            Log.d("db","데이터베이스 오픈 db: " + DATABASE_NAME);
        }
        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) { // 버전이 바뀔 때만 호출
            Log.w("db","데이터 베이스가 업데이트 되었습니다. " + oldVersion + " To " + newVersion + "기존 테이블을 삭제하고 다시 생성합니다.");
            onCreate(sqLiteDatabase); // onCreate함수 재호출
        }
    }
    private void println(String msg) {
        Log.d("db", msg);
    }
}
