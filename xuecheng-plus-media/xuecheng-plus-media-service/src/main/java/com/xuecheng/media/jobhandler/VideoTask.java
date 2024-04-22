package com.xuecheng.media.jobhandler;

import com.xuecheng.base.info.Dictionary;
import com.xuecheng.base.utils.Mp4VideoUtil;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaFileService;
import com.xuecheng.media.service.MediaProcessService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
* @description 视频编码转换任务类
* @author TMC
* @date 2023/2/13 21:38
* @version 1.0
*/
@Slf4j
@Component
public class VideoTask {

    @Resource
    MediaProcessService mediaProcessService;

    @Resource
    MediaFileService mediaFileService;

    @Value("${videoprocess.ffmpegpath}")
    String ffmpegpath;

    @XxlJob("videoJobHandler")
    public void  videoJobHandler() throws Exception {

        //1.查询待处理任务,一次处理任务数和cpu核心数一样
        int shardIndex = XxlJobHelper.getShardIndex(); //分片序号,从0开始
        int shardTotal = XxlJobHelper.getShardTotal(); //分片总数
        List<MediaProcess> mediaProcessList = mediaProcessService.queryMediaProcessList(shardTotal, shardIndex, 2);
        if (mediaProcessList == null || mediaProcessList.size() == 0) {
            log.error("待处理视频任务不存在");
            return;
        }

        //2.创建线程池与计数器
        int size = mediaProcessList.size(); //待处理的任务数
        ExecutorService threadPool = Executors.newFixedThreadPool(size); //线程池
        CountDownLatch countDownLatch = new CountDownLatch(size); //计数器

        //3.遍历任务集并放入线程池执行
        mediaProcessList.forEach(mediaProcess -> threadPool.execute(()->{
            //3.1.保持任务幂等性
            if (Dictionary.MediaProcess.Status_SUCCESS.getCode().equals(mediaProcess.getStatus())) {
                log.error("已处理视频不需重复处理,视频信息:{}", mediaProcess);
                countDownLatch.countDown();
                return;
            }

            //3.2.待处理视频信息
            String bucket = mediaProcess.getBucket();
            String objectName = mediaProcess.getFilePath();
            String fileId = mediaProcess.getFileId();

            //3.3.创建视频处理所需临时文件
            File origin; //将待处理视频下载到服务器
            File mp4File; //处理后的视频文件
            try {
                origin = File.createTempFile("origin", null);
                mp4File = File.createTempFile("mp4", ".mp4");
            }catch (Exception e) {
                log.error("处理视频前创建临时文件失败");
                countDownLatch.countDown();
                return;
            }

            //3.4.下载原始视频文件
            try {
                mediaFileService.downloadFromMinio(origin, bucket, objectName);
            }catch (Exception e) {
                log.error("下载待处理视频:{},出错:{}", objectName, e.getMessage());
                countDownLatch.countDown();
                return;
            }

            //3.5.视频编码转换
            String origin_path = origin.getAbsolutePath(); //待处理视频路径
            String mp4_name = mp4File.getName(); //转码后mp4文件名称
            String mp4_path = mp4File.getAbsolutePath(); //转码后mp4文件路径
            String processResult;
            try {
                Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpegpath, origin_path, mp4_name, mp4_path);
                processResult = videoUtil.generateMp4();
            } catch (Exception e) {
                log.error("处理视频文件:{},出错:{}", origin_path, e.getMessage());
                countDownLatch.countDown();
                return;
            }

            //3.6.上传编码转换后的视频
            String statusNew = Dictionary.MediaProcess.Status_FAILURE.getCode();
            String url = null;
            String mp4ObjectName = this.getFilePath(fileId, ".mp4");
            if ("success".equals(processResult)) {
                try {
                    mediaFileService.uploadToMinio(mp4_path, bucket, mp4ObjectName);
                    statusNew = Dictionary.MediaProcess.Status_SUCCESS.getCode();
                    url = "/" + bucket + "/" + mp4ObjectName;
                }catch (Exception e) {
                    log.error("上传已处理视频:{},出错:{}", mp4_path, e.getMessage());
                    countDownLatch.countDown();
                    return;
                }
            }

            //3.7.记录任务处理结果
            try {
                mediaProcessService.saveMediaProcess(mediaProcess.getId(), statusNew, fileId, url, processResult);
            }catch (Exception e) {
                log.error("保存任务处理结果出错:{}", e.getMessage());
                countDownLatch.countDown();
                return;
            }

            //3.8.计数器减去1,退出线程
            countDownLatch.countDown();
        }));


        //3.阻塞到任务执行完成,当countDownLatch计数器归零，这里的阻塞解除
        //3.等待,给一个充裕的超时时间,防止无限等待，到达超时时间还没有处理完成则结束任务
        countDownLatch.await(30, TimeUnit.MINUTES);
    }

    /**
    * @description 根据md5和拓展名获取文件在MinIO存储位置
    * @param fileMd5 文件md5
     * @param extension 文件拓展名
    * @return java.lang.String
    * @author TMC
    * @date 2023/3/4 22:13
    */
    private String getFilePath(String fileMd5, String extension) {

        return fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/" + fileMd5 + extension;

    }


}
