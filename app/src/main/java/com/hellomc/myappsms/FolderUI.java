package com.hellomc.myappsms;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FolderUI extends ListActivity {
    private ListView listView;

    private String[] names = new String[]{"收件箱", "发件箱", "草稿箱", "已发送"};

    private int[] imageIds = new int[]{R.drawable.a_f_inbox, R.drawable.a_f_outbox,
            R.drawable.a_f_draft, R.drawable.a_f_sent};
    private int[] counts = new int[4];
    private MyAdapter adapter;
    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_folder_ui);
        ctx = this;
        listView = getListView();
        adapter = new MyAdapter();
        listView.setAdapter(adapter);
        fillData();
        rigeListener();
    }

    private void rigeListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (counts[position]>0){
                    Intent intent = new Intent(ctx,FolderDetailActivity.class);
                    intent.setData(getUri(position));
                    startActivity(intent);
                }else {
                    Toast.makeText(ctx, "还没有内容那", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void fillData() {
        MyQueryHandler queryHandler = new MyQueryHandler(getContentResolver());
        for (int i = 0; i < counts.length; i++) {
            queryHandler.startQuery(i,null,getUri(i),new String[]{"count(*)"},null,null,null);

        }
        queryHandler.setOnQueryCursorCompliteListener(new MyQueryHandler.OnQueryCursorCompliteListener() {
            @Override
            public void onQueryCursorComplite(int token, Object cookie, Cursor cursor) {
                cursor.moveToFirst();
                int count = cursor.getInt(0);
                counts[token] = count;
                switch (token){
                    case 0:
                        counts[0] = count;
                        break;
                    default:
                        break;
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private Uri getUri(int i) {
        switch (i) {
            case 0:
                return MyContants.URI_INBOX;
            case 1:
                return MyContants.URI_OUTBOX;
            case 2:
                return MyContants.URI_DRAFT;
            case 3:
                return MyContants.URI_SENT;
        }
        return null;
    }

    protected class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = getLayoutInflater().inflate(R.layout.list_item_folder, null);
            } else {
                view = convertView;
            }
            ImageView icon = (ImageView) view.findViewById(R.id.iv_icon_list_item);
            TextView name = (TextView) view.findViewById(R.id.tv_name_list_item);
            TextView count = (TextView) view.findViewById(R.id.tv_count_list_item);
            icon.setBackgroundResource(imageIds[position]);
            name.setText(names[position]);
            count.setText(counts[position]+"");
            if (position%2==0){
                view.setBackgroundColor(Color.GRAY);
            }else{
                view.setBackgroundColor(Color.WHITE);
            }
            return view;
        }
    }

}
