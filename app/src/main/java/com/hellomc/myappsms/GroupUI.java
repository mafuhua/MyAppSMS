package com.hellomc.myappsms;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class GroupUI extends ListActivity {
    private ListView listView;
    private Context ctx;
    private AlertDialog dialog;
    private GroupDao groupDao;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_group_ui);
        ctx = this;
        groupDao = GroupDao.getInstance(ctx);
        listView = getListView();

        fillData();
        regListener();
    }

    private void regListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = adapter.getCursor();
                cursor.moveToPosition(position);
                int groupId = cursor.getInt(0);
                List<Integer> threadByGroupId = groupDao.getThreadByGroupId(groupId);
                if (threadByGroupId.size() == 0) {
                    Toast.makeText(ctx, "该群还是空的，先添加人吧", Toast.LENGTH_SHORT).show();
                    return;
                } else {

                    Intent intent = new Intent(ctx, ConversationUI.class);
                    String subSql = pincou(threadByGroupId);
                    intent.putExtra("subSql", subSql);
                    startActivity(intent);
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showSelectDialog(position);
                return true;
            }
        });
    }

    private String pincou(List<Integer> threadByGroupId) {
        StringBuffer sb = new StringBuffer(" thread_id in (");
        for (int id : threadByGroupId) {
            sb.append(id + ",");
        }
        sb.replace(sb.length() - 1, sb.length(), ")");
        return sb.toString();
    }

    private void showSelectDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setItems(new String[]{"编辑", "删除"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showUpdateNameDialog(position);
                } else {
                    showDeleteDialog(position);
                }
            }
        });
    }

    private void showDeleteDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("删除群组");
        builder.setMessage("真的要删除吗?");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Cursor cursor = adapter.getCursor();
                cursor.moveToPosition(position);
                String groupName = cursor.getString(1);
                groupDao.delete(groupName);
            }
        });
        builder.show();
    }

    private void showUpdateNameDialog(int position) {
        Cursor cursor = adapter.getCursor();
        cursor.moveToPosition(position);
        final String oldGroupName = cursor.getString(1);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialog = builder.create();
        dialog.setTitle("创建群组");
        final EditText editText = new EditText(this);
        editText.setBackgroundResource(R.drawable.et_input_bg);
        editText.setText(oldGroupName);
        dialog.setView(editText, 0, 0, 0, 0);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String edit = editText.getText().toString().trim();
                if (TextUtils.isEmpty(edit)) {
                    Toast.makeText(ctx, "不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (edit.equals(oldGroupName)) {
                    Toast.makeText(ctx, "名称不能一样", Toast.LENGTH_SHORT).show();
                    return;
                }
                groupDao.updateGroupName(oldGroupName, edit);
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void fillData() {
        Cursor cursor = groupDao.getAllGroups();
        adapter = new MyAdapter(this, cursor);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_group_ui, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            showNewGroupDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showNewGroupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialog = builder.create();
        dialog.setTitle("创建群组");
        final EditText editText = new EditText(this);
        editText.setBackgroundResource(R.drawable.et_input_bg);
        editText.setHint("请输入群组名称");
        dialog.setView(editText, 0, 0, 0, 0);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String edit = editText.getText().toString().trim();
                if (TextUtils.isEmpty(edit)) {
                    Toast.makeText(ctx, "不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                groupDao.addNewGroup(edit);
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private class MyAdapter extends CursorAdapter {
        public MyAdapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {

            View view = getLayoutInflater().inflate(R.layout.list_item_group, null);

            TextView tvName = (TextView) view.findViewById(R.id.tv_name_list_ite);

            view.setTag(tvName); // 将子view 当做背包，背在view的身上

            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView tvName = (TextView) view.getTag();

            tvName.setText(cursor.getString(cursor.getColumnIndex("group_name")));
        }
    }
}
