package com.hellomc.myappsms;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;

public class MainActivity extends TabActivity implements View.OnClickListener {
    private static String tab_conversation = "tab_conversation";
    private static String tab_folder = "tab_folder";
    private static String tab_group = "tab_group";
    private TabHost tabHost;
    private ImageView mSlideBg;
    private LinearLayout mLlconverstation;
    private LinearLayout mLlfolder;
    private LinearLayout mLlgroup;

    private void assignViews() {
        mSlideBg = (ImageView) findViewById(R.id.slide_bg);
        mLlconverstation = (LinearLayout) findViewById(R.id.llconverstation);
        mLlfolder = (LinearLayout) findViewById(R.id.llfolder);
        mLlgroup = (LinearLayout) findViewById(R.id.llgroup);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assignViews();
        mLlconverstation.setOnClickListener(this);
        mLlfolder.setOnClickListener(this);
        mLlgroup.setOnClickListener(this);
        tabHost = getTabHost();
        addTab(tab_conversation, "会话", R.mipmap.tab_conversation, ConversationUI.class);
        addTab(tab_folder, "文件夹", R.mipmap.tab_conversation, FolderUI.class);
        addTab(tab_group, "群组", R.mipmap.tab_conversation, GroupUI.class);

        mLlconverstation.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = mLlconverstation.getWidth();
                int height = mLlconverstation.getHeight();
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mSlideBg.getLayoutParams();
                params.width = width;
                params.height = height;
                int left = mLlconverstation.getLeft();
                mSlideBg.setLayoutParams(params);
                params.leftMargin = left;
                itemLength = ((ViewGroup)mLlconverstation.getParent()).getWidth();
            }
        });

    }

    public void addTab(String tab_name, CharSequence label, int icon_id, Class<? extends Activity> clazz) {
        TabHost.TabSpec tabSpec = tabHost.newTabSpec(tab_name);
        tabSpec.setIndicator("会话", getResources().getDrawable(icon_id));
        Intent intent = new Intent(this, clazz);
        tabSpec.setContent(intent);
        tabHost.addTab(tabSpec);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private int itemLength;
    private int lastPosition = 0;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llconverstation:

                if (!tabHost.getCurrentTabTag().equals(tab_conversation)){
                    tabHost.setCurrentTabByTag(tab_conversation);
                    mSlideBg.startAnimation(getTranslateAnimation(0));
                }

                break;
            case R.id.llfolder:
                if (!tabHost.getCurrentTabTag().equals(tab_folder)){
                    tabHost.setCurrentTabByTag(tab_folder);
                    mSlideBg.startAnimation(getTranslateAnimation(itemLength));
                }
                break;
            case R.id.llgroup:
                if (!tabHost.getCurrentTabTag().equals(tab_group)){
                    tabHost.setCurrentTabByTag(tab_group);
                    mSlideBg.startAnimation(getTranslateAnimation(itemLength*2));
                }
                break;

        }
    }

    @NonNull
    private TranslateAnimation getTranslateAnimation(float destPosition) {
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.ABSOLUTE,lastPosition,Animation.ABSOLUTE,destPosition,
                Animation.ABSOLUTE,0,Animation.ABSOLUTE,0);
        translateAnimation.setDuration(300);
        translateAnimation.setFillAfter(true);
        lastPosition = (int) destPosition;
        return translateAnimation;
    }
}
