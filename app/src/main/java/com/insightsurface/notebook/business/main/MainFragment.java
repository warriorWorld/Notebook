package com.insightsurface.notebook.business.main;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CloudQueryCallback;
import com.avos.avoscloud.FindCallback;
import com.insightsurface.lib.base.BaseRefreshListFragment;
import com.insightsurface.lib.bean.LoginBean;
import com.insightsurface.lib.listener.OnDialogClickListener;
import com.insightsurface.lib.listener.OnRecycleItemClickListener;
import com.insightsurface.lib.listener.OnRecycleItemLongClickListener;
import com.insightsurface.lib.utils.LeanCloundUtil;
import com.insightsurface.lib.utils.SingleLoadBarUtil;
import com.insightsurface.lib.widget.dialog.NormalDialog;
import com.insightsurface.notebook.R;
import com.insightsurface.notebook.adapter.NoteAdapter;
import com.insightsurface.notebook.bean.NoteBean;
import com.insightsurface.notebook.business.release.ReleaseActivity;
import com.insightsurface.notebook.utils.StateUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainFragment extends BaseRefreshListFragment {
    private ArrayList<NoteBean> noteList;
    private NoteAdapter mAdapter;

    @Override
    protected void initUI(View v) {
        super.initUI(v);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            doGetData();
        }
    }

    @Override
    protected void doGetData() {
        if (TextUtils.isEmpty(LoginBean.getInstance().getObjectId())) {
            initRec();
            return;
        }
        SingleLoadBarUtil.getInstance().showLoadBar(getActivity());
        AVQuery<AVObject> query = new AVQuery<>("Note");
        query.whereEqualTo("targetUser", AVObject.createWithoutData("_User", LoginBean.getInstance().getObjectId()));
        query.limit(50);
        query.selectKeys(Arrays.asList("title"));
        // 按时间，降序排列
        query.orderByDescending("createdAt");
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
                            item.setTitle(list.get(i).getString("title"));
                            item.setCreate_at(list.get(i).getCreatedAt());
                            item.setObjectId(list.get(i).getObjectId());
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
        return R.layout.fragment_main;
    }

    public void scrollToTop() {
        refreshRcv.scrollToPosition(0);
    }

    @Override
    protected String getType() {
        return null;
    }

    private void deleteNote(String noteId) {
        if (TextUtils.isEmpty(noteId)) {
            return;
        }
        SingleLoadBarUtil.getInstance().showLoadBar(getActivity());
        // 执行 CQL 语句实现删除一个  对象
        AVQuery.doCloudQueryInBackground(
                "delete from Note where objectId='" + noteId + "'"
                , new CloudQueryCallback<AVCloudQueryResult>() {
                    @Override
                    public void done(AVCloudQueryResult avCloudQueryResult, AVException e) {
                        SingleLoadBarUtil.getInstance().dismissLoadBar();
                        if (LeanCloundUtil.handleLeanResult(getActivity(), e)) {
                            doGetData();
                        }
                    }
                });
    }

    private void showDeleteDialog(final String id) {
        NormalDialog dialog = new NormalDialog(getActivity());
        dialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onOkClick() {
                deleteNote(id);
            }

            @Override
            public void onCancelClick() {

            }
        });
        dialog.show();
        dialog.setTitle("是否删除该笔记?");
        dialog.setOkBtnText("删除");
        dialog.setCancelBtnText("取消");
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
                        if (StateUtil.haveKey(getActivity())) {
                            Intent intent = new Intent(getActivity(), ReleaseActivity.class);
                            intent.putExtra("note_id", noteList.get(position).getObjectId());
                            startActivity(intent);
                        }
                    }
                });
                mAdapter.setOnRecycleItemLongClickListener(new OnRecycleItemLongClickListener() {
                    @Override
                    public void onItemLongClick(int position) {
                        showDeleteDialog(noteList.get(position).getObjectId());
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
