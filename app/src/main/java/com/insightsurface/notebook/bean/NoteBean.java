package com.insightsurface.notebook.bean;

import com.insightsurface.lib.bean.BaseBean;
import com.insightsurface.lib.bean.LeanBaseBean;

public class NoteBean extends LeanBaseBean {
    private String title;
    private String content;

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
}
