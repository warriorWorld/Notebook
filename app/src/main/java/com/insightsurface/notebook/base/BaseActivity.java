package com.insightsurface.notebook.base;/**
 * Created by Administrator on 2016/10/17.
 */

import android.content.Intent;
import android.os.Bundle;

import com.insightsurface.lib.listener.ScreenListener;
import com.insightsurface.notebook.business.fingerprint.FingerPrintActivity;


/**
 * 作者：苏航 on 2016/10/17 11:56
 * 邮箱：772192594@qq.com
 */
public abstract class BaseActivity extends com.insightsurface.lib.base.BaseActivity implements ScreenListener.ScreenStateListener {
    private ScreenListener screenListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 启动监听锁屏的广播
        screenListener = new ScreenListener(this);
        screenListener.begin(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 每次必须取消订阅
        screenListener.unregisterListener();
    }

    @Override
    public void onScreenOn() {

    }

    @Override
    public void onScreenOff() {
        Intent intent = new Intent(BaseActivity.this, FingerPrintActivity.class);
        startActivity(intent);
    }

    @Override
    public void onUserPresent() {

    }
}
