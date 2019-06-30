package com.insightsurface.notebook.business.release;

import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.insightsurface.lib.bean.LoginBean;
import com.insightsurface.lib.config.Configure;
import com.insightsurface.lib.utils.LeanCloundUtil;
import com.insightsurface.lib.utils.SingleLoadBarUtil;
import com.insightsurface.lib.utils.ThreeDESUtil;
import com.insightsurface.lib.widget.bar.TopBar;
import com.insightsurface.lib.widget.dragview.DragView;
import com.insightsurface.notebook.R;
import com.insightsurface.notebook.base.BaseActivity;
import com.insightsurface.notebook.utils.StateUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReleaseActivity extends BaseActivity implements View.OnClickListener {
    private EditText titleEt;
    private EditText contentEt;
    private DragView editDv;
    private String noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        noteId = getIntent().getStringExtra("note_id");
        titleEt.setEnabled(TextUtils.isEmpty(noteId));
        contentEt.setEnabled(TextUtils.isEmpty(noteId));
        if (!TextUtils.isEmpty(noteId)) {
            doGetNote();
        }
    }

    private void doGetNote() {
        SingleLoadBarUtil.getInstance().showLoadBar(this);
        AVQuery<AVObject> query = new AVQuery<>("Note");
        query.whereEqualTo("objectId", noteId);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                SingleLoadBarUtil.getInstance().dismissLoadBar();
                if (LeanCloundUtil.handleLeanResult(ReleaseActivity.this, e)) {
                    if (null != list && list.size() > 0) {
                        titleEt.setText(Html.fromHtml(ThreeDESUtil.decode(StateUtil.getKey(ReleaseActivity.this), list.get(0).getString("title"))));
                        contentEt.setText(Html.fromHtml(ThreeDESUtil.decode(StateUtil.getKey(ReleaseActivity.this), list.get(0).getString("content"))));
                    }
                }
            }
        });
    }

    @Override
    protected void initUI() {
        super.initUI();
        titleEt = (EditText) findViewById(R.id.title_et);
        contentEt = (EditText) findViewById(R.id.content_et);
        editDv = (DragView) findViewById(R.id.edit_dv);
        baseTopBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {
            @Override
            public void onLeftClick() {
                doUploadRelease();
            }

            @Override
            public void onRightClick() {

            }

            @Override
            public void onTitleClick() {

            }
        });
        baseTopBar.setTitle("笔记");

        editDv.setOnClickListener(this);
    }

    private void doUploadRelease() {
        if (TextUtils.isEmpty(titleEt.getText().toString())) {
            finish();
            return;
        }
        if (!titleEt.isEnabled() && !contentEt.isEnabled()) {
            finish();
            return;
        }
        String title = titleEt.getText().toString().replaceAll(" ", "<space/>").replaceAll("\n", "<br/>");
        String content = contentEt.getText().toString().replaceAll(" ", "<space/>").replaceAll("\n", "<br/>");
        if (TextUtils.isEmpty(noteId)) {
            SingleLoadBarUtil.getInstance().showLoadBar(this);
            AVObject note = new AVObject("Note");// 构建 Comment 对象
            note.put("title", ThreeDESUtil.encode(StateUtil.getKey(this), title));// 如果点了赞就是 1，而点了不喜欢则为 -1，没有做任何操作就是默认的 0
            if (!TextUtils.isEmpty(content)) {
                note.put("content", ThreeDESUtil.encode(StateUtil.getKey(this), content));
            }

            // 假设已知了被分享的该 TodoFolder 的 objectId 是 5590cdfde4b00f7adb5860c8
            note.put("targetUser", AVObject.createWithoutData("_User", LoginBean.getInstance().getObjectId()));
            // 以上代码就是的执行结果就会在 comment 对象上有一个名为 targetTodoFolder 属性，它是一个 Pointer 类型，指向 objectId 为 5590cdfde4b00f7adb5860c8 的 TodoFolder
            note.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    SingleLoadBarUtil.getInstance().dismissLoadBar();
                    if (LeanCloundUtil.handleLeanResult(ReleaseActivity.this, e)) {
                        baseToast.showToast("上传完成");
                        finish();
                    }
                }
            });
        } else {
            SingleLoadBarUtil.getInstance().showLoadBar(this);
            AVObject object = AVObject.createWithoutData("Note", noteId);
            object.put("title", ThreeDESUtil.encode(StateUtil.getKey(this), title));// 如果点了赞就是 1，而点了不喜欢则为 -1，没有做任何操作就是默认的 0
            if (!TextUtils.isEmpty(content)) {
                object.put("content", ThreeDESUtil.encode(StateUtil.getKey(this), content));
            }
            object.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    SingleLoadBarUtil.getInstance().dismissLoadBar();
                    if (LeanCloundUtil.handleLeanResult(ReleaseActivity.this, e)) {
                        baseToast.showToast("上传完成");
                        finish();
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        doUploadRelease();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_release;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_dv:
                titleEt.setEnabled(true);
                contentEt.setEnabled(true);
                break;
        }
    }
}
