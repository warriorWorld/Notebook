package com.insightsurface.notebook.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.insightsurface.lib.base.BaseRecyclerAdapter;
import com.insightsurface.lib.listener.OnRecycleItemClickListener;
import com.insightsurface.lib.listener.OnRecycleItemLongClickListener;
import com.insightsurface.lib.utils.ThreeDESUtil;
import com.insightsurface.notebook.R;
import com.insightsurface.notebook.bean.NoteBean;
import com.insightsurface.notebook.business.main.MainActivity;
import com.insightsurface.notebook.business.release.ReleaseActivity;
import com.insightsurface.notebook.utils.StateUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/11/15.
 * 还款页的还款计划
 */
public class NoteAdapter extends BaseRecyclerAdapter {
    private ArrayList<NoteBean> list = null;
    private OnRecycleItemClickListener onRecycleItemClickListener;
    private OnRecycleItemLongClickListener mOnRecycleItemLongClickListener;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
    public NoteAdapter(Context context) {
        super(context);
    }

    @Override
    protected String getEmptyText() {
        return "暂无笔记";
    }

    @Override
    protected String getEmptyBtnText() {
        return "+  添加笔记";
    }

    @Override
    protected View.OnClickListener getEmptyBtnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StateUtil.haveKey(context)) {
                    Intent intent = new Intent(context, ReleaseActivity.class);
                    context.startActivity(intent);
                }
            }
        };
    }

    @Override
    protected String getListEndText() {
        return "更多请进入文件夹查看";
    }

    @Override
    protected <T> ArrayList<T> getDatas() {
        return (ArrayList<T>) list;
    }

    @Override
    protected RecyclerView.ViewHolder getNormalViewHolder(ViewGroup viewGroup) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_note, viewGroup, false);
        NormalViewHolder vh = new NormalViewHolder(view);
        return vh;
    }

    @Override
    protected void refreshNormalViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final NoteBean item = list.get(position);
        if (StateUtil.haveKey(context,false)) {
            ((NormalViewHolder) viewHolder).titleTv.setText(Html.fromHtml(ThreeDESUtil.decode(StateUtil.getKey(context), item.getTitle())));
        }else {
            ((NormalViewHolder) viewHolder).titleTv.setText("请重新填写秘钥");
        }
        ((NormalViewHolder) viewHolder).timeTv.setText(format.format(item.getCreate_at()));
        ((NormalViewHolder) viewHolder).noteLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onRecycleItemClickListener) {
                    onRecycleItemClickListener.onItemClick(position);
                }
            }
        });
        ((NormalViewHolder) viewHolder).noteLl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (null!=mOnRecycleItemLongClickListener){
                    mOnRecycleItemLongClickListener.onItemLongClick(position);
                }
                return true;
            }
        });
    }

    public void setList(ArrayList<NoteBean> list) {
        this.list = list;
    }

    public ArrayList<NoteBean> getList() {
        return list;
    }

    public void setOnRecycleItemClickListener(OnRecycleItemClickListener onRecycleItemClickListener) {
        this.onRecycleItemClickListener = onRecycleItemClickListener;
    }

    public void setOnRecycleItemLongClickListener(OnRecycleItemLongClickListener onRecycleItemLongClickListener) {
        mOnRecycleItemLongClickListener = onRecycleItemLongClickListener;
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class NormalViewHolder extends RecyclerView.ViewHolder {
        private View noteLl;
        private TextView titleTv;
        private TextView timeTv;

        public NormalViewHolder(View view) {
            super(view);
            noteLl = (View) view.findViewById(R.id.note_ll);
            titleTv = (TextView) view.findViewById(R.id.title_tv);
            timeTv = (TextView) view.findViewById(R.id.time_tv);
        }
    }
}
