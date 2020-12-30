package com.example.musicplayer.bean;

import android.graphics.Bitmap;

/**
 * Describe:
 * <p>歌曲实体类</p>
 *
 * @author 鄧鄧
 * @Date 2020/12.24
 */
public class MusicBean {
    //歌名
    private String song;
    //作者
    private String singer;
    //专辑图片ID
    private int imageID;
    //专辑图片
    private Bitmap image;
    //时长
    private Long duration;
    //文件大小
    private Long size;
    //歌曲路径
    private String path;
    //是否正在播放
    private  Boolean isPlay = false;

    public MusicBean() {
    }

    public MusicBean(String song, String singer, int imageID, Bitmap image, Long duration, Long size, String path) {
        this.song = song;
        this.singer = singer;
        this.imageID = imageID;
        this.image = image;
        this.duration = duration;
        this.size = size;
        this.path = path;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getPlay() {
        return isPlay;
    }

    public void setPlay(Boolean play) {
        isPlay = play;
    }

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }
}
