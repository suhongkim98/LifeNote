package com.example.kimsuhong.lifenote;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class GguActivity extends AppCompatActivity {
    private static GridView gridView;
    private static SingerDiaryListAdapter diaryListAdapter;
    public static NoteDataBase nDataBase;
    TextView dateText;
    int year, month;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ggu);
        init();
        initAdapterList();


    }
    protected void onStart(){
        openDatabase();
        loadDiaryListData();
        super.onStart();
    }
    public void openDatabase(){
        if(nDataBase != null){
            nDataBase.close();
            nDataBase = null;
        }

        nDataBase = NoteDataBase.getInstance(this);
        boolean isOpen = nDataBase.open();
        if(isOpen){
            Log.d("db",NoteDataBase.DATABASE_NAME + " is open");
        }else{

            Log.d("db",NoteDataBase.DATABASE_NAME + " is not open");
        }
    }

    private void init(){
        dateText = (TextView)findViewById(R.id.textViewDate);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd"); // 현재 시간을 구함
        int year = Integer.parseInt(dateFormat.format(date).substring(0,4));
        int month = Integer.parseInt(dateFormat.format(date).substring(5,7));
        setDate(year,month);

    }
    private void setDate(int year, int month){
        this.year = year;
        this.month = month;
        dateText.setText(year+"년 "+month+"월");
    }

    public void onBackButtonPressed(View v){
        finish();
    }
    public void onPreButtonPressed(View v){
        if(month - 1 > 0)
            month--;
        else {
            year--;
            month = 12;
        }
        setDate(year,month);
        loadDiaryListData();
    }
    public void onNextButtonPressed(View v){
        if(month + 1 < 13)
            month++;
        else {
            year++;
            month = 1;
        }
        setDate(year,month);
        loadDiaryListData();
    }
    public void onAddButtonPressed(View v){
        Intent intent = new Intent(getApplicationContext(),AddDiaryActivity.class);
        startActivity(intent);
    }
    private  void initAdapterList(){
        gridView = (GridView) findViewById(R.id.diaryGrid);
        diaryListAdapter = new SingerDiaryListAdapter();
        //
        //
        gridView.setAdapter(diaryListAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                SingerDiaryDateListItem item = (SingerDiaryDateListItem) diaryListAdapter.getItem(position);
                Intent intent = new Intent(getApplicationContext(), ShowDiaryActivity.class);
                intent.putExtra("question1",item.getQuestions(0));
                intent.putExtra("question2",item.getQuestions(1));
                intent.putExtra("content1",item.getContents(0));
                intent.putExtra("content2",item.getContents(1));
                intent.putExtra("date",item.getDate());
                intent.putExtra("imageID",item.getUri().toString());
                intent.putExtra("idx",item.getIdx());
                intent.putExtra("record1",item.getRecordPath(0));
                intent.putExtra("record2",item.getRecordPath(1));
                startActivity(intent);
            }
        });
    }

    private int loadDiaryListData(){
        String dateFormat = String.format("%4d/%d",year,month);

        String SQL = "select id_PHOTO,question1,content1,question2,content2,selectDate,_idx,id_record1,id_record2 from "+nDataBase.DIARY_TABLE_NAME + " where selectDate like '"+dateFormat+"%'" +
                "order by selectDate";
        int recordCount = -1;
        if(nDataBase != null){
            Cursor outCursor = nDataBase.rawQuery(SQL);

            recordCount = outCursor.getCount();
            Log.d("db","커서 카운트"+recordCount);
            diaryListAdapter.clear();
            Resources res = getResources();
            for (int i = 0; i <recordCount;i++){
                outCursor.moveToNext();
                String record1 = null,record2 = null,id_PHOTO = null;
                if(!outCursor.isNull(0))
                    id_PHOTO = outCursor.getString(0);
                String question1 = outCursor.getString(1);
                String content1 = outCursor.getString(2);
                String question2 = outCursor.getString(3);
                String content2 = outCursor.getString(4);
                String date = outCursor.getString(5);
                String idx = outCursor.getString(6);
                if(!outCursor.isNull(7))
                    record1 = outCursor.getString(7);
                if(!outCursor.isNull(8))
                    record2 = outCursor.getString(8);
                SingerDiaryDateListItem singerDiaryDateListItem = new SingerDiaryDateListItem(idx,date,id_PHOTO,question1,content1,question2,content2);
                singerDiaryDateListItem.setRecordPath(0,record1); singerDiaryDateListItem.setRecordPath(1,record2);
                diaryListAdapter.addItem(singerDiaryDateListItem);
            }
            outCursor.close();
            diaryListAdapter.notifyDataSetChanged();
        }
        return recordCount;
    }

    class SingerDiaryListAdapter extends BaseAdapter {
        ArrayList<SingerDiaryDateListItem> items = new ArrayList<SingerDiaryDateListItem>();
        @Override
        public int getCount(){
            return items.size();
        }
        public void clear(){
            items.clear();
        }
        public void addItem(SingerDiaryDateListItem item){
            items.add(item);
        }
        @Override
        public Object getItem(int position){
            return items.get(position);
        }
        @Override
        public long getItemId(int position){
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup){

            SingerDiaryDateListItemView view = new SingerDiaryDateListItemView(getApplicationContext());
            SingerDiaryDateListItem item = items.get(position);

            view.setDate(item.getDate());
            view.setImage(item.getUri());
            return view;
        }
    }
}
