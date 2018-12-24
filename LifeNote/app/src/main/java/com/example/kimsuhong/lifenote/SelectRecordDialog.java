package com.example.kimsuhong.lifenote;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class SelectRecordDialog extends AppCompatDialogFragment {
    Button recordButton,playButton,resetButton;
    SeekBar recBar, playBar;
    TextView maxPointText, questionText;
    LinearLayout playLayout, recordLayout;
    private SelectRecordDialogListener listener;
    public RecordManager recordManager;
    Dialog dialog;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_record_audio,null);

        builder.setView(view)
                .setTitle("음성 녹음을 해주세요")
                .setCancelable(false)
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //취소버튼 눌렀을 시
                        if(recordManager.mRecState == recordManager.RECORDING)
                            recordManager.mBtnRecOnClick(); // 녹음 중이었다면 녹음 종료
                        File f = new File(recordManager.getFilePath());
                        if(f.delete()){
                            Toast.makeText(getContext(), "취소 버튼을 누르셨어요. 녹음된 파일을 삭제 성공했습니다", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(getContext(), "취소 버튼을 누르셨어요. 녹음된 파일을 삭제 실패했습니다.", Toast.LENGTH_LONG).show();
                        }
                        listener.deleteRecord(getRecordIndex());
                    }
                })
                .setPositiveButton("음성 저장", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //저장버튼 눌렀을 시
                        if(recordManager.mRecState == recordManager.RECORDING){
                            //녹음 중이라면
                            recordManager.mBtnRecOnClick();
                            //녹음 중지하고
                        }
                        if(recordManager.isRecordSuccess) // 음성 녹화에 성공한다면
                            listener.saveRecord(getRecordIndex(),recordManager.getFilePath()); // 액티비티 변수에 저장
                    }
                });
        recordButton = view.findViewById(R.id.buttonRecord);
        playButton = view.findViewById(R.id.buttonPlay);
        resetButton = view.findViewById(R.id.buttonReset);
        recordLayout = view.findViewById(R.id.recordLayout);
        playLayout = view.findViewById(R.id.playLayout);
        recBar = view.findViewById(R.id.recordBar);
        playBar = view.findViewById(R.id.playBar);
        maxPointText = view.findViewById(R.id.maxPoint);
        questionText = view.findViewById(R.id.questionTextView);
        questionText.setText(getQuestion());
        recordManager = new RecordManager(recBar,playBar,recordButton,playButton,maxPointText);
        onClickEvent();
        if(getPlayingRecordPath() != null){ //재생모드라면
            recordLayout.setVisibility(View.GONE);
            String fileName = getPlayingRecordPath().substring(getPlayingRecordPath().lastIndexOf(File.separator)); // 파일 이름을 가져온다
            recordManager.setFileName(fileName);
        }
        else{
            playLayout.setVisibility(View.GONE); // 처음엔 안보이게
        }


        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new Dialog.OnKeyListener(){
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if(recordManager.mRecState == recordManager.RECORDING)
                        recordManager.mBtnRecOnClick(); // 녹음 중이었다면 녹음 종료
                    File f = new File(recordManager.getFilePath());
                    if(f.delete()){
                        Toast.makeText(getContext(), "취소 버튼을 누르셨어요. 녹음된 파일을 삭제 성공했습니다", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(getContext(), "취소 버튼을 누르셨어요. 녹음된 파일을 삭제 실패했습니다.", Toast.LENGTH_LONG).show();
                    }
                    listener.deleteRecord(getRecordIndex());
                    dialog.dismiss();
                }
                return true;
            }
        });
        return dialog;
    }

    private void onClickEvent(){
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordManager.mBtnRecOnClick();
                if(recordManager.mRecState == recordManager.REC_STOP){
                    playLayout.setVisibility(View.VISIBLE);
                    recordLayout.setVisibility(View.GONE);
                }
            }
        });
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"재생합니다.",Toast.LENGTH_SHORT).show();
                recordManager.mBtnPlayOnClick();
            }
        });
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(recordManager.mPlayerState == recordManager.PLAYING){ // 재생 중이면 재생 중지
                    recordManager.mBtnPlayOnClick();
                }
                File f = new File(recordManager.getFilePath());
                if(f.delete()){
                    Toast.makeText(getContext(), "기존 음성파일을 삭제하고 다시 녹음합니다.", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getContext(), "기존 음성파일을 삭제하는데 실패했습니다.", Toast.LENGTH_LONG).show();
                }
                recordManager.setFileName(null);
                playLayout.setVisibility(View.GONE);
                recordLayout.setVisibility(View.VISIBLE);
            }
        });
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            listener = (SelectRecordDialogListener)context;
        }
        catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "exception in SelectRecordDialog 안의 onAttach");
        }
    }

    @Override
    public void onPause(){ // onPaues?
        super.onPause();
        try {
            if (recordManager != null) {
                if (recordManager.mRecState == recordManager.RECORDING) { // 녹화 중이라면
                    //녹화를 중지하고 해당 데이터 삭제
                    recordManager.mBtnRecOnClick(); // 녹화중지
                    String path = recordManager.getFilePath();
                    File f = new File(path);
                    if (f.delete()) { //삭제
                        Log.d("record", path + "삭제");
                    } else {
                        Log.d("record", path + "삭제 실패");
                    }
                    Toast.makeText(getActivity(),"녹음을 중지합니다.",Toast.LENGTH_LONG).show();
                }
                if (recordManager.mPlayerState == recordManager.PLAYING) { // 재생 중이라면
                    //재생을 중지한다.
                    recordManager.mBtnPlayOnClick(); // 재생 중지
                    Toast.makeText(getActivity(),"재생을 중지합니다.",Toast.LENGTH_LONG).show();
                }
            }
            dialog.dismiss(); // 다이얼로그 끄기
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public static SelectRecordDialog newInstance(int index,String question){
        SelectRecordDialog dialog = new SelectRecordDialog();
        Bundle bundle = dialog.getArguments();
        if(bundle == null)
            bundle = new Bundle();

        bundle.putInt("recordIndex",index);
        bundle.putString("question",question);
        dialog.setArguments(bundle);
        return dialog;
    }
    public static SelectRecordDialog newInstance(int index, String recordPath,String question){
        SelectRecordDialog dialog = new SelectRecordDialog();
        Bundle bundle = dialog.getArguments();
        if(bundle == null)
            bundle = new Bundle();

        bundle.putInt("recordIndex",index);
        bundle.putString("recordPlayingPath",recordPath);
        bundle.putString("question",question);
        dialog.setArguments(bundle);
        return dialog;
    }
    public int getRecordIndex(){
        return getArguments().getInt("recordIndex",0);
    }
    public String getPlayingRecordPath(){
        return getArguments().getString("recordPlayingPath",null);
    }
    public String getQuestion(){
        return getArguments().getString("question",null);
    }
    protected interface SelectRecordDialogListener{
        void saveRecord(int index,String path); // index 1부터 시작
        void deleteRecord(int index); // 음성을 INVISIBLE했으니 다시 켜주는 함수
    }
}
