package com.example.kimsuhong.lifenote;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class FinishListActivity extends AppCompatActivity {
    private static ListView listView;
    private static SingerFinishListAdapter finishListAdapter;
    private static NoteDataBase nDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_list);
        initAdapterList();
        init();
    }

    protected void onStart() {
        openDatabase();
        loadFinishListData();
        super.onStart();
    }
    private  void initAdapterList(){
        listView = (ListView)findViewById(R.id.listView);
        finishListAdapter = new SingerFinishListAdapter();
        //
        //
        listView.setAdapter(finishListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                SingerMyFinishListItem item = (SingerMyFinishListItem)finishListAdapter.getItem(position); // 클릭한 아이템
                //각각의 정보 클릭시 이벤트 코드를 여기에 작성
                Toast.makeText(getApplicationContext(),"선택:"+item.getTitle()+finishListAdapter.getItemId(position),Toast.LENGTH_SHORT).show();

            }
        });
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
    public int loadFinishListData(){
        String SQL = "select title, content from "+nDataBase.FINISHLIST_TABLE_NAME;
        int recordCount = -1;
        if(nDataBase != null){
            Cursor outCursor = nDataBase.rawQuery(SQL);

            recordCount = outCursor.getCount();
            Log.d("db","커서 카운트"+recordCount);
            finishListAdapter.clear();
            Resources res = getResources();
            for (int i = 0; i <recordCount;i++){
                outCursor.moveToNext();
                String title = outCursor.getString(0);
                String content = outCursor.getString(1);
                finishListAdapter.addItem(new SingerMyFinishListItem(title,content));
            }
            outCursor.close();
            finishListAdapter.notifyDataSetChanged();
        }
        return recordCount;
    }

    private void init(){

    }

    public void onBackButtonPressed(View v){
        finish();
    }

    class SingerFinishListAdapter extends BaseAdapter {
        ArrayList<SingerMyFinishListItem> items = new ArrayList<SingerMyFinishListItem>();
        @Override
        public int getCount(){
            return items.size();
        }
        public void clear(){
            items.clear();
        }
        public void addItem(SingerMyFinishListItem item){
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

            SingerMyFinishListItemView view = new SingerMyFinishListItemView(getApplicationContext());
            SingerMyFinishListItem item = items.get(position);

            view.setTitle(item.getTitle());
            return view;
        }
    }
}
