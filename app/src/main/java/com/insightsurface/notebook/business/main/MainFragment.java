package com.insightsurface.notebook.business.main;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.insightsurface.lib.base.BaseRefreshListFragment;
import com.insightsurface.lib.bean.LoginBean;
import com.insightsurface.lib.listener.OnRecycleItemClickListener;
import com.insightsurface.lib.utils.LeanCloundUtil;
import com.insightsurface.lib.utils.SingleLoadBarUtil;
import com.insightsurface.notebook.adapter.NoteAdapter;
import com.insightsurface.notebook.bean.NoteBean;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends BaseRefreshListFragment {
    private ArrayList<NoteBean> noteList;
    private NoteAdapter mAdapter;

    @Override
    protected void initUI(View v) {
        super.initUI(v);
        swipeToLoadLayout.setLoadMoreEnabled(false);
    }

    @Override
    protected void doGetData() {
        if (TextUtils.isEmpty(LoginBean.getInstance().getObjectId())) {
            initRec();
            return;
        }
        SingleLoadBarUtil.getInstance().showLoadBar(getActivity());
        AVQuery<AVObject> query = new AVQuery<>("Note");
        query.whereEqualTo("targetTodoFolder", AVObject.createWithoutData("_User", LoginBean.getInstance().getObjectId()));
        query.limit(50);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                SingleLoadBarUtil.getInstance().dismissLoadBar();
                if (LeanCloundUtil.handleLeanResult(getActivity(), e)) {
                    noteList = new ArrayList<NoteBean>();
                    if (null != list && list.size() > 0) {
                        NoteBean item;
                        for (int i = 0; i < list.size(); i++) {
                            item = new NoteBean();
                            item.setContent(list.get(i).getString("content"));
                            item.setTitle(list.get(i).getString("title"));
                            noteList.add(item);
                        }
                    }
                    initRec();
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    public void scrollToTop() {
        refreshRcv.scrollToPosition(0);
    }

    @Override
    protected String getType() {
        return null;
    }

    @Override
    protected void initRec() {
        try {
            if (null == mAdapter) {
                mAdapter = new NoteAdapter(getActivity());
                mAdapter.setList(noteList);
                mAdapter.setOnRecycleItemClickListener(new OnRecycleItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                    }
                });
                refreshRcv.setAdapter(mAdapter);
            } else {
                mAdapter.setList(noteList);
                mAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            noMoreData();
        }
        noMoreData();
    }
}
