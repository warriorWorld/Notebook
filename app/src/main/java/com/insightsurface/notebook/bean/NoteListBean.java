package com.insightsurface.notebook.bean;

import com.insightsurface.lib.bean.BaseBean;

import java.util.ArrayList;

public class NoteListBean extends BaseBean {
    private ArrayList<NoteBean> list;

    public ArrayList<NoteBean> getList() {
        return list;
    }

    public void setList(ArrayList<NoteBean> list) {
        this.list = list;
    }
}
