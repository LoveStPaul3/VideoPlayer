# VideoPlayer
红岩半期考核 <br>
=======
关于功能的实现 <br>
---------
1.列表<br>
   （1）加载每个数据，就不贴图了=。=<br>
   （2）每个item显示视频部分具体信息····<br>
   （3）截取视频第一帧显示在item上  （完成）<br>
  ![](https://github.com/LoveStPaul3/VideoPlayer/blob/master/app/src/main/res/drawable/1.png)<br>
    （4）在列表播放时，`item划出后暂停，滑回来过后点播放可以从上次播放的地方继续播放。`（没做动态图）<br>
播放控制<br>
----
1.进度条，拖动改变视频进度<br>
  ![](https://github.com/LoveStPaul3/VideoPlayer/blob/master/app/src/main/res/drawable/2.png)<br>
2.开始，暂停，下一个&上一个      在此基础上增加了`点击播放视频的案件之外的区域会隐藏这些按钮，再次点击就会显示这些按钮`<br>
3.重力感应  切换横竖屏后，视频不会从头开始播放。<br>
  ![](https://github.com/LoveStPaul3/VideoPlayer/blob/master/app/src/main/res/drawable/8.png)<br>
扩展功能<br>
-----
1.播放进度保存，再次播放跳转到上次播放的地方<br>
2.播放记录，按时间排序<br>
  ![](https://github.com/LoveStPaul3/VideoPlayer/blob/master/app/src/main/res/drawable/6.png)<br>
 `只有在item点击了大按钮播放后，才算是播放，在视频中点击上下一集并没有记录在内`<br>
 3.视频下载到本机，且有进度条<br>
   ![](https://github.com/LoveStPaul3/VideoPlayer/blob/master/app/src/main/res/drawable/5.png)<br>
4.下到本地后再次播放使用本地数据。<br>
5.滑动删除播放记录`没做=。=` <br>
6.视频缓冲  进度条分为两种<br>
   ![](https://github.com/LoveStPaul3/VideoPlayer/blob/master/app/src/main/res/drawable/3.png)<br>
其他<br>
------
1.做了一定的封装，UI逻辑还好吧（自我感觉=。=）<br>
2.图片做了`三级`缓存。<br>
3.`遇见的一些BUG：`<br>
（1）第一次进入后点击播放在虚拟机上会崩，在真机上就没有。<br>
（2）进去比较卡，因为截取的第一帧是bitmap形式的，应该放在子线程进行的，但是错位的问题设置了tag也没有解决所以就没有放在线程中了。<br>
 （3）这次半期考核还是比较累，网上抄的代码基本自己都理解了，所以做的时候逻辑还是比较清楚，唯一在一个为题卡的比较久就是下载，<br>
 一开始没用自带的Download那个，是自己手撸的，自己撸服务啊，AnysTask，定义回调类呀。后面想清楚了没多少行代码就解决了。<br>
 （4）总的而言，感觉开学加入红岩是正确的选择，自己收获了许多东西，体会到努力奋斗的感觉，其实大一下才开始的时候跟组内的比起感觉差很多，<br>
 现在自己在慢慢进步还是挺不错的，嘿嘿！
