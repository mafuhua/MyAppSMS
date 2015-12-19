package com.hellomc.myappsms;

import android.app.SearchManager;
import android.content.SearchRecentSuggestionsProvider;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by hellomc on 2015/12/1.
 */
public class MySuggestionProvider extends SearchRecentSuggestionsProvider {

    /**
     * 声明的 AUTHORITY 要和 清单文件中的，保持一至
     */
    public final static String AUTHORITY = "zz.itcast.sms.manager.z9.provider";

    /**
     * 在提示框中的条目，是二行的模式
     */
    public final static int MODE = DATABASE_MODE_QUERIES | DATABASE_MODE_2LINES;

    private String[] mprojection = new String[]{
            "_id as " + BaseColumns._ID, // 我们搜的这些列名，系统不认识，所以，我们要起个系统认识的别名
            "address as " + SearchManager.SUGGEST_COLUMN_TEXT_1, // listview 条目中，第一行的文字
            "body as " + SearchManager.SUGGEST_COLUMN_TEXT_2, // listview 条目中，第二行的文字
            "body as " + SearchManager.SUGGEST_COLUMN_QUERY  // 点击条目时，搜索该列的值
    };

    public MySuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }

    @Override
    /**
     * 当搜索框中输入内容时，会不断调用此方法 ，查询结果
     */
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        System.out.println("selectionArgs[0]:" + selectionArgs[0]);
        // 我们要做的，就是，根据用户输入的内容，不断的查询数据，返回cursor

        Cursor cursor = getContext().getContentResolver().query(MyContants.URI_SMS,
                mprojection, "body like '%" + selectionArgs[0] + "%'", null, null);
        return cursor;
    }


}