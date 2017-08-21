package com.witlife.witlifemediaplayer.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.witlife.witlifemediaplayer.R;
import com.witlife.witlifemediaplayer.bean.MediaBean;
import com.witlife.witlifemediaplayer.utils.Utils;

import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.*;
import io.vov.vitamio.MediaPlayer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by bruce on 15/08/2017.
 */

public class VitamioVideoPlayerActivity extends AppCompatActivity {

    public static final String VIDEO_LIST = "video_list";
    public static final int PROGRESS = 1;
    public static final int HIDE_CONTROLLER = 2;
    public static final int SHOW_SPEED = 3;

    private VideoView videoView;
    private LinearLayout ll_loading;
    private LinearLayout ll_buffer;
    private RelativeLayout rl_controller;
    private TextView tvLoadingText;
    private TextView tvBufferingText;
    private TextView tvTitle;
    private TextView tvTime;
    private TextView tv_process_time;
    private TextView tv_time_totle;
    private ImageView iv_battery;
    private Button btn_voice;
    private Button btn_info;
    private Button btn_exit;
    private Button btn_video_pre;
    private Button btn_video_start_pause;
    private Button btn_video_next;
    private Button btn_video_switch_screen;
    private SeekBar seekbar_time;
    private SeekBar seekbar_voice;

    private List<MediaBean.TrailersBean> trailers;
    private int position;

    private AudioManager am;
    private int currentVolume;
    private int maxVolume;
    private boolean isMute = false;
    private boolean isShowMediaController = false;

    private Utils utils;
    private GestureDetector detector;

    private boolean isNetUri;
    private boolean isUnder17 = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //hide status bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Vitamio.isInitialized(this);

        setContentView(R.layout.activity_vitamio_video_player);

        bindView();
        setFullScreen();
        initData();

        setListener();

        setStatusBar();

        setVolume();

