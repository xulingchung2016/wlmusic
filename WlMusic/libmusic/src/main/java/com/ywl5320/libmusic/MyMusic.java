package com.ywl5320.libmusic;

import android.text.TextUtils;
import android.util.Log;

import com.ywl5320.bean.TimeBean;
import com.ywl5320.listener.OnCompleteListener;
import com.ywl5320.listener.OnErrorListener;
import com.ywl5320.listener.OnInfoListener;
import com.ywl5320.listener.OnLoadListener;
import com.ywl5320.listener.OnParparedListener;
import com.ywl5320.listener.OnPauseResumeListener;

/**
 * Created by ywl on 2018-1-7.
 */

public class MyMusic {

    private static String source;
    private static TimeBean timeBean;
    private static int duration = 0;
    private static boolean playNext = false;
    private static OnParparedListener onParparedListener;
    private static OnErrorListener onErrorListener;
    private static OnLoadListener onLoadListener;
    private static OnInfoListener onInfoListener;
    private static OnCompleteListener onCompleteListener;
    private static OnPauseResumeListener onPauseResumeListener;

    public void setSource(String source) {
        this.source = source;
    }

    public void setOnParparedListener(OnParparedListener onParparedListener) {
        this.onParparedListener = onParparedListener;
    }

    public void setOnErrorListener(OnErrorListener onErrorListener) {
        this.onErrorListener = onErrorListener;
    }

    public void setOnLoadListener(OnLoadListener onLoadListener) {
        this.onLoadListener = onLoadListener;
    }

    public void setOnInfoListener(OnInfoListener onInfoListener) {
        this.onInfoListener = onInfoListener;
    }

    public void setOnCompleteListener(OnCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
    }

    public void setOnPauseResumeListener(OnPauseResumeListener onPauseResumeListener) {
        this.onPauseResumeListener = onPauseResumeListener;
    }

    public void parpared()
    {
        if(TextUtils.isEmpty(source))
        {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                n_parpared(source);
            }
        }).start();
    }

    public void playNext(String source)
    {
        playNext = true;
        this.source = source;
        stop();
    }

    public void setPlayNext(boolean playNext) {
        this.playNext = playNext;
    }

    public void start()
    {
        if(timeBean == null)
        {
            timeBean = new TimeBean();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                n_start();
            }
        }).start();
    }

    public void pause()
    {
        n_pause();
        if(onPauseResumeListener != null)
        {
            onPauseResumeListener.onPause(true);
        }
    }

    public void resume()
    {
        n_resume();
        if(onPauseResumeListener != null)
        {
            onPauseResumeListener.onPause(false);
        }
    }

    public void stop()
    {
        timeBean = null;
        new Thread(new Runnable() {
            @Override
            public void run() {
                n_stop();
                if(playNext)
                {
                    playNext = false;
                    parpared();
                }
            }
        }).start();
    }

    public void seek(final int secds)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                n_seek(secds);
            }
        }).start();
    }

    public int getDuration()
    {
        return duration;
    }

    private void onCallParpared()
    {
        if(onParparedListener != null)
        {
            onParparedListener.onParpared();
        }
    }

    private void onCallError(int code, String msg)
    {
        if(onErrorListener != null)
        {
            onErrorListener.onError(code, msg);
        }
    }

    private void onCallLoad(boolean load)
    {
        if(onLoadListener != null)
        {
            onLoadListener.onLoad(load);
        }
    }

    private void onCallInfo(int currSec, int totalSec)
    {
        if(onInfoListener != null && timeBean != null)
        {
            timeBean.setCurrSecs(currSec);
            timeBean.setTotalSecs(totalSec);
            duration = totalSec;
            onInfoListener.onInfo(timeBean);
        }
    }

    private void onCallComplete()
    {
        if(onCompleteListener != null)
        {
            onCallInfo(duration, duration);
            onCompleteListener.onComplete();
            stop();
        }
    }




    private native void n_parpared(String source);
    private native void n_start();
    private native void n_pause();
    private native void n_resume();
    private native int n_stop();
    private native void n_seek(int secds);

    static {
        System.loadLibrary("avutil-55");
        System.loadLibrary("swresample-2");
        System.loadLibrary("avcodec-57");
        System.loadLibrary("avformat-57");
        System.loadLibrary("swscale-4");
        System.loadLibrary("postproc-54");
        System.loadLibrary("avfilter-6");
        System.loadLibrary("avdevice-57");
        System.loadLibrary("wlmusic");
    }
}
