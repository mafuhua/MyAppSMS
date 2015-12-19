package com.hellomc.myappsms;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ConversationDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private final int index_date = 1;
    private final int index_body = 2;
    private final int index_type = 3;
    private String address;
    private Button mBtnBack;
    private TextView mTvTitle;
    private ListView mListView;
    private EditText mEtInputMsg;
    private Button mBtnOk;
    private Context ctx;
    private String[] projection = new String[]{
            "_id",
            "date",
            "body",
            "type"
    };
    private MyAdapter adapter;

    private void assignViews() {
        mBtnBack = (Button) findViewById(R.id.btn_back);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mListView = (ListView) findViewById(R.id.listView);
        mEtInputMsg = (EditText) findViewById(R.id.et_input_msg);
        mBtnOk = (Button) findViewById(R.id.btn_ok);
        mBtnOk.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_detail);
        ctx = this;
        assignViews();
        address = getIntent().getStringExtra("address");
        if (address == null) {
            throw new IllegalStateException("没有电话号码");
        }
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        String name = MyUtils.getNameByAddress(ctx, address);
        Log.d("Con", name);
        if (name != null) {
            mTvTitle.setText(name);
        } else {
            mTvTitle.setText(address);
        }
        adapter = new MyAdapter(ctx, null);
        mListView.setAdapter(adapter);
        fillData();


    }

    private void fillData() {
        MyQueryHandler queryHandler = new MyQueryHandler(getContentResolver());
        queryHandler.startQuery(88, adapter, MyContants.URI_SMS, projection, "address=" + address, null, "date");
        queryHandler.setOnCursorChangedListener(new MyQueryHandler.OnCursorChangedListener() {
            @Override
            public void onCursorChanged(int token, Object cookie, Cursor cursor) {
                mListView.setSelection(adapter.getCount() - 1);
            }
        });
    }

    @Override
    public void onClick(View v) {
        String msg = mEtInputMsg.getText().toString().trim();
        if (TextUtils.isEmpty(msg)) {
            Toast.makeText(ctx, "请输入内容", Toast.LENGTH_SHORT).show();
            return;
        }
        MyUtils.sendTextMsg(ctx, address, msg);
        mEtInputMsg.setText("");
        // 隐藏软键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEtInputMsg.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

    }

    private class MyAdapter extends CursorAdapter {
        public MyAdapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        protected void onContentChanged() {
            super.onContentChanged();
            mListView.setSelection(getCount() - 1);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.list_item_conversation_detail, null);
            ViewHolder vh = new ViewHolder();
            vh.llReceive = (LinearLayout) view.findViewById(R.id.ll_receive);
            vh.tvReceiveBody = (TextView) view.findViewById(R.id.tv_receive_body);
            vh.tvReceiveDate = (TextView) view.findViewById(R.id.tv_receive_date);
            vh.llSend = (LinearLayout) view.findViewById(R.id.ll_send);
            vh.tvSendDate = (TextView) view.findViewById(R.id.tv_send_date);
            vh.tvSendBody = (TextView) view.findViewById(R.id.tv_send_body);
            view.setTag(vh);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder vh = (ViewHolder) view.getTag();
            String bodyStr = cursor.getString(index_body);
            int type = cursor.getInt(index_type);
            long dateLong = cursor.getLong(index_date);
            String dateStr = DateFormat.getDateFormat(ctx).format(dateLong);
            if (type == 1) {
                vh.llReceive.setVisibility(View.VISIBLE);
                vh.llSend.setVisibility(View.GONE);
                vh.tvReceiveBody.setText(bodyStr);
                vh.tvReceiveDate.setText(dateStr);
            } else {
                vh.llReceive.setVisibility(View.GONE);
                vh.llSend.setVisibility(View.VISIBLE);
                vh.tvSendBody.setText(bodyStr);
                vh.tvSendDate.setText(dateStr);
            }

        }
    }

    private class ViewHolder {
        public LinearLayout llReceive;
        public TextView tvReceiveBody;
        public TextView tvReceiveDate;
        public LinearLayout llSend;
        public TextView tvSendDate;
        public TextView tvSendBody;
    }
}
