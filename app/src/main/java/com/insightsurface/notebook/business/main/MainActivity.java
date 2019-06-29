package com.insightsurface.notebook.business.main;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import com.insightsurface.lib.bean.LoginBean;
import com.insightsurface.lib.business.user.LoginActivity;
import com.insightsurface.lib.eventbus.EventBusEvent;
import com.insightsurface.notebook.R;
import com.insightsurface.notebook.base.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setStatusBarFullTransparent();
            setFitSystemWindow(false);
        }
        if (TextUtils.isEmpty(LoginBean.getInstance().getObjectId())) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void initUI() {
        super.initUI();
        hideBaseTopBar();
    }

    @Override
    public void onEventMainThread(EventBusEvent event) {
        super.onEventMainThread(event);
        switch (event.getEventType()){
            case EventBusEvent.LOGIN_EVENT:

                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }
}
