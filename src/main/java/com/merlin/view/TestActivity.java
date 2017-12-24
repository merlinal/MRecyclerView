package com.merlin.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.merlin.view.recycler.AbstractRecyclerAdapter;
import com.merlin.view.recycler.MRecyclerView;
import com.merlin.view.recycler.R;
import com.merlin.view.recycler.RecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/16.
 */

public class TestActivity extends Activity {

    private ArrayList<String> mDatas = new ArrayList<>();
    private HomeAdapter mAdapter;

    private int count = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        initData();
        final MRecyclerView mRecyclerView = (MRecyclerView) findViewById(R.id.mRecyclerView);
        mRecyclerView.setAdapter(mAdapter = new HomeAdapter(mDatas));
        mRecyclerView.setOnRefreshListener(new MRecyclerView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerView.refreshed();
                        mDatas.clear();
                        count = 100;
                        for (int i = 0; i < 20; i++) {
                            mDatas.add((count - i) + "");
                        }
                        mRecyclerView.setHasMore(count < 150);
                        mAdapter.notifyDataSetChanged();
                    }
                }, 3000);
            }
        });
        mRecyclerView.setOnLoadListener(new MRecyclerView.OnLoadListener() {
            @Override
            public void onLoad() {
                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerView.loaded();
                        for (int i = count; i < count + 20; i++) {
                            mDatas.add(i + "");
                        }
                        count = count + 20;
                        mRecyclerView.setHasMore(count < 150);
//                        mRecyclerView.setHasMore(false);
                        mAdapter.notifyDataSetChanged();
                    }
                }, 3000);
            }
        });
        TextView textView = new TextView(this);
        textView.setText("代码生成空页面");
        textView.setTextColor(0xff00cd00);
        mRecyclerView.setEmptyView(textView);

        mRecyclerView.addHeader(LayoutInflater.from(this).inflate(R.layout.m_progress_header, null));
        mRecyclerView.addHeader(LayoutInflater.from(this).inflate(R.layout.m_progress_header, null));
        mRecyclerView.addFooter(LayoutInflater.from(this).inflate(R.layout.m_progress_header, null));

        mRecyclerView.refresh();
    }

    protected void initData() {


    }

    class HomeAdapter extends AbstractRecyclerAdapter<String> {

        public HomeAdapter(List<String> mDataList) {
            super(mDataList);
        }

        @Override
        public int getItemResId(ViewGroup parent, int viewType) {
            return R.layout.test_item;
        }

        @Override
        public void onBindViewHolder(RecyclerViewHolder holder, int position) {
            TextView tv = holder.view(R.id.id_num);
            tv.setText(getData(position));
        }
    }
}
