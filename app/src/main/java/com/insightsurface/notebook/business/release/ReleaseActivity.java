package com.insightsurface.notebook.business.release;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
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
import com.insightsurface.lib.listener.OnDialogClickListener;
import com.insightsurface.lib.utils.LeanCloundUtil;
import com.insightsurface.lib.utils.SingleLoadBarUtil;
import com.insightsurface.lib.utils.ThreeDESUtil;
import com.insightsurface.lib.widget.bar.TopBar;
import com.insightsurface.lib.widget.dialog.NormalDialog;
import com.insightsurface.lib.widget.dragview.DragView;
import com.insightsurface.notebook.R;
import com.insightsurface.notebook.base.BaseActivity;
import com.insightsurface.notebook.configure.Configure;
import com.insightsurface.notebook.listener.OnResultListener;
import com.insightsurface.notebook.utils.StateUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class ReleaseActivity extends BaseActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {
    private EditText titleEt;
    private EditText contentEt;
    private DragView editDv;
    private String noteId;
    private String title, content;

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
                        title = ThreeDESUtil.decode(StateUtil.getKey(ReleaseActivity.this), list.get(0).getString("title")).replaceAll("<space/>", " ");
                        content = ThreeDESUtil.decode(StateUtil.getKey(ReleaseActivity.this), list.get(0).getString("content")).replaceAll("<space/>", " ");
                        titleEt.setText(Html.fromHtml(title));
                        contentEt.setText(Html.fromHtml(content));
                        baseTopBar.setRightText(content.length() + "");
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
                doUploadRelease(new OnResultListener() {
                    @Override
                    public void onFinish() {
                        finish();
                    }

                    @Override
                    public void onFailed() {
                        finish();
                    }
                });
            }

            @Override
            public void onRightClick() {

            }

            @Override
            public void onTitleClick() {
                doUploadRelease();
            }
        });
        baseTopBar.setTitle("笔记");

        editDv.setOnClickListener(this);
    }

    private void doUploadRelease() {
        doUploadRelease(null);
    }

    private void doUploadRelease(final OnResultListener onResultListener) {
        if (TextUtils.isEmpty(titleEt.getText().toString()) || TextUtils.isEmpty(contentEt.getText().toString())) {
            if (null != onResultListener) {
                onResultListener.onFailed();
            }
            return;
        }
        if (!titleEt.isEnabled() && !contentEt.isEnabled()) {
            if (null != onResultListener) {
                onResultListener.onFailed();
            }
            return;
        }

        String newTitle = titleEt.getText().toString().replaceAll(" ", "<space/>").replaceAll("\n", "<br/>");
        String newContent = contentEt.getText().toString().replaceAll(" ", "<space/>").replaceAll("\n", "<br/>");
        baseTopBar.setRightText(newContent.length() + "");
        if (newTitle.equals(title) && newContent.equals(content)) {
            if (null != onResultListener) {
                onResultListener.onFailed();
            }
            return;
        }
        title = newTitle;
        content = newContent;
        saveInLocal();
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
                        if (null != onResultListener) {
                            onResultListener.onFinish();
                        }
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
                        if (null != onResultListener) {
                            onResultListener.onFinish();
                        }
                    }
                }
            });
        }
    }

    @AfterPermissionGranted(111)
    private void saveInLocal() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            // ...
            File file = new File(Configure.DOWNLOAD_PATH);
            if (!file.exists()) {
                file.mkdirs();
            }
            try {
//                FileWriter fw = new FileWriter(Configure.DOWNLOAD_PATH + File.separator
//                        + ThreeDESUtil.encode(StateUtil.getKey(this), title).
//                        replaceAll("/","sp").replaceAll("\\\\","sp")
//                        + ".txt", true);
                FileWriter fw = new FileWriter(Configure.DOWNLOAD_PATH + File.separator
                        + title
                        + ".txt", true);
                fw.write(ThreeDESUtil.encode(StateUtil.getKey(this), content));
                fw.close();
                baseToast.showToast("保存成功!\n已保存至" + Configure.DOWNLOAD_PATH + "文件夹");
                // 上传错误信息到服务器
//                uploadToServer(crashInfo);
            } catch (IOException e) {
                baseToast.showToast(e + "");
            }
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "我们需要写入/读取权限",
                    111, perms);
        }
    }

    @Override
    public void onBackPressed() {
        doUploadRelease(new OnResultListener() {
            @Override
            public void onFinish() {
                finish();
            }

            @Override
            public void onFailed() {
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        baseToast.showToast("已获得授权,请继续!");
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
//        baseToast.showToast(getResources().getString(R.string.no_permissions), true);
        if (111 == requestCode) {
            NormalDialog peanutDialog = new NormalDialog(ReleaseActivity.this);
            peanutDialog.show();
            peanutDialog.setTitle("没有文件读写权限,无法备份!");
        }
    }
}
