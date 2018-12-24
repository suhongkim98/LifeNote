package com.example.kimsuhong.lifenote;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class AddDiaryActivity extends AppCompatActivity implements SelectImageDialog.SelectImageDialogListener,SelectRecordDialog.SelectRecordDialogListener{
    int year, month, day;
    TextView dateText;
    int[] multiTextIndexArr = {R.id.multiText,R.id.multiText2};
    EditText[] multiTextArr = new EditText[multiTextIndexArr.length];
    String[] recordPath = new String[multiTextArr.length];

    int[] questionTextIndexArr = {R.id.question1Text,R.id.question2Text};
    TextView[] questionTextArr = new TextView[questionTextIndexArr.length];

    int[] recordButtonIndexArr = {R.id.buttonRecord1,R.id.buttonRecord2}; // 녹음 버튼
    Button[] recordButtonArr = new Button[recordButtonIndexArr.length];

    ImageView imageView;
    private boolean saveSuccess = false;
    private final int REQUEST_GALLERY_CODE=1112;
    private final int REQUEST_IMAGE_CROP=1113;
    private final int MY_PERMISSIONS_REQUEST = 1;

//////////////////////////////////////http://g-y-e-o-m.tistory.com/48
    String mCurrentPhotoPath;
    Uri albumUri, photoUri;
    ///////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_diary);
        init();
    }
    @Override
    public void onDestroy(){
        if(!saveSuccess) { // 세이브 하고 액티비티가 Destroy가 아니라면
            for (int i = 0; i < recordPath.length; i++) {
                if (recordPath[i] != null) {
                    File f = new File(recordPath[i]);
                    f.delete(); // 파일 삭제
                }
            }
        }
        super.onDestroy();
    }
    private void init(){
        requestPermission(); // 권한 요청
        imageView = (ImageView) findViewById(R.id.imageView);
        dateText = (TextView)findViewById(R.id.dateText);
        for (int i = 0 ; i < multiTextIndexArr.length;i++){
            multiTextArr[i] = (EditText)findViewById(multiTextIndexArr[i]);
        }
        for(int i = 0 ; i < questionTextIndexArr.length;i++){
            questionTextArr[i] = (TextView)findViewById(questionTextIndexArr[i]);
        }
        for(int i = 0 ; i < recordButtonIndexArr.length;i++){
            recordButtonArr[i] = (Button)findViewById(recordButtonIndexArr[i]);
        }

        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd"); // 현재 시간을 구함
        int year = Integer.parseInt(dateFormat.format(date).substring(0,4));
        int month = Integer.parseInt(dateFormat.format(date).substring(5,7));
        int day = Integer.parseInt(dateFormat.format(date).substring(8,10));
        setDate(year,month,day);
    }


    public void onSubmitButtonPressed(View v){
        if(isAllWrite()){
            saveDiary(); // 저장하는 함수 실행
        }
        else{
            Toast.makeText(getApplicationContext(),"모든 질문에 답을 해주세요!",Toast.LENGTH_SHORT).show();
        }
    }
    public boolean isAllWrite(){
        //모든 텍스트가 작성되었는지 확인하는 함수
        for(int i = 0 ; i < multiTextIndexArr.length ; i++){
            if(multiTextArr[i].getText().toString().equals("") && multiTextArr[i].getVisibility() == View.VISIBLE) // VISIBLE가 GONE라면 거기엔 음성파일이 대신 작성되어 있다.
                return false;
        }
        return true;
    }

    private void saveDiary(){
        File albumFile = null;
        try {
            albumFile = createImageFile();
            albumUri = Uri.fromFile(albumFile); // albumUri를 DB에 저장하자(새로 저장한 이미지 경로이다.)
            cropImage(); // 이미지를 크롭한다. --> 후에 저장

            String SQL = null;
            if(GguActivity.nDataBase != null){ // SQL 삽입
                GguActivity.nDataBase.execSQL("insert into " + NoteDataBase.DIARY_TABLE_NAME+
                        "(id_PHOTO, selectDate,question1, content1,id_record1,question2, content2,id_record2) values " +
                        "('"+albumUri+"','"+year+"/"+month+"/"+day+"','"
                        +questionTextArr[0].getText()+"','"+multiTextArr[0].getText()+"','"+recordPath[0]+"','"
                        +questionTextArr[1].getText()+"','"+multiTextArr[1].getText()+"','"+recordPath[1]+"');");
                GguActivity.nDataBase.execSQL(SQL);
            }
            saveSuccess = true;
            finish();
        }
        catch (IOException e){
            Log.e("TakeAlbumSingle Error",e.toString());
        }

    }
    ///////////////////////////////////// 사진 크롭 후 저장
    private File createImageFile() throws IOException{ // 이미지 파일 생성
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(System.currentTimeMillis()));
        String imgFileName = "JPEG_"+timeStamp+".jpg";
        File imgFile = null;
        File storageDir = new File(Setting.pictureFolerPath,"ssong");

        if(!storageDir.exists()){
            Log.i("mCurrentPhotoPath1",storageDir.toString());
            storageDir.mkdirs();
        }

        imgFile = new File(storageDir,imgFileName);
        mCurrentPhotoPath = imgFile.getAbsolutePath();
        return imgFile;
    }
    private void galleryAddPic(){
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
        Toast.makeText(getApplicationContext(), "사진이 저장되었습니다.", Toast.LENGTH_SHORT).show();
    }

    private void cropImage(){
        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropIntent.setDataAndType(photoUri,"image/*");

        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY",1);
        cropIntent.putExtra("scale",true);
        cropIntent.putExtra("output",albumUri);
        startActivityForResult(cropIntent,REQUEST_IMAGE_CROP);
    }
    /////////////////////////////////////

    public void onImagePressed(View v){ // 이미지 클릭했을 때
        openImageDialog();
    }

    ////////////////////////////////////////////////////////////////////    이미지 선택 openImageDialog();  ////////////////////////////////////////////////////////////////////
    @Override
    public void applySelectRadioResult(int checkedIndex){ // SelectImageDialog.SelectImageDialogListener 안에 있다.
        try{ // 나중에 카메라로 사진찍어 이미지를 업로드하는 것을 대비하기 위해 코드를 짜두고 주석처리를 하겠다.
            if(checkedIndex == 0){ // 앨범으로 사진 선택
                showPhotoSelectActivity();
            }
            /*
            if(checkedIndex == 1){ // 카메라로 사진 선택
                showPhotoCaptureActivity();
            }
            */
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(),"이미지 불러오기 부분 다이얼로그에서 오류가 발생하였습니다. 관리자에게 문의해주세요",Toast.LENGTH_LONG).show();
        }
    }

    private void showPhotoSelectActivity(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_GALLERY_CODE);
    }
    /*
    private void showPhotoCaptureActivity(){

    }
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_GALLERY_CODE:
                    sendPicture(data.getData()); //갤러리에서 가져와서 imgview에 세팅
                    photoUri = data.getData();
                    break;
                case REQUEST_IMAGE_CROP:
                    galleryAddPic();
                    break;
                default:
                    break;
            }

        }
    }
    private void sendPicture(Uri imgUri) {
        String imagePath = getRealPathFromURI(imgUri); // path 경로

        Toast.makeText(getApplicationContext(), "경로:"+imagePath, Toast.LENGTH_SHORT).show();
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int exifDegree = exifOrientationToDegrees(exifOrientation);

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);//경로를 통해 비트맵으로 전환
        imageView.setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기
    }
    private String getRealPathFromURI(Uri contentUri) {
        int column_index=0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }
        return cursor.getString(column_index);
    }
    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }
    private Bitmap rotate(Bitmap src, float degree) {

// Matrix 객체 생성
        Matrix matrix = new Matrix();
// 회전 각도 셋팅
        matrix.postRotate(degree);
// 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(),
                src.getHeight(), matrix, true);
    }
    public void openImageDialog(){
        SelectImageDialog selectImageDialog = new SelectImageDialog();
        selectImageDialog.show(getSupportFragmentManager(),"select dialog");
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////    권한요청;  ////////////////////////////////////////////////////////////////////

    private void requestPermission() {
        String[] permissions = { // 권한들
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        };
        if(!hasPermissions(this,permissions)){ // 하나라도 권한이 없으면
            ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST);// 해당 권한들을 요청한다
        }
    }
    public static boolean hasPermissions(Context context, String... permissions) { // 권한있는지 체크해주는 함수
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { // 권한 수락버튼을 눌렀다면
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.


                } else { // 권한 거부버튼을 누르면
                    Toast.makeText(getApplicationContext(), "권한이 없어 이용 불가능한 기능입니다. 앱 설정에서 권한을 주세요", Toast.LENGTH_LONG).show();
                    finish(); // 권한 요구를 거부하면 창을 닫는다.

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }
    //////////////////////////////////////////////음성/////////////////////////////////////////////////////////
    public void onRecordButtonPressed(View v){ // 녹음 버튼 눌렀을 때
        switch (v.getId()){
            case R.id.buttonRecord1:
                if(recordPath[0] == null) { // 음성 녹화를 이미 한 상태가 아니라면
                    openRecordDialog(1,questionTextArr[0].getText().toString());
                }
                else { // 음성 녹화를 이미 했으면
                    Toast.makeText(getApplicationContext(),"재생하는다이어로그띄우기",Toast.LENGTH_SHORT).show();
                    openRecordDialog(1,recordPath[0],questionTextArr[0].getText().toString());
                }
                break;
            case R.id.buttonRecord2:
                if(recordPath[1] == null) {
                    openRecordDialog(2,questionTextArr[1].getText().toString());
                }
                else {
                    Toast.makeText(getApplicationContext(),"재생하는다이어로그띄우기",Toast.LENGTH_SHORT).show();
                    openRecordDialog(2,recordPath[1],questionTextArr[1].getText().toString());
                }
                break;
        }
    }
    private void openRecordDialog(int index,String question){
        SelectRecordDialog selectRecordDialog = SelectRecordDialog.newInstance(index,question);
        selectRecordDialog.show(getSupportFragmentManager(),"select record dialog");
    }
    private void openRecordDialog(int index, String recordPath,String question){
        SelectRecordDialog selectRecordDialog = SelectRecordDialog.newInstance(index,recordPath,question);
        selectRecordDialog.show(getSupportFragmentManager(),"select record dialog");
    }
    @Override
    public void saveRecord(int index,String path){  // index는 1부터 시작한다. (질문이 1번부터라..ㅎㅎ)
        // 저장버튼을 눌렀을 시 해당 파일의 경로를 어떠한 변수에 저장하는 함수 /// 그 변수를 insert하여 테이블에 삽입할 예정 // 뒤로가기 누르면 삭제하기
        multiTextArr[index - 1].setText("클릭하여 음성 파일 재생");
        multiTextArr[index - 1].setVisibility(View.INVISIBLE); // 텍스트 입력 못하게 막아둔다.
        recordPath[index - 1] = path;
    }
    @Override
    public void deleteRecord(int index){ // 녹음하고 다시 취소를 눌러 음성파일이 삭제됐을 경우
        multiTextArr[index - 1].setText(null);
        multiTextArr[index - 1].setVisibility(View.VISIBLE); // 다시 돌려놔
        recordPath[index - 1] = null;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void onChooseCalendarButtonPressed(View v){
        DatePickerDialog dialog = new DatePickerDialog(this, dateListener, year,month - 1,day);
        dialog.show();
    }

    public void onBackButtonPressed(View v){
        finish();
    }
    private void setDate(int year, int month, int day){
        this.year = year;
        this.month = month;
        this.day = day;
        dateText.setText(year + "년 " + month + "월 " + day + "일");
    }

    private DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int getYear, int monthOfYear, int dayOfMonth) {
            setDate(getYear,monthOfYear + 1,dayOfMonth);
            Toast.makeText(getApplicationContext(), year + "년" + month + "월" + day +"일을 선택하였습니다.", Toast.LENGTH_SHORT).show();
        }
    };

}




