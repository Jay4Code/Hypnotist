package com.lga.hypnotist;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;

import java.io.IOException;

public class PlayerService extends Service {

    private MediaPlayer mPlayer;
    private Handler mHandler;

    public PlayerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initMediaPlayer();

        mHandler = new Handler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mPlayer == null) stopSelf();

        boolean isPlaying = intent.getBooleanExtra(Constant.EXTRA_IS_PLAYING, false);
        if (isPlaying) {
            if (!mPlayer.isPlaying()) {
                mPlayer.start();
            }

            int duration = intent.getIntExtra(Constant.EXTRA_DURATION, Constant.DEFAULT_DURATION);
            mHandler.removeCallbacksAndMessages(null);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopSelf();
                }
            }, duration * Constant.MINUTE);
        } else {
            if (mPlayer.isPlaying()) {
                mPlayer.pause();

                mHandler.removeCallbacksAndMessages(null);
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            mPlayer.release();
        }

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    private void initMediaPlayer() {
        MediaPlayer player = new MediaPlayer();
        try {
            AssetManager assetManager = getAssets();
            AssetFileDescriptor afd = assetManager.openFd(Constant.MUSIC_FILE_NAME);
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            player.setLooping(true); // 循环播放
            player.prepare();
        } catch (IOException e) {
            player = null;
            e.printStackTrace();
        }
        mPlayer = player;
    }
}
