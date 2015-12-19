package com.hellomc.myappsms;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashSet;


public class ConversationUI extends AppCompatActivity implements View.OnClickListener {
    private final int ID_SEARCH = 100;
    private final int ID_EDIT = 101;
    private final int ID_CANCEL_EDIT = 1010;
    Uri uri = Uri.parse("content://sms/conversations");
    private Button mBtnNewMsg;
    private Button mBtnSelectAll;
    private Button mBtnSelectNull;
    private ListView mListView;
    private Button mBtnDeleteMsg;
    private Context ctx;
    private boolean isEdit;
    private HashSet<String> selectItemSet;
    private String[] projection = new String[]{
            "sms.body AS snippet",
            " sms.thread_id AS _id ",
            " groups.msg_count AS msg_count ",
            " address  as address",
            " date as date"};
    private MyAdapter adapter;
    private int index_body = 0;
    private int index_id = 1;
    private int index_msg_count = 2;
    private int index_address = 3;
    private int index_date = 4;
    private ProgressDialog progressDialog;
    private boolean isDeleteCancle = false;
    private GroupDao groupDao;

    private void assignViews() {
        mBtnNewMsg = (Button) findViewById(R.id.btn_new_msg);
        mBtnSelectAll = (Button) findViewById(R.id.btn_select_all);
        mBtnSelectNull = (Button) findViewById(R.id.btn_select_null);
        mListView = (ListView) findViewById(R.id.listView);
        mBtnDeleteMsg = (Button) findViewById(R.id.btn_delete_msg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        groupDao = GroupDao.getInstance(this);
        setContentView(R.layout.activity_conversation_ui);
        assignViews();
        mBtnNewMsg.setOnClickListener(this);
        mBtnSelectAll.setOnClickListener(this);
        mBtnSelectNull.setOnClickListener(this);
        mBtnDeleteMsg.setOnClickListener(this);
        isEdit = false;
        selectItemSet = new HashSet<String>();
        adapter = new MyAdapter(this, null);

        mListView.setAdapter(adapter);
        fillData();
        regListener();
        flushState();
        isEdit = false;
        flushState();

    }

    private void regListener() {

        //  设置条目的点击事件
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            /**
             * position 点击的条目的下标
             */
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Cursor cursor = adapter.getCursor();
                cursor.moveToPosition(position);
                String address = cursor.getString(index_address);
                if (isEdit) {
                    if (selectItemSet.contains(address)) {
                        selectItemSet.remove(address);
                    } else {
                        selectItemSet.add(address);
                    }
                    adapter.notifyDataSetChanged();
                    flushState();
                } else {
                    Intent intent = new Intent(ConversationUI.this, ConversationDetailActivity.class);
                    intent.putExtra("address", address);
                    startActivity(intent);
                }

            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showSelectGroupDialog(position);
                return true;
            }
        });
    }

    private void showSelectGroupDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("请选择要加入的群");
        Cursor groupCursor = groupDao.getAllGroups();
        String[] items = new String[groupCursor.getCount()];
        final int[] groudIds = new int[groupCursor.getCount()];
        while (groupCursor.moveToNext()) {
            items[groupCursor.getPosition()] = groupCursor.getString(1);
            groudIds[groupCursor.getPosition()] = groupCursor.getInt(0);
        }
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Cursor cursor = adapter.getCursor();
                cursor.moveToPosition(position);
                int threadId = cursor.getInt(index_id);
                int groudId = groudIds[which];
                groupDao.addThread2Group(threadId, groudId);
            }
        });
        builder.show();


    }

    private void fillData() {
        MyQueryHandler queryHandler = new MyQueryHandler(getContentResolver());
        queryHandler.startQuery(88, adapter, uri, projection, null, null, " date desc ");
    }

    private void flushState() {
        if (isEdit) {
            mBtnNewMsg.setVisibility(View.GONE);
            mBtnSelectNull.setVisibility(View.VISIBLE);
            mBtnDeleteMsg.setVisibility(View.VISIBLE);
            mBtnSelectAll.setVisibility(View.VISIBLE);
        } else {
            mBtnNewMsg.setVisibility(View.VISIBLE);
            mBtnSelectNull.setVisibility(View.GONE);
            mBtnDeleteMsg.setVisibility(View.GONE);
            mBtnSelectAll.setVisibility(View.GONE);
        }
        if (selectItemSet.size() == adapter.getCount()) {
            mBtnSelectAll.setEnabled(false);
        } else {
            mBtnSelectAll.setEnabled(true);
        }
        if (selectItemSet.size() == 0) {
            mBtnSelectNull.setEnabled(false);
        } else {
            mBtnSelectNull.setEnabled(true);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 添加三个菜单条目

        menu.add(0, ID_SEARCH, 0, "搜索");
        menu.add(0, ID_EDIT, 0, "编辑");
        menu.add(0, ID_CANCEL_EDIT, 0, "取消编辑");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case ID_SEARCH:
                onSearchRequested();
                break;
            case ID_CANCEL_EDIT:
                isEdit = false;
                flushState();
                break;
            case ID_EDIT:
                isEdit = true;
                Log.d("MyAdapter", "isEdit4:" + isEdit);

                flushState();
                break;

        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isEdit) {
            menu.findItem(ID_CANCEL_EDIT).setVisible(true);
            menu.findItem(ID_EDIT).setVisible(false);
            menu.findItem(ID_SEARCH).setVisible(false);
        } else {
            menu.findItem(ID_CANCEL_EDIT).setVisible(false);
            menu.findItem(ID_EDIT).setVisible(true);
            menu.findItem(ID_SEARCH).setVisible(true);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_new_msg:
                Intent intent = new Intent(ctx, NewMsgActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_delete_msg:
                showConfigDeleteDialog();
                break;
            case R.id.btn_select_all:
                Cursor cursor = adapter.getCursor();
                cursor.moveToPosition(-1);
                while (cursor.moveToNext()) {
                    String address = cursor.getString(index_address);
                    selectItemSet.add(address);
                }
                adapter.notifyDataSetChanged();
                flushState();
                break;
            case R.id.btn_select_null:
                selectItemSet.clear();
                adapter.notifyDataSetChanged();
                flushState();
                break;


        }

    }

    private void showConfigDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("要删除吗>");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showDeleteProgress();
                isDeleteCancle = false;
                DeleteMsgThread deleteMsgThread = new DeleteMsgThread();
                deleteMsgThread.start();
            }
        });
        builder.show();
    }

    private void showDeleteProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("正在删除");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(selectItemSet.size());
        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isEdit = false;
                flushState();
            }
        });
        progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isDeleteCancle = true;
            }
        });
        progressDialog.show();
    }

    private class MyAdapter extends CursorAdapter {

        public MyAdapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.list_item_conversation, null);
            ViewHolder vh = new ViewHolder();

            // 找到子view
            ImageView icon = (ImageView) view.findViewById(R.id.iv_icon_list_item);
            TextView body = (TextView) view.findViewById(R.id.tv_body_list_item);
            TextView address = (TextView) view.findViewById(R.id.tv_address_list_item);
            TextView date = (TextView) view.findViewById(R.id.tv_date_list_item);
            ImageView ivCheck = (ImageView) view.findViewById(R.id.iv_check_list_item);
            ivCheck.setEnabled(false); // 默认不选中
            // 打包
            vh.icon = icon;
            vh.body = body;
            vh.address = address;
            vh.date = date;
            vh.ivCheck = ivCheck;

            // 背在view的身上
            view.setTag(vh);
            return view;
        }

        @Override
        public void bindView(android.view.View view, Context context, Cursor cursor) {
            ViewHolder vh = (ViewHolder) view.getTag();
            CharSequence bodyStr = cursor.getString(index_body);
            vh.body.setText(bodyStr);
            long dateLong = cursor.getLong(index_date);
            String dateStr;
            if (DateUtils.isToday(dateLong)) {
                dateStr = DateFormat.getTimeFormat(ctx).format(dateLong);

            } else {
                dateStr = DateFormat.getDateFormat(ctx).format(dateLong);

            }
            String msgCount = cursor.getString(index_msg_count);

            vh.date.setText(dateStr);
            String address = cursor.getString(index_address);
            vh.address.setText(address);
            String name = MyUtils.getNameByAddress(ctx, address);
            if (name != null) {
                vh.address.setText(name + "(" + msgCount + ")");

            } else {
                vh.address.setText(address + "(" + msgCount + ")");

            }
            String contactId = MyUtils.getContactIdByAddress(ctx, address);
            if (contactId == null) {
                vh.icon.setBackgroundResource(R.drawable.ic_unknow_contact_picture);
            } else {
                Bitmap face = MyUtils.getFaceByContactId(ctx, contactId);
                if (face == null) {
                    vh.icon.setBackgroundResource(R.drawable.ic_contact_picture);
                } else {
                    vh.icon.setBackgroundDrawable(new BitmapDrawable(face));
                }
            }
            if (isEdit) {
                vh.ivCheck.setVisibility(View.VISIBLE);
                if (selectItemSet.contains(address)) {
                    vh.ivCheck.setEnabled(true);
                } else {
                    vh.ivCheck.setEnabled(false);
                }
            } else {
                vh.ivCheck.setVisibility(View.GONE);
            }
        }
    }

    protected class DeleteMsgThread extends Thread {
        @Override
        public void run() {
            super.run();
            for (String msg : selectItemSet) {
                if (isDeleteCancle) {
                    break;
                }
                getContentResolver().delete(MyContants.URI_SMS, "address=?", new String[]{msg});
                SystemClock.sleep(1000);
                progressDialog.incrementProgressBy(1);
            }
            selectItemSet.clear();
            progressDialog.dismiss();
        }
    }

    private class ViewHolder {

        public ImageView ivCheck;
        public TextView date;
        public TextView address;
        public TextView body;
        public ImageView icon;
    }
}
