package com.hellomc.myappsms;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class NewMsgActivity extends AppCompatActivity implements View.OnClickListener {

    private AutoCompleteTextView mActv;
    private ImageView mIvSelectContact;
    private EditText mEtInputMsg;
    private MyAdapter adapter;
    private Context ctx;
    private String[] projection = new String[]{
            "_id", "display_name", "data1"
    };


    private void assignViews() {
        mActv = (AutoCompleteTextView) findViewById(R.id.actv);
        mIvSelectContact = (ImageView) findViewById(R.id.iv_select_contact);
        mEtInputMsg = (EditText) findViewById(R.id.et_input_msg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_msg);
        ctx = this;
        assignViews();
        mIvSelectContact.setOnClickListener(this);
        adapter = new MyAdapter(ctx, null);
        mActv.setAdapter(adapter);
        adapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                if (constraint == null) {
                    return null;
                }
                Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                Cursor cursor = getContentResolver().query(uri, projection, "data1 like '%" + constraint + "%'", null, null);
                return cursor;
            }
        });

    }

    public void sendMsg(View view) {
        String address = mActv.getText().toString().trim();
        if (TextUtils.isEmpty(address)) {
            Toast.makeText(this, "请输入号码", Toast.LENGTH_SHORT).show();
            return;
        }
        String msg = mEtInputMsg.getText().toString().trim();
        if (TextUtils.isEmpty(msg)) {
            Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
            return;
        }
        MyUtils.sendTextMsg(this, address, msg);
    }

    @Override
    public void onClick(View v) {
            /*{
        act = android.intent.action.MAIN cat =[android.intent.category.LAUNCHER]flg = 0x10200000
        cmp = com.android.contacts /.activities.PeopleActivity
        11-30 11:20:55.413     399-1631/? I/ActivityManager﹕ START
        u0 {act=android.intent.action.MAIN cat=[android.intent.category.LAUNCHER] flg=0x10200000
         cmp=com.android.contacts/.activities.PeopleActivity bnds=[168,1136][296,1264]} from pid 632l 12ms

      399-414/? I/ActivityManager﹕ Displayed com.android.contacts/.activities.PeopleActivity: +244ms

    }*/
        Intent intent = new Intent();
//		act=android.intent.action.PICK
//		dat=content://com.android.contacts/contacts
        intent.setAction("android.intent.action.PICK");
        intent.setData(Uri.parse("content://com.android.contacts/contacts"));

        startActivityForResult(intent, 88);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null){
            return ;
        }
        Uri uri = data.getData();

        Cursor cursor = getContentResolver().query(uri, new String[]{"_id"}, null, null, null);
        cursor.moveToNext(); // 向下移动一行，而且，肯定不会出错
        String contactId = cursor.getString(0); // 仅查了一列

        //  根据联络人ID，获得电话号码
        Cursor cursor2 = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{"data1"},
                " contact_id = ?", new String[]{contactId}, null);
        cursor2.moveToNext(); // 肯定也是有内容的
        String address = cursor2.getString(0);

        mActv.setText(address);

    }

    private class MyAdapter extends CursorAdapter {
        public MyAdapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public CharSequence convertToString(Cursor cursor) {
            String result = "";
            result = cursor.getString(2);
            return result;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.list_item_actv, null);

            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView tv_name_list_item = (TextView) view.findViewById(R.id.tv_name_list_item);
            TextView tv_address_list_item = (TextView) view.findViewById(R.id.tv_address_list_item);
            tv_name_list_item.setText(cursor.getString(1));
            tv_address_list_item.setText(cursor.getString(2));
        }
    }


}
