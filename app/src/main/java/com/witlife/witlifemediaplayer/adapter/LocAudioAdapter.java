package com.witlife.witlifemediaplayer.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.witlife.witlifemediaplayer.R;
import com.witlife.witlifemediaplayer.activity.VideoPlayerActivity;
import com.witlife.witlifemediaplayer.bean.MediaBean;
import com.witlife.witlifemediaplayer.bean.MediaItem;
import com.witlife.witlifemediaplayer.utils.Utils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bruce on 18/08/2017.
 */

public class LocAudioAdapter extends RecyclerView.Adapter<LocAudioAdapter.LocVideoViewHolder>{

    public static final String VIDEO_LIST = "video_list";

    private Context context;
    private List<MediaBean.TrailersBean> mediaItems;
    private Utils utils;

    public LocAudioAdapter(Context context, List<MediaBean.TrailersBean> mediaItems) {
        this.context = context;
        this.mediaItems = mediaItems;
        utils = new Utils();
    }

    @Override
    public LocVideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = View.inflate(context, R.layout.item_loc_audio, null);
        LocVideoViewHolder holder = new LocVideoViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(LocVideoViewHolder holder, int position) {
        MediaBean.TrailersBean item = mediaItems.get(position);

        holder.tvTitle.setText(item.getMovieName());
        holder.tvTime.setText(utils.stringForTime((int)item.getVideoLength()));
    }

    @Override
    public int getItemCount() {
        return mediaItems.size();
    }

    class LocVideoViewHolder extends RecyclerView.ViewHolder{

        public TextView tvTitle;
        public TextView tvTime;
        public TextView tvSize;

        public LocVideoViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView)itemView.findViewById(R.id.tvTitle);
            tvTime = (TextView)itemView.findViewById(R.id.tvTime);
            tvSize = (TextView)itemView.findViewById(R.id.tvSize);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, VideoPlayerActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(VIDEO_LIST, (Serializable) mediaItems);
                    intent.putExtras(bundle);
                    intent.putExtra("position", getAdapterPosition());
                    context.startActivity(intent);
                }
            });
        }
    }
}
