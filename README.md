# MusicPlayer
## 播放器功能
Android实现的简易版本地播放器，有音乐列表选择、播放和暂停、切歌三个功能。功能实现有：歌曲列表的数据绑定、音乐播放和暂停、切歌等功能。  

（1）音乐列表的数据绑定：通过RecyclerView和CardView控件实现音乐列表布局。使用ContentResolver来获取本地存储当中的所有的音频文件信息。  

（2）音乐播放功能的实现：通过调用MediaPlayer类实现音频的播放功能。在歌曲列表中选择歌曲，点击则开始播放，再次点击播放按钮，则暂停播放。
## 开发及运行环境
1. 开发环境：JDK1.8
### 2. 开发工具：Android Studio 3.5.2
### 3. Gradle 插件版本：3.5.2(gradle-5.4.1-all.zip)
### 4. 本项目需要在repositories 闭包里面引入：
maven { url 'https://maven.aliyun.com/repository/public' }
maven { url 'https://maven.aliyun.com/repository/google' }
maven { url 'https://maven.aliyun.com/repository/gradle-plugin' }
maven { url 'https://jitpack.io'}
### 5. 本项目需要在dependencies中引入：
//使用RecyclerView和CardView组件
implementation 'androidx.cardview:cardview:1.0.0'
implementation 'androidx.recyclerview:recyclerview:1.0.0'
//权限管理
implementation "io.reactivex.rxjava2:rxjava:2.2.19"
implementation "io.reactivex.rxjava2:rxandroid:2.1.1"
//rxpermissions 动听请求权限
implementation "com.github.tbruyelle:rxpermissions:0.10.2"
### 6. 运行环境：真机或者MuMu模拟器（需要开放存储权限）

## 总结
本项目是通过MediaStore类来扫描本地存储当中所有的音乐文件。理论上，路径为MediaStore.Audio.Media.EXTERNAL_CONTENT_URI是可以扫描到本地存储中所有的音乐文件，但是在MuMu模拟器中进行调试时，无法扫描到SD卡目录下的Music文件夹。当在MuMu模拟器中下载了网易云等音乐播放器，在其中下载了歌曲，重新加载本项目MusicPlayer，即可扫描到已下载的歌曲，自己手动添加到下载目录的音乐文件还是扫描不到的。但使用真机进行调试时，是可以扫描到本地存储中的所有音乐文件的，即使是自己手动添加的音乐文件也可以扫描得到。经过以上分析，最终得出结论：MuMu模拟器无法扫描到本地存储中的所有音乐文件，可能是MuMu模拟器没有开放存储权限所致。建议使用真机进行调试和运行。  

本项目在实现过程中，遇到了一些问题，如：使用RecyclerView和CardView控件需要导入相应的依赖包；本项目的Gradle 插件版本是gradle:3.5.2，在设置SD卡权限时，不仅需要静态注册，还需要进行动态注册才可以。当然，在实现过程中，最大的疑惑就是上述所说的本地音乐文件的获取。  

对出现的问题逐个击破后，实现了简易的本地音乐播放器，通过扫描本地存储中的音乐文件，获取音乐相关信息，对音乐列表页进行布局和渲染。选择列表中的歌曲进行播放、切歌。


