package com.insightsurface.notebook.business.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.insightsurface.lib.base.BaseFragment;

import java.util.ArrayList;
import java.util.HashMap;

public class MainFragment extends BaseFragment implements View.OnClickListener, OnRefreshListener,
        OnLoadMoreListener {
    private View logoIv;
    private BannerView bannerView;
    private RecyclerView goodsRcv;
    private HomeBean mainBean;
    private GoodsAdapter mAdapter;
    private ArrayList<ProductBean> goodsList = new ArrayList<>();
    private SwipeToLoadLayout swipeToLoadLayout;
    private int page = 1;
    private View divideV;
    private ListenableScrollView mScrollView;
    private boolean onLoadingMore = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        initUI(v);
        doGetData();
        return v;
    }

    private void doGetData() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("page", page + "");
        VolleyCallBack<HomeBean> callback = new VolleyCallBack<HomeBean>() {

            @Override
            public void loadSucceed(HomeBean result) {
                mainBean = result;
                refreshBanner();
                initGridView();
            }

            @Override
            public void loadFailed(VolleyError error) {
                noMoreData();
            }

            @Override
            public void loadSucceedButNotNormal(HomeBean result) {
                noMoreData();
            }
        };
        VolleyTool.getInstance(getActivity()).requestData(getActivity(), UrlAddress.SHOP_LIST_SUFFIX, params,
                HomeBean.class, callback);
    }


    private void initUI(View view) {
        divideV = view.findViewById(R.id.divide_v);
        int height = 0;
        int resourceId = getActivity().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = getActivity().getResources().getDimensionPixelSize(resourceId);
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
        divideV.setLayoutParams(params);

        swipeToLoadLayout = (SwipeToLoadLayout) view.findViewById(R.id.swipeToLoadLayout);
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
//        swipeToLoadLayout.setLoadMoreEnabled(false);
        mScrollView = (ListenableScrollView) view.findViewById(R.id.swipe_target);
        mScrollView.setScrollViewListener(new ListenableScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(ListenableScrollView scrollView, int x, int y, int oldx, int oldy) {
            }

            @Override
            public void onBottom() {
                onLoadMore();
            }

            @Override
            public void onTop() {

            }
        });
        logoIv = view.findViewById(R.id.logo_iv);
        bannerView = (BannerView) view.findViewById(R.id.banner_view);
        goodsRcv = (RecyclerView) view.findViewById(R.id.goods_rcv);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        goodsRcv.setLayoutManager(layoutManager);
        goodsRcv.setItemAnimator(null);//去掉最后一位的动画
        goodsRcv.setFocusableInTouchMode(false);
        goodsRcv.setFocusable(false);
        goodsRcv.setHasFixedSize(true);

        logoIv.setOnClickListener(this);
        logoIv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (UrlAddress.isTest) {
                    Intent intent = new Intent(getActivity(), AboutActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });
    }

    private void initGridView() {
        int lastPositon = 0;
        try {
            if (page > 1) {
                //如果不是首页 那就加上之后的
                lastPositon = goodsList.size() - 1;
                goodsList.addAll(mainBean.getList());
            } else {
                if (null != mainBean) {
                    lastPositon = 0;
                    goodsList = mainBean.getList();
                }
            }
            if (null == mAdapter) {
                mAdapter = new GoodsAdapter(getActivity());
                mAdapter.setList(goodsList);
                mAdapter.setOnRecycleItemClickListener(new OnRecycleItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Intent intent = new Intent(getActivity(), GoodsDetailsActivity.class);
                        intent.putExtra("goods_id", goodsList.get(position).getGoods_id());
                        startActivity(intent);
                    }
                });
                goodsRcv.setAdapter(mAdapter);
//                ColorDrawable dividerDrawable = new ColorDrawable(0x00000000) {
//                    @Override
//                    public int getIntrinsicHeight() {
//                        return DisplayUtil.dip2px(getActivity(), 15);
//                    }
//
//                    @Override
//                    public int getIntrinsicWidth() {
//                        return DisplayUtil.dip2px(getActivity(), 15);
//                    }
//                };
//                RecyclerGridDecoration itemDecoration = new RecyclerGridDecoration(getActivity(),
//                        dividerDrawable, true);
//                goodsRcv.addItemDecoration(itemDecoration);
            } else {
                mAdapter.setList(goodsList);
                if (lastPositon == 0) {
                    mAdapter.notifyDataSetChanged();
                } else {
                    mAdapter.notifyItemRangeChanged(lastPositon, goodsList.size());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            noMoreData();
        }
    }

    private void refreshBanner() {
        if (bannerView == null || null == mainBean.getBannerlist() || mainBean.getBannerlist().size() < 1)
            return;
        bannerView.setBannerList(mainBean.getBannerlist());
        bannerView.setOnItemClickListener(new BannerView.OnItemClickListener() {
            // 图片点击事件
            @Override
            public void OnItemClick(int position) {
                Intent intent = new Intent(getActivity(), WebActivity.class);
                intent.putExtra("url", mainBean.getBannerlist().get(position).getClick_url());
                startActivity(intent);
            }
        });
        bannerView.startRoll(8000);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.logo_iv:
                if (UrlAddress.isTest) {
                    intent = new Intent(getActivity(), TestActivity.class);
                }
                break;
        }
        if (null != intent) {
            startActivity(intent);
        }
    }

    public void scrollToTop() {
        mScrollView.smoothScrollTo(0, 0);
    }

    private void noMoreData() {
        onLoadingMore = false;
        swipeToLoadLayout.setRefreshing(false);
        swipeToLoadLayout.setLoadingMore(false);
    }

    @Override
    public void onLoadMore() {
        if (!onLoadingMore) {
            onLoadingMore = true;
            page++;
            doGetData();
        }
    }

    @Override
    public void onRefresh() {
        page = 1;
        doGetData();
    }
}
