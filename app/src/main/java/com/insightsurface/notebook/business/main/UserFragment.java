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

import com.insightsurface.event.Event;
import com.insightsurface.lib.base.BaseFragment;
import com.insightsurface.lib.bean.LoginBean;
import com.insightsurface.notebook.R;

import org.greenrobot.eventbus.EventBus;

public class UserFragment extends BaseFragment implements View.OnClickListener {
    private ImageView userIv;
    private TextView userNameTv;
    private TextView logoutTv;
    private RelativeLayout feedbackRl;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_user, null);
        initUI(mainView);
        toggleUI();
        return mainView;
    }

    private void initUI(View view) {

    }

    private void doLogout() {
        LoginBean.getInstance().clean(getActivity());
        EventBus.getDefault().post(
                new Event(Event.LOGOUT_EVENT));
        toggleUI();
    }

    private void toggleUI() {
        if (TextUtils.isEmpty(LoginBean.getInstance().getObjectId())) {
            userNameTv.setText("点击登录");
        }else {
            userNameTv.setText(LoginBean.getInstance().getUserName());
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.user_iv:
                break;
            case R.id.feedback_rl:
                break;
            case R.id.logout_tv:
                doLogout();
                break;
        }
        if (null != intent) {
            startActivity(intent);
        }
    }
}
