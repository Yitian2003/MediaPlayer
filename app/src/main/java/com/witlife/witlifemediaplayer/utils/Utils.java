package com.witlife.witlifemediaplayer.utils;

import android.content.Context;
import android.net.TrafficStats;
import android.os.Handler;
import android.provider.Settings;

import java.util.Formatter;
import java.util.Locale;

/**
 * Created by bruce on 17/08/2017.
 */

public class Utils {

    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;

    private long lastTotalRxBytes = 0;
    private long lastTimeStamp = 0;

    public Utils (){
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
    }

    public String stringForTime(int timeMs){
        int totalSeconds = timeMs /1000;
        int seconds = totalSeconds % 60;

        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds /3600;

        mFormatBuilder.setLength(0);

        if(hours > 0){
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    public boolean isNetUri(String uri) {

        //boolean result = false;

        if(uri != null){
            if(uri.toLowerCase().startsWith("http") || uri.toLowerCase().startsWith("rtsp") || uri.toLowerCase().startsWith("mms")){
                return true;
            }
        }
        return false;
    }

    public String getNetSpeed(Context context) {
        String netSpeed = "0 kb/s";
        long nowTotalRxBytes = TrafficStats.getUidRxBytes(context.getApplicationInfo().uid)==TrafficStats.UNSUPPORTED ? 0 :(TrafficStats.getTotalRxBytes()/1024);//转为KB;
        long nowTimeStamp = System.currentTimeMillis();
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换

        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;
        netSpeed  = String.valueOf(speed) + " kb/s";
        return netSpeed;
    }
}
