package com.lga.hypnotist;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.xw.repo.BubbleSeekBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity {

    private SharedPreferences mSpf;
    private boolean isPlaying;
    private Intent mServiceIntent;
    private int mDuration;

    @BindView(R.id.btn_player) Button mPlayerBtn;
    @BindView(R.id.sbar_duration) BubbleSeekBar mSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preprocess();
        initData();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = mSpf.edit();
        if (isPlaying) {
            editor.putBoolean(Constant.EXTRA_IS_PLAYING, isPlaying);
            editor.putInt(Constant.EXTRA_DURATION, mDuration);
        } else {
            editor.clear();
        }
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.exit) {
            if(mServiceIntent != null) stopService(mServiceIntent);

            isPlaying = false;

            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 预处理
     * 使用ButterKnife依赖注入
     */
    private void preprocess() {
        ButterKnife.bind(this);
    }

    /**
     * 初始化数据
     * 读取SharedPreferences数据
     */
    private void initData() {
        mSpf = getSharedPreferences(Constant.FILE_NAME, MODE_PRIVATE);
        isPlaying = mSpf.getBoolean(Constant.EXTRA_IS_PLAYING, false);
        mDuration = mSpf.getInt(Constant.EXTRA_DURATION, Constant.DEFAULT_DURATION);
    }

    /**
     * 初始化视图
     */
    private void initView() {
        mPlayerBtn.setText(isPlaying ? R.string.pause : R.string.start);

        mSeekBar.setProgress(mDuration);
        mSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {}

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {
                if(mServiceIntent == null) {
                    mServiceIntent = new Intent(MainActivity.this, PlayerService.class);
                }
                mDuration = progress;
                mServiceIntent.putExtra(Constant.EXTRA_DURATION, progress);
                startService(mServiceIntent);
            }

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {}
        });
    }

    /**
     * 设置单击监听事件
     * @param v view
     */
    @OnClick(R.id.btn_player)
    public void playOrPause(View v) {
        isPlaying = !isPlaying;
        mPlayerBtn.setText(isPlaying ? R.string.pause : R.string.start);

        if(mServiceIntent == null) {
            mServiceIntent = new Intent(MainActivity.this, PlayerService.class);
        }
        mServiceIntent.putExtra(Constant.EXTRA_IS_PLAYING, isPlaying);
        startService(mServiceIntent);
    }
}
