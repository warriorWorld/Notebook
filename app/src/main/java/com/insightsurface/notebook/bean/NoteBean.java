package com.insightsurface.notebook.bean;

import com.insightsurface.lib.bean.BaseBean;
import com.insightsurface.lib.bean.LeanBaseBean;

public class NoteBean extends LeanBaseBean {
    private int dbId;
    private String title;
    private String content;
    private String create_at_s;
    private String folder;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreate_at_s() {
        return create_at_s;
    }

    public void setCreate_at_s(String create_at_s) {
        this.create_at_s = create_at_s;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public int getDbId() {
        return dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }
}
