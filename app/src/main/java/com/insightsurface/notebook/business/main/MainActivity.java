package com.insightsurface.notebook.business.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.insightsurface.lib.config.Configure;
import com.insightsurface.notebook.R;
import com.insightsurface.notebook.base.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseToast.showToast(Configure.isTest(this)+"");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }
}
