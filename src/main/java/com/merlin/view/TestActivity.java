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

import com.merlin.view.mrecycler.MRecyclerView;
import com.merlin.view.mrecycler.ProgressFooter;
import com.merlin.view.mrecycler.ProgressHeader;
import com.merlin.view.recycler.R;

import java.util.ArrayList;

import static java.security.AccessController.getContext;

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
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        mRecyclerView.addHeader(LayoutInflater.from(this).inflate(R.layout.m_progress_footer, null));
//        mRecyclerView.addHeader(LayoutInflater.from(this).inflate(R.layout.m_progress_footer, null));
//        mRecyclerView.addFooter(LayoutInflater.from(this).inflate(R.layout.m_progress_header, null));
        mRecyclerView.setAdapter(mAdapter = new HomeAdapter());
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
                        mAdapter.notifyDataSetChanged();
                    }
                }, 3000);
            }
        });
        mRecyclerView.refresh();
//        mRecyclerView.addHeader(new ProgressHeader(this));
//        mRecyclerView.addHeader(new BlankHeader(this));
//        mRecyclerView.addFooter(new ProgressHeader(this));
//        mRecyclerView.addFooter(new ProgressHeader(this));
//        mRecyclerView.addFooter(new ProgressHeader(this));
    }

    protected void initData() {


    }

    class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                    TestActivity.this).inflate(R.layout.test_item, parent,
                    false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.tv.setText(mDatas.get(position));
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tv;

            public MyViewHolder(View view) {
                super(view);
                tv = (TextView) view.findViewById(R.id.id_num);
            }
        }
    }
}
