package com.hellomc.myappsms;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SearchableActivity extends ListActivity {
    private final int index_address = 1;
    private final int index_date = 2;
    private final int index_body = 3;
    public Context ctx;
    private MyAdapter adapter;
    private ListView listView;
    private String[] projection = new String[]{
            "_id",
            "address",
            "date",
            "body"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        ctx = this;
        listView = getListView();
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
        adapter = new MyAdapter(ctx, null);
        listView.setAdapter(adapter);
    }

    private void doMySearch(String query) {
        MyQueryHandler queryHandler = new MyQueryHandler(getContentResolver());
        queryHandler.startQuery(88, adapter, MyContants.URI_SMS, projection, " body like '%" + query + "%'", null, "date desc");
    }

    private class MyAdapter extends CursorAdapter {
        public MyAdapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.list_item_folder_detail, null);
            ViewHolder vh = new ViewHolder();
            TextView subtitle = (TextView) view.findViewById(R.id.tv_sub_title);
            subtitle.setVisibility(View.GONE);
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
