package com.example.kimsuhong.lifenote;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class PlayAudioDialog extends AppCompatDialogFragment {
    Button playButton;
    SeekBar playBar;
    TextView maxPointText, questionText;
    RecordManager recordManager;
    Dialog dialog;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_audio_play,null);

        builder.setView(view)
                .setTitle("음성 재생을 합니다")
                .setCancelable(false)
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stopAudio();
                    }
                })
                .setPositiveButton("완료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //저장버튼 눌렀을 시
                        stopAudio();
                    }
                });
        playButton = view.findViewById(R.id.buttonPlay);
        playBar = view.findViewById(R.id.playBar);
        maxPointText = view.findViewById(R.id.maxPoint);
        recordManager  = new RecordManager(playBar,playButton,maxPointText); // 재생용 생성자
        questionText = view.findViewById(R.id.questionTextView);
        questionText.setText(getQuestion());
        onClickEvent();
        dialog = builder.create();
        dialog.setOnKeyListener(new Dialog.OnKeyListener(){
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    stopAudio();
                    dialog.dismiss();
                }
                return true;
            }
        });
        return dialog;
    }
    private boolean stopAudio(){ // 음악끄는거
        if(recordManager.mPlayerState == recordManager.PLAY_STOP) // 이미 멈춰있으면
            return false; // false주고 함수 종료
        recordManager.mBtnPlayOnClick();
        Toast.makeText(getContext(), "음성 재생을 중지합니다.", Toast.LENGTH_LONG).show();
        return true;
    }
    public void onClickEvent(){
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 클릭 시
                String fileName = getRecordPath().substring(getRecordPath().lastIndexOf(File.separator)); // 파일 이름을 가져온다
                recordManager.setFileName(fileName);
                recordManager.mBtnPlayOnClick();
            }
        });
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
    public static PlayAudioDialog newInstance(String recordPath, String question){
        PlayAudioDialog dialog = new PlayAudioDialog();
        Bundle bundle = dialog.getArguments();
        if(bundle == null)
            bundle = new Bundle();

        bundle.putString("recordPlayingPath",recordPath);
        bundle.putString("question",question);
        dialog.setArguments(bundle);
        return dialog;
    }
    public String getRecordPath(){
        return getArguments().getString("recordPlayingPath",null);
    }
    public String getQuestion(){
        return getArguments().getString("question",null);
    }
}
