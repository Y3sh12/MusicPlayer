package com.example.musicplayer;


import android.Manifest;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.adapter.MusicAdapter;
import com.example.musicplayer.bean.MusicBean;
import com.example.musicplayer.utils.ScanMusicUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //声明变量
    private List<MusicBean> musicList = new ArrayList<>();//数据源（歌曲）
    private MusicAdapter musicAdapter;//歌曲适配器

    private MediaPlayer mediaPlayer;
    //    private MusicPlayHelper musicPlayHelper = new MusicPlayHelper();
    //记录当前正在播放的音乐的位置
    private int pos = -1;
    //记录暂停时音乐进度条的位置
    private int pausePos = 0;

    private Timer timer;

    private boolean isSeekBarChanging;//互斥变量，防止进度条与定时器冲突

    private RecyclerView rv_music_list;
    //当前播放音乐
    private TextView tv_current_song;
    //3个按钮
    private ImageView iv_last, iv_play, iv_next;
    //1个进度条
    private SeekBar sb_bar;
    //时间文本
    TextView tv_current, tv_total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mediaPlayer = new MediaPlayer();
        boolean rxPermissions = requestRxPermissions();
        if (rxPermissions) {
            //有权限啦
            renderMusicList();
            //设置RecyclerView子项点击事件
            setEvenListener();
        }


    }

    /**
     * 设置RecyclerView子项点击事件
     */
    private void setEvenListener() {
        musicAdapter.setOnItemClickListener(new MusicAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                clearIsPlay();
                pos = position;
                //获取当前播放的歌曲对象
                MusicBean musicBean = musicList.get(position);
                setMusicPlayIcon(musicBean,true);
                playMusicByMusic(musicBean);
                //设置sb_bar的最大值
                sb_bar.setMax(mediaPlayer.getDuration());
                getCurrentTime();
                tv_total.setText(ScanMusicUtil.formatTime(mediaPlayer.getDuration()));
            }
        });
    }

    /**
     * 遍历list，将isPlay属性置为false
     */
    private void clearIsPlay(){
        for(MusicBean musicBean:musicList){
            musicBean.setPlay(false);
        }
    }
    //监听播放时回调函数
    private void getCurrentTime(){
        timer = new Timer();
        timer.schedule(new TimerTask() {

            Runnable updateUI = new Runnable() {
                @Override
                public void run() {
                    tv_current.setText(ScanMusicUtil.formatTime(mediaPlayer.getCurrentPosition()));
                }
            };
            @Override
            public void run() {
                if(!isSeekBarChanging){
                    //将sb_bar位置设置为当前播放位置
                    sb_bar.setProgress(mediaPlayer.getCurrentPosition());
                    runOnUiThread(updateUI);
                }
            }
        },0,50);
    }
    /**
     * 根据传入对象播放音乐
     */
    private void playMusicByMusic(MusicBean musicBean) {
        tv_current_song.setText("正在播放：" + musicBean.getSong());
        setMusicPlayIcon(musicBean,false);
        stopMusic();
        //继续播放前重置mediaPlayer
        mediaPlayer.reset();
        //设置新的播放路径
        try {
            mediaPlayer.setDataSource(musicBean.getPath());
            setMusicPlayIcon(musicBean,true);
            playMusic();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置播放图标
     */
    private void setMusicPlayIcon(MusicBean musicBean,boolean isPlay){
        musicBean.setPlay(isPlay);
        musicAdapter.notifyDataSetChanged();
    }


    /**
     * 播放音乐/继续播放
     */
    private void playMusic() {
        Log.i("msg","正在播放?");
        if(mediaPlayer!=null && !mediaPlayer.isPlaying()){

            Log.i("msg","正在播放");

            if(pausePos==0) {
                //从头播放
                try {
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                //继续播放
                mediaPlayer.seekTo(pausePos);
                mediaPlayer.start();
            }
            iv_play.setImageResource(R.drawable.ic_pause);

        }
    }

    /**
     * 暂停音乐
     */
    private void pauseMusic() {
        if(mediaPlayer!=null && mediaPlayer.isPlaying()){
            pausePos = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            iv_play.setImageResource(R.drawable.ic_play);
        }
    }

    /**
     * 停止音乐
     */
    private void stopMusic() {
        if (mediaPlayer != null) {
            pausePos = 0;
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
            mediaPlayer.stop();
            iv_play.setImageResource(R.drawable.ic_pause);
        }
    }

    /**
     * 请求读写权限
     *
     * @return
     */
    public boolean requestRxPermissions() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).subscribe(aBoolean -> {
            if (!aBoolean) {
                Toast.makeText(this, "缺少存储权限，将会导致部分功能无法使用", Toast.LENGTH_SHORT).show();
                return;
            }
        });
        return true;
    }

    /**
     * 渲染音乐列表
     */
    private void renderMusicList() {
        Log.i("msg", "我有权限的");
        //初始化控件
        initView();
        //加载数据源
        musicList = ScanMusicUtil.getMusicList(this);
        Log.i("msg", "有多少数据：" + musicList.size());
        //创建歌曲适配器对象
        musicAdapter = new MusicAdapter(this, musicList);
        //设置适配器
        rv_music_list.setAdapter(musicAdapter);
        //创建布局管理器对象，规定布局展示形式：列表形式，垂直滑动，不翻转
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        //设置布局管理器
        rv_music_list.setLayoutManager(layoutManager);
        //数据源发生变化，提示适配器更新
        musicAdapter.notifyDataSetChanged();

        Log.i("msg", "有歌啦");
    }


    /**
     * 初始化控件
     */
    private void initView() {
        //音乐列表页
        rv_music_list = findViewById(R.id.rv_music_list);

        tv_current_song = findViewById(R.id.tv_current_song);
        //3个按钮
        iv_last = findViewById(R.id.iv_last);
        iv_play = findViewById(R.id.iv_play);
        iv_next = findViewById(R.id.iv_next);
        //进度条
        sb_bar = findViewById(R.id.sb_bar);
        //时间文本
        tv_current = findViewById(R.id.tv_current);
        tv_total = findViewById(R.id.tv_total);
        //设置按钮监听
        iv_next.setOnClickListener(this);
        iv_play.setOnClickListener(this);
        iv_last.setOnClickListener(this);
        //设置sb_bar监听
        sb_bar.setOnSeekBarChangeListener(new MySeekBar());
    }



    /**
     * 按钮的点击事件绑定
     */
    @Override
    public void onClick(View v) {
        MusicBean currentMusic = musicList.get(pos);
        switch (v.getId()){
            case R.id.iv_last:
                if(pos == 0){
                    Toast.makeText(this,"已经是第一首了，没有上一曲！",Toast.LENGTH_SHORT).show();
                    return;
                }
                pos = pos-1;
                musicList.get(pos);
                MusicBean lastMusic = musicList.get(pos);
                setMusicPlayIcon(lastMusic,true);
                setMusicPlayIcon(currentMusic,false);
                playMusicByMusic(lastMusic);
                break;
            case R.id.iv_next:
                if(pos == musicList.size()-1){
                    Toast.makeText(this,"已经是最后一首了，没有下一曲！",Toast.LENGTH_SHORT).show();
                    return;
                }
                pos = pos+1;
                musicList.get(pos);
                MusicBean nextMusic = musicList.get(pos);
                setMusicPlayIcon(nextMusic,true);
                setMusicPlayIcon(currentMusic,false);
                playMusicByMusic(nextMusic);
                break;
            case R.id.iv_play:
                if(pos==-1){
                    Toast.makeText(this,"请选择想要播放的音乐",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mediaPlayer.isPlaying()){
                    //此时处于播放状态，需要暂停音乐
                    setMusicPlayIcon(currentMusic,false);
                    pauseMusic();
                }else {
                    //此时没有播放，点击播放音乐
                    setMusicPlayIcon(currentMusic,true);
                    playMusic();
                }
                break;
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMusic();
        isSeekBarChanging = true;
        if (timer != null){
            timer.cancel();
            timer = null;
        }
    }


    /*进度条处理*/
    public class MySeekBar implements SeekBar.OnSeekBarChangeListener {

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        }

        /*滚动时,应当暂停后台定时器*/
        public void onStartTrackingTouch(SeekBar seekBar) {
            isSeekBarChanging = true;
        }
        /*滑动结束后，重新设置值*/
        public void onStopTrackingTouch(SeekBar seekBar) {
            isSeekBarChanging = false;
            mediaPlayer.seekTo(seekBar.getProgress());
        }
    }
}
