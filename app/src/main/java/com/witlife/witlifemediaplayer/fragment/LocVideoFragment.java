package com.witlife.witlifemediaplayer.fragment;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
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
import com.witlife.witlifemediaplayer.adapter.LocVideoAdapter;
import com.witlife.witlifemediaplayer.adapter.NetVideoAdapter;
import com.witlife.witlifemediaplayer.bean.MediaBean;
import com.witlife.witlifemediaplayer.http.BaseCallback;
import com.witlife.witlifemediaplayer.http.OkHttpHelper;
import com.witlife.witlifemediaplayer.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bruce on 14/08/2017.
 */

public class LocVideoFragment extends Fragment{

    private RecyclerView recyclerView;
    private LocVideoAdapter adapter;
    private List<MediaBean.TrailersBean> trailers;

    private ProgressBar pg_loading;
    private TextView tv_loading_text;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loc_video, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        pg_loading = (ProgressBar) view.findViewById(R.id.pg_loading);
        tv_loading_text = (TextView) view.findViewById(R.id.tv_loading_text);

        initData();
        return view;
    }

    private Handler handle = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(trailers != null && trailers.size() > 0){
                initRecyclerView();
                pg_loading.setVisibility(View.GONE);
                tv_loading_text.setVisibility(View.GONE);
            } else {
                pg_loading.setVisibility(View.VISIBLE);
                tv_loading_text.setVisibility(View.VISIBLE);
            }
        }
    };


    private void initData() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                trailers = new ArrayList<MediaBean.TrailersBean>();

                ContentResolver resolver = getContext().getContentResolver();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Video.Media.DISPLAY_NAME,
                        MediaStore.Video.Media.DURATION,
                        MediaStore.Video.Media.SIZE,
                        MediaStore.Video.Media.DATA,
                };

                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if(cursor != null){
                    while (cursor.moveToNext()){

                        MediaBean.TrailersBean trailer = new MediaBean.TrailersBean();
                        trailers.add(trailer);
                        trailer.setMovieName(cursor.getString(0));
                        trailer.setVideoLength(cursor.getLong(1));
                        //trailer.set
                        trailer.setUrl(cursor.getString(3));

                    }
                    cursor.close();
                }
                handle.sendEmptyMessage(100);
            }
        }.start();
    }

    private void initRecyclerView(){

        if (recyclerView != null) {
            LinearLayoutManager manager = new LinearLayoutManager(getContext());

            adapter = new LocVideoAdapter(getContext(), trailers);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(manager);

            DividerItemDecoration divider = new DividerItemDecoration(getContext(), manager.getOrientation());
            recyclerView.addItemDecoration(divider);
        }

    }
}
