package com.example.kimsuhong.lifenote;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class AlbumAcitivity extends AppCompatActivity implements SelectAlbumAudioDialog.SelectAlbumAudioDialogListener{
    int[] nameTextID = {R.id.nameText,R.id.nameText2,R.id.nameText3,R.id.nameText4};
    TextView[] nameText = new TextView[nameTextID.length];
    ListView bucketListView,finishListView,diaryListView;
    View bucketLayout,finishLayout,diaryLayout;

    private static final int IS_TYPE_BUCKETLIST = 0;
    private static final int IS_TYPE_FINISHLIST = 1;
    private static final int IS_TYPE_DIARY = 2;

    private static SingerAlbumDiaryAdapter albumDiaryAdapter;
    private static SingerAlbumBucketAdapter albumBucketAdapter;
    private static SingerAlbumFinishAdapter albumFinishAdapter;
    public static NoteDataBase nDataBase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        init();
        initAdapterList();
    }
    protected void onStart(){
        openDatabase();

        if(loadDiaryListData() <= 0) // 만약 데이터가 없다면 레이아웃을 GONE으로 해서 안보이게 함
            diaryLayout.setVisibility(View.GONE);
        if(loadBucketListData() <= 0)
            bucketLayout.setVisibility(View.GONE);
        if(loadFinishListData() <= 0)
            finishLayout.setVisibility(View.GONE);


        setListViewHeightBasedOnChildren(bucketListView);
        setListViewHeightBasedOnChildren(finishListView);
        setListViewHeightBasedOnChildren(diaryListView);
        super.onStart();
    }
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) return;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0) view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LinearLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
            if(i == listAdapter.getCount() - 1) totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));

        listView.setLayoutParams(params);
        listView.requestLayout();
    }
    private void init(){
        for (int i = 0 ; i < nameTextID.length;i++){
            nameText[i] = (TextView) findViewById(nameTextID[i]);
            nameText[i].setText(Setting.name);
        }
        bucketListView = (ListView)findViewById(R.id.bucketListView);
        finishListView = (ListView)findViewById(R.id.bucketFinishListView);
        diaryListView = (ListView)findViewById(R.id.diaryListView);

        bucketLayout = (View)findViewById(R.id.bucketListLayout);
        finishLayout = (View)findViewById(R.id.bucketFinishListLayout);
        diaryLayout = (View)findViewById(R.id.diaryLayout);
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
    private  void initAdapterList(){
        albumDiaryAdapter = new SingerAlbumDiaryAdapter();
        albumBucketAdapter = new SingerAlbumBucketAdapter();
        albumFinishAdapter = new SingerAlbumFinishAdapter();
        //
        //
        bucketListView.setAdapter(albumBucketAdapter);
        diaryListView.setAdapter(albumDiaryAdapter);
        finishListView.setAdapter(albumFinishAdapter);

        diaryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //리스트뷰에서 각각의 아이템이 눌렸다면
                //만약 오디오 파일이 있을경우
                //오디오 선택 다이얼로그를 띄운다.
                SingerAlbumItem item = (SingerAlbumItem)albumDiaryAdapter.getItem(position);
                if(item.isHaveAudio()){
                    String[] questions = {
                            item.getQuestions(0),
                            item.getQuestions(1)
                    };
                    String[] paths = {
                            item.getRecord(0),
                            item.getRecord(1)
                    };
                    openSelectAudioDialog(questions,paths);
                }
            }
        });
    }
    private void openSelectAudioDialog(String[] questions, String[] paths){
        SelectAlbumAudioDialog selectAlbumAudioDialog = SelectAlbumAudioDialog.newInstance(questions,paths);
        selectAlbumAudioDialog.show(getSupportFragmentManager(),"select audio dialog");
    }
    private int loadDiaryListData(){
        String SQL = "select id_PHOTO,question1,content1,question2,content2,selectDate,id_record1,id_record2 from "+nDataBase.DIARY_TABLE_NAME +" order by selectDate";
        int recordCount = -1;
        if(nDataBase != null){
            Cursor outCursor = nDataBase.rawQuery(SQL);

            recordCount = outCursor.getCount();
            Log.d("db","커서 카운트"+recordCount);
            albumDiaryAdapter.clear();
            Resources res = getResources();
            for (int i = 0; i <recordCount;i++){
                outCursor.moveToNext();
                String id_PHOTO = outCursor.getString(0);
                String question1 = outCursor.getString(1);
                String content1 = outCursor.getString(2);
                String question2 = outCursor.getString(3);
                String content2 = outCursor.getString(4);
                String date = outCursor.getString(5);
                String record1 = null, record2 = null;
                if(!outCursor.getString(6).equals("null"))
                    record1 = outCursor.getString(6);
                if(!outCursor.getString(7).equals("null"))
                    record2 = outCursor.getString(7);
                albumDiaryAdapter.addItem(new SingerAlbumItem(IS_TYPE_DIARY,date, Uri.parse(id_PHOTO),question1,content1,question2,content2,record1,record2));
            }
            outCursor.close();
            albumDiaryAdapter.notifyDataSetChanged();
        }
        return recordCount;
    }
    private int loadBucketListData(){
        String SQL = "select receiveDate,title,content from "+nDataBase.BUCKETLIST_TABLE_NAME+" order by receiveDate";
        int recordCount = -1;
        if(nDataBase != null){
            Cursor outCursor = nDataBase.rawQuery(SQL);

            recordCount = outCursor.getCount();
            Log.d("db","커서 카운트"+recordCount);
            albumBucketAdapter.clear();
            Resources res = getResources();
            for (int i = 0; i <recordCount;i++){
                outCursor.moveToNext();
                String date = outCursor.getString(0);
                String title = outCursor.getString(1);
                String content = outCursor.getString(2);
                albumBucketAdapter.addItem(new SingerAlbumItem(IS_TYPE_BUCKETLIST,date,title,content));
            }
            outCursor.close();
            albumBucketAdapter.notifyDataSetChanged();
        }
        return recordCount;
    }
    private int loadFinishListData(){
        String SQL = "select finishDate,title,content from "+nDataBase.FINISHLIST_TABLE_NAME+ " order by finishDate";
        int recordCount = -1;
        if(nDataBase != null){
            Cursor outCursor = nDataBase.rawQuery(SQL);

            recordCount = outCursor.getCount();
            Log.d("db","커서 카운트"+recordCount);
            albumFinishAdapter.clear();
            Resources res = getResources();
            for (int i = 0; i <recordCount;i++){
                outCursor.moveToNext();
                String date = outCursor.getString(0);
                String title = outCursor.getString(1);
                String content = outCursor.getString(2);
                albumFinishAdapter.addItem(new SingerAlbumItem(IS_TYPE_FINISHLIST,date,title,content));
            }
            outCursor.close();
            albumFinishAdapter.notifyDataSetChanged();
        }
        return recordCount;
    }
    public void onBackButtonPressed(View v){
        finish();
    }

    class SingerAlbumDiaryAdapter extends BaseAdapter {
        ArrayList<SingerAlbumItem> items = new ArrayList<SingerAlbumItem>();


        @Override
        public int getCount(){
            return items.size();
        }
        public void clear(){
            items.clear();
        }
        public void addItem(SingerAlbumItem item){
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
            SingerAlbumItem item = items.get(position);
            SingerAlbumItemView view = new SingerAlbumItemView(getApplicationContext(),item.getType());

            view.setDate(item.getDate());
            view.setImage(item.getPhotoUri());
            for(int i = 0 ; i < view.questionCount();i++){ // 질문 답변 SET
                view.setQuestions(item.getQuestions(i),i);
                view.setAnswers(item.getContents(i),i);
            }
            return view;
        }
    }
    class SingerAlbumBucketAdapter extends BaseAdapter {
        ArrayList<SingerAlbumItem> items = new ArrayList<SingerAlbumItem>();


        @Override
        public int getCount(){
            return items.size();
        }
        public void clear(){
            items.clear();
        }
        public void addItem(SingerAlbumItem item){
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
            SingerAlbumItem item = items.get(position);
            SingerAlbumItemView view = new SingerAlbumItemView(getApplicationContext(),item.getType());

            view.setDate(item.getDate());
            view.setBucketTitle(item.getTitle());
            view.setBucketContent(item.getBucketContent());
            return view;
        }
    }
    class SingerAlbumFinishAdapter extends BaseAdapter {
        ArrayList<SingerAlbumItem> items = new ArrayList<SingerAlbumItem>();

        @Override
        public int getCount(){
            return items.size();
        }
        public void clear(){
            items.clear();
        }
        public void addItem(SingerAlbumItem item){
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
            SingerAlbumItem item = items.get(position);
            SingerAlbumItemView view = new SingerAlbumItemView(getApplicationContext(),item.getType());
            view.setDate(item.getDate());
            view.setBucketTitle(item.getTitle());
            view.setBucketContent(item.getBucketContent());
            return view;
        }
    }
}
