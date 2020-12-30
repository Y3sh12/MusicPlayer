package com.example.musicplayer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.bean.MusicBean;

import java.util.List;

/**
 * Describe:
 * <p>后台绑定类MusicAdapter，将歌曲绑定在RecyclerView上</p>
 *
 * @author 鄧鄧
 * @Date 2020/12/24
 */
public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {


    protected Context context;
    protected List<MusicBean> musicList;//数据源

    public onItemClickListener onItemClickListener;

    public void setOnItemClickListener(MusicAdapter.onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    /**
     * RecyclerView子项点击事件接口
     */

    public interface onItemClickListener {
        public void onItemClick(View view, int position);
    }


    public MusicAdapter(Context context, List<MusicBean> musicList) {
        this.context = context;
        this.musicList = musicList;
    }

    /**
     * 渲染歌曲ViewHolder
     *
     * @param parent   ViewHolder的容器
     * @param viewType 可以据此实现渲染不同类型的ViewHolder
     * @return
     */
    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_music_list, parent, false);
        MusicViewHolder holder = new MusicViewHolder(view);
        return holder;
    }


    /**
     * 绑定ViewHolder的数据
     *
     * @param holder   viewHolder
     * @param position 数据源List的下标
     */
    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        MusicBean musicBean = musicList.get(position);
        //绑定歌曲名称
        holder.tv_music_name.setText(musicBean.getSong());
        //判断当前歌曲是否正在播放，显示不同图标
        boolean isPlay = musicBean.getPlay();
        holder.iv_music_icon.setTag(R.id.iv_music_icon, position);
        if (position == (int) holder.iv_music_icon.getTag(R.id.iv_music_icon) && isPlay) {
            holder.iv_music_icon.setBackgroundResource(R.drawable.ic_music_play);
        } else {
            holder.iv_music_icon.setBackgroundResource(R.drawable.ic_music_item);
        }
        //设置RecyclerView子项点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v,position);
            }
        });
    }


    /**
     * 决定元素的布局使用哪种类型
     *
     * @param position 数据源List的下标
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }


    /**
     * @return 数据条目的多少
     */
    @Override
    public int getItemCount() {

        Log.i("msg", "显示多少歌曲：" + musicList.size());
        return musicList.size();
    }


    /**
     * Describe:
     * <p>歌曲ViewHolder，layout_music_list布局文件中显示图标和歌曲名称</p>
     */
    public class MusicViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_music_icon;
        TextView tv_music_name;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_music_icon = itemView.findViewById(R.id.iv_music_icon);
            tv_music_name = itemView.findViewById(R.id.tv_music_name);
        }
    }


}


