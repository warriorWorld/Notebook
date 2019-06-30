package com.insightsurface.notebook.business.encrypt;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.insightsurface.lib.utils.SharedPreferencesUtils;
import com.insightsurface.notebook.R;
import com.insightsurface.notebook.base.BaseActivity;
import com.insightsurface.notebook.configure.ShareKeys;
import com.insightsurface.notebook.utils.StateUtil;

public class KeyActivity extends BaseActivity implements View.OnClickListener {
    private EditText keyEt;
    private Button okBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (StateUtil.haveKey(this, false)) {
            baseToast.showToast("秘钥不可更改!");
            finish();
        }
    }

    @Override
    protected void initUI() {
        super.initUI();
        keyEt = (EditText) findViewById(R.id.key_et);
        okBtn = (Button) findViewById(R.id.ok_btn);

        okBtn.setOnClickListener(this);
        baseTopBar.setTitle("设置秘钥");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_key;
    }

    @Override
    public void onClick(View v) {
        if (v == okBtn) {
            // Handle clicks for okBtn
            String key = keyEt.getText().toString().replaceAll(" ", "");
            if (TextUtils.isEmpty(key)) {
                baseToast.showToast("秘钥不能为空");
                return;
            }
            if (key.length() != 24) {
                baseToast.showToast("秘钥需为24位");
                return;
            }
            SharedPreferencesUtils.setSharedPreferencesData(KeyActivity.this, ShareKeys.KEY_KEY, key);
            baseToast.showToast("秘钥设置完成!");
            finish();
        }
    }
}