        hideController();

    }

    private int preProgress;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case PROGRESS:
                    int currentProgress = (int) videoView.getCurrentPosition();
                    seekbar_time.setProgress(currentProgress);

                    tv_process_time.setText(utils.stringForTime(currentProgress));
                    Date currentTime = Calendar.getInstance().getTime();
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                    String timeString = format.format(currentTime);
                    tvTime.setText(timeString);

                    if(isNetUri) {
                        int buffer = videoView.getBufferPercentage();
                        int totalBuffer = buffer * seekbar_time.getMax();
                        int secondaryProgress = totalBuffer / 100;
                        seekbar_time.setSecondaryProgress(secondaryProgress);
                    } else {
                        seekbar_time.setProgress(0);
                    }

                    if(isUnder17 && videoView.isPlaying()) {
                        if (currentProgress - preProgress < 500) {
                            ll_buffer.setVisibility(View.VISIBLE);
                        } else {
                            ll_buffer.setVisibility(View.GONE);
                        }
                    } else {
                        ll_buffer.setVisibility(View.GONE);
                    }
                    preProgress = currentProgress;

                    handler.removeMessages(PROGRESS);
                    handler.sendEmptyMessageDelayed(PROGRESS, 1000);

                    break;

                case HIDE_CONTROLLER:
                    hideController();
                    isShowMediaController = false;
                    break;

                case SHOW_SPEED:
                    String netSpeed = utils.getNetSpeed(VitamioVideoPlayerActivity.this);

                    tvLoadingText.setText("Loading..." + netSpeed);
                    Log.e("net speed:", netSpeed);
                    tvBufferingText.setText("Buffering..." + netSpeed);

                    handler.removeMessages(SHOW_SPEED);
                    handler.sendEmptyMessageDelayed(SHOW_SPEED, 2000);

                    break;
            }
        }
    };

    private void hideController() {
        rl_controller.setVisibility(View.GONE);
        isShowMediaController = false;
    }

    private void showController() {
        rl_controller.setVisibility(View.VISIBLE);
        isShowMediaController = true;
    }

    private void setVolume() {

        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        seekbar_voice.setProgress(currentVolume);
        seekbar_voice.setMax(maxVolume);
    }

    private void bindView() {
        videoView = (VideoView) findViewById(R.id.videoview);
        ll_loading = (LinearLayout) findViewById(R.id.loading);
        ll_buffer = (LinearLayout) findViewById(R.id.buffer);
        rl_controller = (RelativeLayout) findViewById(R.id.controller);
        tvLoadingText = (TextView) findViewById(R.id.tvLoadingText);
        tvBufferingText = (TextView) findViewById(R.id.tvBufferingText);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tv_process_time = (TextView) findViewById(R.id.tv_process_time);
        tv_time_totle = (TextView) findViewById(R.id.tv_time_totle);
        btn_voice = (Button) findViewById(R.id.btn_voice);
        btn_info = (Button) findViewById(R.id.btn_info);
        btn_exit = (Button) findViewById(R.id.btn_exit);
        btn_video_pre = (Button) findViewById(R.id.btn_video_pre);
        btn_video_start_pause = (Button) findViewById(R.id.btn_video_start_pause);
        btn_video_next = (Button) findViewById(R.id.btn_video_next);
        btn_video_switch_screen = (Button) findViewById(R.id.btn_video_siwch_screen);
        iv_battery = (ImageView) findViewById(R.id.iv_battery);
        seekbar_time = (SeekBar) findViewById(R.id.seekbar_time);
        seekbar_voice = (SeekBar) findViewById(R.id.seekbar_voice);

    }

    private void setStatusBar() {

        tvTitle.setText(trailers.get(position).getMovieName());

        //setup battery
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int level = intent.getIntExtra("level", 0);
                setBattery(level);
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, intentFilter);
    }

    private void setBattery(int level) {
        if (level <= 0) {
            iv_battery.setImageResource(R.drawable.ic_battery_0);
        } else if (level <= 10) {
            iv_battery.setImageResource(R.drawable.ic_battery_10);
        } else if (level <= 20) {
            iv_battery.setImageResource(R.drawable.ic_battery_20);
        } else if (level <= 40) {
            iv_battery.setImageResource(R.drawable.ic_battery_40);
        } else if (level <= 60) {
            iv_battery.setImageResource(R.drawable.ic_battery_60);
        } else if (level <= 80) {
            iv_battery.setImageResource(R.drawable.ic_battery_80);
        } else if (level <= 100) {
            iv_battery.setImageResource(R.drawable.ic_battery_100);
        } else {
            iv_battery.setImageResource(R.drawable.ic_battery_100);
        }
    }

    private void setFullScreen() {

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) videoView.getLayoutParams();
        params.width = metrics.widthPixels;
        params.height = metrics.heightPixels;
        videoView.setLayoutParams(params);
    }

    private void setListener() {

        // listen to touch event
        detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (isShowMediaController) {
                    hideController();
                    handler.removeMessages(HIDE_CONTROLLER);
                } else {
                    showController();
                    handler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 5000);
                }

                return super.onSingleTapConfirmed(e);

            }
        });

        //listen to buttons
        btn_video_start_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                    btn_video_start_pause.setBackgroundResource(R.drawable.btn_video_play_selector);
                } else {
                    videoView.start();
                    btn_video_start_pause.setBackgroundResource(R.drawable.btn_video_pause_selector);
                }
                handler.removeMessages(HIDE_CONTROLLER);
                handler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 5000);
            }
        });

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_video_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (trailers != null && position > 0) {
                    position--;

                    ll_loading.setVisibility(View.VISIBLE);
                    tvTitle.setText(trailers.get(position).getMovieName());
                    isNetUri = utils.isNetUri(trailers.get(position).getUrl());
                    videoView.setVideoPath(trailers.get(position).getUrl());
                    seekbar_time.setProgress(0);
                } else {
                    Toast.makeText(VitamioVideoPlayerActivity.this, "This is the first Video.", Toast.LENGTH_LONG).show();
                }
                handler.removeMessages(HIDE_CONTROLLER);
                handler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 5000);
            }
        });

        btn_video_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position < trailers.size() - 1) {
                    position++;

                    ll_loading.setVisibility(View.VISIBLE);
                    tvTitle.setText(trailers.get(position).getMovieName());
                    isNetUri = utils.isNetUri(trailers.get(position).getUrl());
                    videoView.setVideoPath(trailers.get(position).getUrl());
                    seekbar_time.setProgress(0);
                } else {
                    Toast.makeText(VitamioVideoPlayerActivity.this, "This is the last Video.", Toast.LENGTH_LONG).show();
                }
                handler.removeMessages(HIDE_CONTROLLER);
                handler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 5000);
            }
        });

        // volume button listener event
        btn_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isMute = !isMute;
                updateVolume(currentVolume, isMute);
                handler.removeMessages(HIDE_CONTROLLER);
                handler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 5000);
            }
        });

        //volume seekbar
        seekbar_voice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                if (fromUser) {
                    if (i > 0){
                        isMute = false;
                    } else {
                        isMute = true;
                    }
                    updateVolume(i, isMute);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeMessages(HIDE_CONTROLLER);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 5000);
            }
        });

        //progress seekbar
        seekbar_time.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    videoView.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeMessages(HIDE_CONTROLLER);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 5000);
            }
        });

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                if (ll_loading.isShown()) {
                    ll_loading.setVisibility(View.GONE);
                }

                videoView.start();

                int duration = (int) videoView.getDuration();
                seekbar_time.setProgress(0);
                seekbar_time.setMax(duration);
                tv_time_totle.setText(utils.stringForTime(duration));

                hideController();
                handler.sendEmptyMessage(PROGRESS);
            }
        });

        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                Toast.makeText(VitamioVideoPlayerActivity.this, "Error", Toast.LENGTH_LONG).show();
                return false;
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

            }
        });

        if (isUnder17){

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
                        switch (i) {
                            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                                ll_buffer.setVisibility(View.VISIBLE);
                                break;
                            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                                ll_buffer.setVisibility(View.GONE);
                                break;
                        }

                        return true;
                    }
                });
            }
        }

    }

    private void updateVolume(int progress, boolean isMute) {
        if (isMute) {
            seekbar_voice.setEnabled(false);
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            btn_voice.setBackgroundResource(R.drawable.btn_voice_normal_disable);
        } else {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
            seekbar_voice.setEnabled(true);
            seekbar_voice.setProgress(progress);
            currentVolume = progress;
            btn_voice.setBackgroundResource(R.drawable.btn_voice_selector);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handler.removeMessages(HIDE_CONTROLLER);
                break;
        }
        return super.onTouchEvent(event);
    }

    private void initData() {

        handler.sendEmptyMessage(SHOW_SPEED);

        utils = new Utils();

        Intent intent = getIntent();

        if (intent != null) {
            trailers = (List<MediaBean.TrailersBean>) getIntent().getSerializableExtra(VIDEO_LIST);
            position = getIntent().getIntExtra("position", 0);
        }

        if(trailers != null && trailers.size() > 0){
            MediaBean.TrailersBean item = trailers.get(position);
            isNetUri = utils.isNetUri(item.getUrl());
            videoView.setVideoPath(item.getUrl());
        }
        //videoView.setVideoURI(Uri.parse(trailers.get(position).getUrl()));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == event.KEYCODE_VOLUME_DOWN){
            currentVolume--;
            updateVolume(currentVolume, false);
            handler.removeMessages(HIDE_CONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 5000);
            return true;
        } else if(keyCode == event.KEYCODE_VOLUME_UP) {
            currentVolume++;
            updateVolume(currentVolume, false);
            handler.removeMessages(HIDE_CONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 5000);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
