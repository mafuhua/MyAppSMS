package com.hellomc.myappsms;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashSet;

public class FolderDetailActivity extends AppCompatActivity {
    private final int index_address = 1;
    private final int index_date = 2;
    private final int index_body = 3;
    private ListView mListView;
    private Context ctx;
    private MyAdapter adapter;
    private Uri uri;
    private String[] projection = new String[]{
            "_id",
            "address",
            "date",
            "body"
    };
    private HashSet<Integer> setTitleSet;

    private void assignViews() {
        mListView = (ListView) findViewById(R.id.listView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_detail);
        assignViews();
        uri = getIntent().getData();
        if (uri == null) {
            throw new RuntimeException("没有数据");
        }
        ctx = this;
        adapter = new MyAdapter(ctx, null);
        mListView.setAdapter(adapter);
        fillData();
        setTitleSet = new HashSet<>();

    }

    private void fillData() {
        MyQueryHandler queryHandler = new MyQueryHandler(getContentResolver());
        queryHandler.startQuery(88, adapter, uri, projection, null, null, "date desc");
        queryHandler.setOnQueryCursorCompliteListener(new MyQueryHandler.OnQueryCursorCompliteListener() {
            @Override
            public void onQueryCursorComplite(int token, Object cookie, Cursor cursor) {
                cursor.moveToPosition(-1);
                long lastDay = 0;
                long currDay = 0;
                while (cursor.moveToNext()) {

                    currDay = cursor.getLong(index_date);

                    if (!isSameDay(lastDay, currDay)) {
                        setTitleSet.add(cursor.getPosition());
                    }
                    lastDay = currDay;

                }

            }
        });
    }

    private boolean isSameDay(long lastDay, long currDay) {
        Time time = new Time();
        time.set(lastDay);

        int thenYear = time.year;
        int thenMonth = time.month;
        int thenMonthDay = time.monthDay;

        time.set(currDay);
        return (thenYear == time.year)
                && (thenMonth == time.month)
                && (thenMonthDay == time.monthDay);

    }

    public void newMessage(View view) {
        Intent intent = new Intent(ctx, NewMsgActivity.class);
        startActivity(intent);
        finish();
    }

    private class MyAdapter extends CursorAdapter {
        public MyAdapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.list_item_folder_detail, null);
            ViewHolder vh = new ViewHolder();
            vh.tvSubTitle = (TextView) view.findViewById(R.id.tv_sub_title);
            vh.ivIconListItem = (ImageView) view.findViewById(R.id.iv_icon_list_item);
            vh.tvAddressListItem = (TextView) view.findViewById(R.id.tv_address_list_item);
            TextView tvBodyListItem = (TextView) view.findViewById(R.id.tv_body_list_item);
            vh.tvDateListItem = (TextView) view.findViewById(R.id.tv_date_list_item);
            vh.tvBodyListItem = tvBodyListItem;
            view.setTag(vh);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder vh = (ViewHolder) view.getTag();
            CharSequence body = cursor.getString(index_body);
            vh.tvBodyListItem.setText(body);
            long date = cursor.getLong(index_date);
            String dateStr = DateFormat.getDateFormat(ctx).format(date);
            vh.tvDateListItem.setText(dateStr);
            String address = cursor.getString(index_address);
            String name = MyUtils.getNameByAddress(ctx, address);
            if (name != null) {
                vh.tvAddressListItem.setText(name);
            } else {
                vh.tvAddressListItem.setText(address);
            }
            String contactId = MyUtils.getContactIdByAddress(ctx, address);
            Bitmap face = MyUtils.getFaceByContactId(ctx, contactId);
            if (face == null) {
                vh.ivIconListItem.setBackgroundResource(R.drawable.ic_contact_picture);
            } else {
                vh.ivIconListItem.setBackgroundDrawable(new BitmapDrawable(face));
            }
            if (setTitleSet.contains(cursor.getPosition())) {
                vh.tvSubTitle.setVisibility(View.VISIBLE);
                vh.tvSubTitle.setText(dateStr);
            } else {
                vh.tvSubTitle.setVisibility(View.GONE);
            }
        }
    }

    public class ViewHolder {
        public TextView tvSubTitle;
        public ImageView ivIconListItem;
        public TextView tvAddressListItem;
        public TextView tvBodyListItem;
        public TextView tvDateListItem;
    }
}
