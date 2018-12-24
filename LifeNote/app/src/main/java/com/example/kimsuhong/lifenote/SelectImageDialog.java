package com.example.kimsuhong.lifenote;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Debug;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

public class SelectImageDialog extends AppCompatDialogFragment {
    RadioGroup radioGroup;
    private SelectImageDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_selectimage,null);

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
                        //저장버튼 눌렀을 시
                        int radioButtonID = radioGroup.getCheckedRadioButtonId();
                        View radioButton = radioGroup.findViewById(radioButtonID); // 체크된 버튼의 인덱스를 구한다.
                        int idx = radioGroup.indexOfChild(radioButton);

                        listener.applySelectRadioResult(idx);
                    }
                });
        radioGroup = view.findViewById(R.id.selectImageGroup);
        return builder.create();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            listener = (SelectImageDialogListener)context;
        }
        catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "exception in SelectImageDialog 안의 onAttach");
        }
    }

    public interface SelectImageDialogListener{
        void applySelectRadioResult(int checkedIndex);
    }
}
