package com.xuecheng.media.service;

import com.xuecheng.media.model.po.MediaProcess;

import java.util.List;

/**
* @description 媒资文件处理服务
* @author TMC
* @date 2023/2/13 16:00
* @version 1.0
*/
public interface MediaProcessService {

    /**
    * @description 查询待处理任务
    * @param shardTotal 分片总数
     * @param shardIndex 分片序号
     * @param count 查询记录条数
    * @return java.util.List<com.xuecheng.media.model.po.MediaProcess>
    * @author TMC
    * @date 2023/2/13 16:02
    */
    List<MediaProcess> queryMediaProcessList(int shardTotal, int shardIndex, int count);

    /**
    * @description 更新任务状态
    * @param taskId 任务id
     * @param status 任务状态
     * @param fileId 文件id
     * @param url url
     * @param errorMsg 错误信息
    * @return void
    * @author TMC
    * @date 2023/2/13 16:16
    */
    void saveMediaProcess(Long taskId, String status, String fileId, String url, String errorMsg);

}
