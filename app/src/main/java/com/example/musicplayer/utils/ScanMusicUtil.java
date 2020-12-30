package com.example.musicplayer.utils;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.example.musicplayer.bean.MusicBean;
import com.example.musicplayer.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Describe:
 * <p>工具类：扫描本地歌曲</p>
 *
 * @author 鄧鄧
 * @Date 2020/12/25
 */
public class ScanMusicUtil {


    /**
     * 扫描本地歌曲，将歌曲信息存储到list中
     *
     * @param context
     * @return list
     */
    public static List<MusicBean> getMusicList(Context context) {
        List<MusicBean> musicList = new ArrayList<>();
        //1.获取ContentResolver对象
        ContentResolver resolver = context.getApplicationContext().getContentResolver();
        //2.扫描本地的歌曲
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
        Cursor cursor = resolver.query(uri,null, null, null, sortOrder);
        //3.遍历cursor，将数据存入list
        if (cursor != null) {
            Log.i("msg","有数据的");
            while (cursor.moveToNext()) {
                String song = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));//歌名
                String singer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));//作者
                int album_id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));//专辑图片ID
                Bitmap image = getImage(context,album_id);//专辑图片
                Long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));//时长
                Long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));//文件大小
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));//路径
                int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));//是否为音乐
                if (isMusic != 0) {//如果是音乐保存到集合中
                    //创建MusicBean对象
                    MusicBean musicBean = new MusicBean(song,singer,album_id,image,duration,size,path);
                    musicList.add(musicBean);
                    Log.i("msg","歌曲"+musicList.size());
                }
            }
        }
        cursor.close();//关闭游标
        return musicList;
    }


    /**
     * 通过专辑ID获取专辑图片
     *
     * @param context
     * @param album_id 专辑ID
     * @return 专辑图片
     */
    public static Bitmap getImage(Context context, int album_id) {
        //通过专辑ID查找专辑图片
        String albumArt = getAlbumArt(context.getApplicationContext(), album_id);
        //如果没有图片，则使用默认图片
        if (albumArt != null) {
            return BitmapFactory.decodeFile(albumArt);
        } else {
            return BitmapFactory.decodeResource(context.getApplicationContext().getResources(), R.drawable.default_album_art);
        }
    }

    /**
     * 通过专辑ID查找专辑图片，如果找不到返回null
     *
     * @param album_id
     * @return
     */
    private static String getAlbumArt(Context context, int album_id) {
        ContentResolver resolver = context.getContentResolver();
        String uri = "content://media/external/audio/albums/"+album_id;
        String[] projection = new String[]{"album_art"};
        Cursor cursor = resolver.query(Uri.parse(uri),projection, null, null, null);
        String album_art = null;
        if (cursor.getCount() > 0 && cursor.getColumnCount() > 0) {
            cursor.moveToNext();
            album_art = cursor.getString(cursor.getColumnIndex("album_art"));

        }
        cursor.close();
        return album_art;
    }

    /**
     * 获取歌词
     *
     * @param fileName
     * @return
     */
    public static String getLrc(String fileName) {
        String lrcStr = null;
        try {
            InputStream is = new FileInputStream(new File(fileName));
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            lrcStr = new String(buffer, "gbk");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lrcStr;
    }

    /**
     * 时间格式化
     */
    public static String formatTime(int time) {
        if (time / 1000 % 60 < 10) {
            return (time / 1000 / 60) + ":0" + time / 1000 % 60;
        } else {
            return (time / 1000 / 60) + ":" + time / 1000 % 60;
        }
    }
}
