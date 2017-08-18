package com.witlife.witlifemediaplayer.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.witlife.witlifemediaplayer.R;
import com.witlife.witlifemediaplayer.adapter.NetVideoAdapter;
import com.witlife.witlifemediaplayer.bean.MediaBean;
import com.witlife.witlifemediaplayer.bean.MediaItem;
import com.witlife.witlifemediaplayer.http.BaseCallback;
import com.witlife.witlifemediaplayer.http.OkHttpHelper;
import com.witlife.witlifemediaplayer.utils.Constants;

import java.util.List;

/**
 * Created by bruce on 14/08/2017.
 */

public class NetVideoFragment extends Fragment {

    private RecyclerView recyclerView;
    private NetVideoAdapter adapter;
    private OkHttpHelper okHttpHelper;
    private List<MediaBean.TrailersBean> trailers;

    private ProgressBar pg_loading;
    private TextView tv_loading_text;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_net_video, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        pg_loading = (ProgressBar) view.findViewById(R.id.pg_loading);
        tv_loading_text = (TextView) view.findViewById(R.id.tv_loading_text);

        getDataFromInternet();
        return view;
    }

    private void initRecyclerView(){

        if (recyclerView != null) {
            LinearLayoutManager manager = new LinearLayoutManager(getContext());

            adapter = new NetVideoAdapter(getContext(), trailers);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(manager);

            DividerItemDecoration divider = new DividerItemDecoration(getContext(), manager.getOrientation());
            recyclerView.addItemDecoration(divider);
        }

    }

    private void getDataFromInternet() {
        String url = Constants.NET_URL;

        okHttpHelper = OkHttpHelper.getInstance();

        okHttpHelper.httpGet(url, new BaseCallback<MediaBean>() {
            @Override
            public void onBeforeRequest(Request request) {

            }

            @Override
            public void onFailure(Request request, Exception e) {
                pg_loading.setVisibility(View.GONE);
                tv_loading_text.setText("Internet is down");
            }

            @Override
            public void onResponse(Response response) {

            }

            @Override
            public void onSuccess(Response response, MediaBean mediaBean) {

                pg_loading.setVisibility(View.GONE);
                tv_loading_text.setVisibility(View.GONE);

                trailers = mediaBean.getTrailers();
                initRecyclerView();
            }


            @Override
            public void onError(Response response, int code, Exception e) {
                pg_loading.setVisibility(View.GONE);
                tv_loading_text.setText("Error");
            }
        });


    }
}
