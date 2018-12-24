package com.example.kimsuhong.lifenote;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SelectAlbumAudioDialog extends AppCompatDialogFragment {
    int[] questionIndexArr = {R.id.questionTextView1,R.id.questionTextView2};
    TextView[] questionTextView = new TextView[questionIndexArr.length];
    int[] buttonPlayIndexArr = {R.id.buttonPlay1,R.id.buttonPlay2};
    Button[] buttonPlay = new Button[buttonPlayIndexArr.length];
    int[] layoutIndexArr = {R.id.q1Layout,R.id.q2Layout};
    LinearLayout[] qLayout = new LinearLayout[layoutIndexArr.length];

    private SelectAlbumAudioDialog.SelectAlbumAudioDialogListener listener;
    private Dialog dialog;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_album_diary_record_dialog,null);

        builder.setView(view)
                .setTitle("선택")
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //취소버튼 눌렀을 시
                        Toast.makeText(getContext(), "취소 버튼을 누르셨어요.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton("완료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //완료버튼 누르면
                    }
                });
        for(int i = 0 ; i < questionIndexArr.length;i++){
            questionTextView[i] = view.findViewById(questionIndexArr[i]);
            buttonPlay[i] = view.findViewById(buttonPlayIndexArr[i]);
            qLayout[i] = view.findViewById(layoutIndexArr[i]);
        }
        for(int i = 0 ; i < buttonPlayIndexArr.length;i++){
            if(getRecordPath(i) == null){// 녹음된게 없으면 GONE으로 보내버림
                qLayout[i].setVisibility(View.GONE);
            }
            else{ // 녹음된게 있으면 질문을 설정한다.
                questionTextView[i].setText(getQuestion(i));
            }
        }
        onClickEvent();
        dialog = builder.create();
        return dialog;
    }
    private void onClickEvent(){ // 버튼 눌렸을 시
        //PlayDialog? 를 실행하고 현재 다이얼로그를 닫는다.
        for(int i = 0 ; i < buttonPlayIndexArr.length;i++){
            final int j = i;
            buttonPlay[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openPlayDialog(getRecordPath(j),getQuestion(j));
                    dialog.dismiss();
                }
            });
        }
    }
    private void openPlayDialog(String audioPath,String question){
        PlayAudioDialog playAudioDialog = PlayAudioDialog.newInstance(audioPath,question);
        playAudioDialog.show(getFragmentManager(),"select audio dialog");
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            listener = (SelectAlbumAudioDialog.SelectAlbumAudioDialogListener)context;
        }
        catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "exception in SelectAlbumAudioDialog 안의 onAttach");
        }
    }
    public static SelectAlbumAudioDialog newInstance(String[] questions, String[] paths){
        SelectAlbumAudioDialog dialog = new SelectAlbumAudioDialog();
        Bundle bundle = dialog.getArguments();
        if(bundle == null)
            bundle = new Bundle();
        for(int i = 0 ; i < questions.length ; i++){
            bundle.putString("question"+(i+1),questions[i]);
        }
        for(int i = 0 ; i < paths.length ; i++){
            bundle.putString("path"+(i+1),paths[i]);
        }
        dialog.setArguments(bundle);
        return dialog;
    }
    private String getQuestion(int idx){
        return getArguments().getString("question"+(idx+1),null);
    }
    private String getRecordPath(int idx){
        return getArguments().getString("path"+(idx+1),null);
    }
    public interface SelectAlbumAudioDialogListener{

    }
}
