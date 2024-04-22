package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xuecheng.base.exception.XcException;
import com.xuecheng.base.info.Dictionary;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.mapper.MediaProcessHistoryMapper;
import com.xuecheng.media.mapper.MediaProcessMapper;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.model.po.MediaProcessHistory;
import com.xuecheng.media.service.MediaProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
* @description 媒资文件处理业务接口实现类
* @author TMC
* @date 2023/2/13 16:04
* @version 1.0
*/
@Slf4j
@Service
public class MediaProcessServiceImpl implements MediaProcessService {

    @Resource
    MediaFilesMapper mediaFilesMapper;

    @Resource
    MediaProcessMapper mediaProcessMapper;

    @Resource
    MediaProcessHistoryMapper mediaProcessHistoryMapper;

    @Override
    public List<MediaProcess> queryMediaProcessList(int shardTotal, int shardIndex, int count) {

        return mediaProcessMapper.selectListByShardIndex(shardTotal, shardIndex, count);
    }

    @Transactional
    @Override
    public void saveMediaProcess(Long taskId, String status, String fileId, String url, String errorMsg) {

        //1.查询任务是否存在
        MediaProcess mediaProcess = mediaProcessMapper.selectById(taskId);
        if (mediaProcess == null) {
            log.debug("待更新状态的任务不存在:{}", taskId);
            return;
        }

        //2.更新任务状态

        //2.1.任务失败
        if (Dictionary.MediaProcess.Status_FAILURE.getCode().equals(status)) {
            LambdaUpdateWrapper<MediaProcess> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(MediaProcess::getId, taskId);
            MediaProcess processFail = new MediaProcess();
            processFail.setStatus(status);
            processFail.setErrormsg(errorMsg);
            int updateProcess = mediaProcessMapper.update(processFail, updateWrapper);
            if (updateProcess < 1) {
                log.error("失败任务状态更新失败");
                XcException.cast("任务更新失败");
            }
            return;
        }

        //2.2.任务成功
        if (Dictionary.MediaProcess.Status_SUCCESS.getCode().equals(status)) {

            //2.2.1.更新media_files表
            MediaFiles mediaFiles = mediaFilesMapper.selectById(fileId);
            if (mediaFiles == null) {
                log.error("待更新url的媒体文件不存在");
                XcException.cast("任务更新失败");
            }
            mediaFiles.setUrl(url);
            int updateUrl = mediaFilesMapper.updateById(mediaFiles);
            if (updateUrl < 1) {
                log.error("更新媒体文件url出错");
                XcException.cast("任务更新失败");
            }

            //2.2.2.添加记录到media_process_history表
            mediaProcess.setStatus(status);
            mediaProcess.setUrl(url);
            MediaProcessHistory mediaProcessHistory = new MediaProcessHistory();
            BeanUtils.copyProperties(mediaProcess, mediaProcessHistory);
            int insertHistory = mediaProcessHistoryMapper.insert(mediaProcessHistory);
            if (insertHistory < 1) {
                log.error("添加记录到媒体处理历史表出错");
                XcException.cast("任务更新失败");
            }
            int deleteProcess = mediaProcessMapper.deleteById(taskId);
            if (deleteProcess < 1) {
                log.error("删除待处理任务出错");
                XcException.cast("任务更新失败");
            }
        }

    }
}
