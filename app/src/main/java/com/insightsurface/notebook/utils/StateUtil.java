package com.insightsurface.notebook.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.insightsurface.lib.bean.LoginBean;
import com.insightsurface.lib.listener.OnDialogClickListener;
import com.insightsurface.lib.utils.SharedPreferencesUtils;
import com.insightsurface.lib.widget.dialog.NormalDialog;
import com.insightsurface.notebook.business.encrypt.KeyActivity;
import com.insightsurface.notebook.configure.ShareKeys;

public class StateUtil {
    public static boolean isLogin() {
        return !TextUtils.isEmpty(LoginBean.getInstance().getObjectId());
    }

    public static boolean haveKey(final Context context) {
        return haveKey(context, true);
    }

    public static boolean haveKey(final Context context, boolean autoJump) {
        if (!TextUtils.isEmpty(SharedPreferencesUtils.getSharedPreferencesData(context, ShareKeys.KEY_KEY))) {
            return true;
        } else {
            if (!autoJump) {
                return false;
            }
            NormalDialog dialog = new NormalDialog(context);
            dialog.setOnDialogClickListener(new OnDialogClickListener() {
                @Override
                public void onOkClick() {
                    Intent intent = new Intent(context, KeyActivity.class);
                    context.startActivity(intent);
                }

                @Override
                public void onCancelClick() {

                }
            });
            dialog.show();
            dialog.setTitle("请先设置秘钥");
            dialog.setMessage("设置秘钥完成后才可以添加笔记");
            dialog.setOkBtnText("去设置");
            dialog.setCancelBtnText("取消");
            return false;
        }
    }

    public static String getKey(Context context) {
        if (haveKey(context)) {
            return SharedPreferencesUtils.getSharedPreferencesData(context, ShareKeys.KEY_KEY);
        } else {
            return "";
        }
    }
}
