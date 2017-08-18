package com.witlife.witlifemediaplayer;

import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.witlife.witlifemediaplayer.bean.Tab;
import com.witlife.witlifemediaplayer.fragment.LocAudioFragment;
import com.witlife.witlifemediaplayer.fragment.LocVideoFragment;
import com.witlife.witlifemediaplayer.fragment.NetAudioFragment;
import com.witlife.witlifemediaplayer.fragment.NetVideoFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FragmentTabHost tabHost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindView();

        initTab();
    }

    private void initTab() {

        Tab tab_local_video = new Tab("Local Video", LocVideoFragment.class, R.drawable.icon_video_selector);
        Tab tab_local_audio = new Tab("Local Audio", LocAudioFragment.class, R.drawable.icon_audio_selector);
        Tab tab_net_video = new Tab("Net Video", NetVideoFragment.class, R.drawable.icon_netvideo_selector);
        Tab tab_net_audio = new Tab("Net Audio", NetAudioFragment.class, R.drawable.icon_netaudio_selector);

        List<Tab> tabs = new ArrayList<>();
        tabs.add(tab_local_video);
        tabs.add(tab_local_audio);
        tabs.add(tab_net_video);
        tabs.add(tab_net_audio);

        tabHost.setup(this, getSupportFragmentManager(), R.id.fragContainer);

        for (Tab tab : tabs){
            TabHost.TabSpec tabSpec = tabHost.newTabSpec(tab.getTitle());

            tabSpec.setIndicator(buildIndicator(tab));
            tabHost.addTab(tabSpec, tab.getFragment(), null);
        }

        tabHost.setCurrentTab(0);
    }

    private View buildIndicator(Tab tab) {
        ImageView imageview;
        TextView tvTitle;

        View view = View.inflate(this, R.layout.layout_indicator, null);

        imageview = (ImageView) view.findViewById(R.id.imageview);
        tvTitle = (TextView)view.findViewById(R.id.tvTitle);
        imageview.setImageResource(tab.getIcon());
        tvTitle.setText(tab.getTitle());

        return view;
    }

    private void bindView() {
        tabHost = (FragmentTabHost) findViewById(R.id.tabHost);

    }
}
