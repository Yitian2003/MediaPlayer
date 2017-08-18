package com.witlife.witlifemediaplayer.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.witlife.witlifemediaplayer.activity.VideoPlayerActivity;
import com.witlife.witlifemediaplayer.bean.MediaBean;
import com.witlife.witlifemediaplayer.R;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bruce on 14/08/2017.
 */

public class NetVideoAdapter extends RecyclerView.Adapter<NetVideoAdapter.NetVideoViewHolder>{

    public static final String VIDEO_LIST = "video_list";
    private List<MediaBean.TrailersBean> trailers;
    private Context context;

    public NetVideoAdapter(Context context, List<MediaBean.TrailersBean> trailers) {
        this.trailers = trailers;
        this.context = context;
    }

    @Override
    public NetVideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = View.inflate(context, R.layout.item_net_video,null);

        return new NetVideoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NetVideoViewHolder holder, int position) {

        MediaBean.TrailersBean item = trailers.get(position);
        Picasso.with(context)
                .load(item.getCoverImg())
                .into(holder.imageView);
        holder.tvTitle.setText(item.getMovieName());
        holder.tvDescription.setText(item.getSummary());
    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }

    class NetVideoViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageView;
        public TextView tvTitle;
        public TextView tvDescription;

        public NetVideoViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView)itemView.findViewById(R.id.imageview);
            tvTitle = (TextView)itemView.findViewById(R.id.tvTitle);
            tvDescription = (TextView)itemView.findViewById(R.id.tv_description);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, VideoPlayerActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(VIDEO_LIST, (Serializable)trailers);
                    intent.putExtras(bundle);
                    intent.putExtra("position", getLayoutPosition());

                    context.startActivity(intent);
                }
            });
        }
    }
}
