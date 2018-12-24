package com.example.kimsuhong.lifenote;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AlertDialog;
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

public class BuketActivity extends AppCompatActivity {
    private static ListView listViewBuket;
    private static SingerMyBuketlistAdapter buketlistAdapter;
    public static NoteDataBase nDataBase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buket);
        initAdapterList();
        ini();
    }

    protected void onStart() {
        openDatabase();
        loadBucketListData();
        super.onStart();
    }

    private void ini(){
    }

    private  void initAdapterList(){
        listViewBuket = (ListView)findViewById(R.id.listViewBuket);
        buketlistAdapter = new SingerMyBuketlistAdapter();
        //
        //
        listViewBuket.setAdapter(buketlistAdapter);

        listViewBuket.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                SingerMyBuketlistItem item = (SingerMyBuketlistItem)buketlistAdapter.getItem(position);
                Toast.makeText(getApplicationContext(),"선택:"+item.getTitle()+buketlistAdapter.getItemId(position),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), BuketContentActivity.class);
                intent.putExtra("buckettitle",item.getTitle());
                intent.putExtra("bucketcontent",item.getContent());
                intent.putExtra("idx",item.getIdx());
                startActivity(intent);
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
    public int loadBucketListData(){
        String SQL = "select title, content,_idx from "+nDataBase.BUCKETLIST_TABLE_NAME;
        int recordCount = -1;
        if(nDataBase != null){
            Cursor outCursor = nDataBase.rawQuery(SQL);

            recordCount = outCursor.getCount();
            Log.d("db","커서 카운트"+recordCount);
            buketlistAdapter.clear();
            Resources res = getResources();
            for (int i = 0; i <recordCount;i++){
                outCursor.moveToNext();
                String title = outCursor.getString(0);
                String content = outCursor.getString(1);
                String idx = outCursor.getString(2);
                buketlistAdapter.addItem(new SingerMyBuketlistItem(idx,title,content));
            }
            outCursor.close();
            buketlistAdapter.notifyDataSetChanged();
        }
        return recordCount;
    }

    public void onFinishListButtonPressed(View v){
        Intent intent = new Intent(getApplicationContext(), FinishListActivity.class);
        startActivity(intent);
    }
    public void onBackButtonPressed(View v){
        finish();
    }

    public void onAddButtonPressed(View v){
        Intent intent = new Intent(getApplicationContext(),BuketAddActivity.class);
        startActivity(intent);
    }

    class SingerMyBuketlistAdapter extends BaseAdapter {
        ArrayList<SingerMyBuketlistItem> items = new ArrayList<SingerMyBuketlistItem>();
        @Override
        public int getCount(){
            return items.size();
        }
        public void clear(){
            items.clear();
        }
        public void addItem(SingerMyBuketlistItem item){
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

            SingerMyBuketlistItemView view = new SingerMyBuketlistItemView(getApplicationContext());
            SingerMyBuketlistItem item = items.get(position);
            view.setTitle(item.getTitle());
            return view;
        }
    }
}
