package com.insightsurface.notebook.business.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.insightsurface.notebook.business.encrypt.KeyActivity;
import com.insightsurface.notebook.configure.Configure;
import com.insightsurface.notebook.event.Event;
import com.insightsurface.lib.base.BaseFragment;
import com.insightsurface.lib.bean.LoginBean;
import com.insightsurface.notebook.R;
import com.insightsurface.notebook.utils.FileSpider;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

public class UserFragment extends BaseFragment implements View.OnClickListener {
    private ImageView userIv;
    private TextView userNameTv;
    private TextView logoutTv;
    private RelativeLayout feedbackRl;
    private RelativeLayout folderRl;
    private RelativeLayout keyRl;
    private RelativeLayout backupRl;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_user, null);
        initUI(mainView);
        toggleUI();
        return mainView;
    }

    private void initUI(View view) {
        userIv = (ImageView) view.findViewById(R.id.user_iv);
        userNameTv = (TextView) view.findViewById(R.id.user_name_tv);
        folderRl = (RelativeLayout) view.findViewById(R.id.folder_rl);
        keyRl = (RelativeLayout) view.findViewById(R.id.key_rl);
        feedbackRl = (RelativeLayout) view.findViewById(R.id.feedback_rl);
        logoutTv = (TextView) view.findViewById(R.id.logout_tv);
        backupRl = view.findViewById(R.id.backups_rl);

        backupRl.setOnClickListener(this);
        userIv.setOnClickListener(this);
        folderRl.setOnClickListener(this);
        keyRl.setOnClickListener(this);
        feedbackRl.setOnClickListener(this);
        logoutTv.setOnClickListener(this);
    }

    private void doLogout() {
        LoginBean.getInstance().clean(getActivity());
        EventBus.getDefault().post(
                new Event(Event.LOGOUT_EVENT));
        toggleUI();
    }

    private void toggleUI() {
        logoutTv.setVisibility(View.GONE);
        if (TextUtils.isEmpty(LoginBean.getInstance().getObjectId())) {
            userNameTv.setText("点击登录");
        } else {
            userNameTv.setText(LoginBean.getInstance().getUserName());
            logoutTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.user_iv:
                break;
            case R.id.key_rl:
                intent = new Intent(getActivity(), KeyActivity.class);
                break;
            case R.id.feedback_rl:
                break;
            case R.id.logout_tv:
                doLogout();
                break;
            case R.id.backups_rl:
                FileSpider.getInstance().copyDir(Configure.DOWNLOAD_PATH+ File.separator, Configure.BACKUPS_PATH+ File.separator);
                baseToast.showToast("备份成功！");
                break;
        }
        if (null != intent) {
            startActivity(intent);
        }
    }
}
