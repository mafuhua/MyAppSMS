package com.hellomc.myappsms;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;

public class MyQueryHandler extends AsyncQueryHandler {

    public OnQueryCursorCompliteListener queryCursorCompliteListener;
    private OnCursorChangedListener onCursorChangedListener;

    public MyQueryHandler(ContentResolver cr) {
        super(cr);
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        super.onQueryComplete(token, cookie, cursor);
        if (queryCursorCompliteListener != null) {
            queryCursorCompliteListener.onQueryCursorComplite(token, cookie, cursor);
        }
        System.out.println("token:" + token + "  cookie:" + cookie + " cursor:" + cursor);

        if (cookie instanceof CursorAdapter) {
            CursorAdapter adapter = (CursorAdapter) cookie;
            adapter.swapCursor(cursor);
            if (onCursorChangedListener != null) {
                onCursorChangedListener.onCursorChanged(token, cookie, cursor);
            }
        }
    }

    public void setOnCursorChangedListener(OnCursorChangedListener onCursorChangedListener) {
        this.onCursorChangedListener = onCursorChangedListener;

    }

    public void setOnQueryCursorCompliteListener(OnQueryCursorCompliteListener queryCursorCompliteListener) {
        this.queryCursorCompliteListener = queryCursorCompliteListener;
    }

    public interface OnCursorChangedListener {
        void onCursorChanged(int token, Object cookie, Cursor cursor);
    }

    public interface OnQueryCursorCompliteListener {
        void onQueryCursorComplite(int token, Object cookie, Cursor cursor);
    }

}
