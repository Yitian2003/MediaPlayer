package com.witlife.witlifemediaplayer.bean;

import android.support.v4.app.Fragment;

/**
 * Created by bruce on 14/08/2017.
 */

public class Tab {

    private String title;
    private Class fragment;
    private int icon;

    public Tab(String title, Class fragment, int icon) {
        this.title = title;
        this.fragment = fragment;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Class getFragment() {
        return fragment;
    }

    public void setFragment(Class fragment) {
        this.fragment = fragment;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
