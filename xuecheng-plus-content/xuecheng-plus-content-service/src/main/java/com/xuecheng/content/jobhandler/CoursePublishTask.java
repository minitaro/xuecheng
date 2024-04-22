package com.xuecheng.content.jobhandler;

import com.xuecheng.content.mapper.CoursePublishMapper;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MessageProcessAbstract;
import com.xuecheng.messagesdk.service.MqMessageService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.concurrent.TimeUnit;

/**
* @description TODO
* @author TMC
* @date 2023/2/17 16:33
* @version 1.0
*/
@Slf4j
@Component
public class CoursePublishTask extends MessageProcessAbstract {

    //课程发布消息类型
    public static final String MESSAGE_TYPE = "course_publish";

    @Resource
    CoursePublishService coursePublishService;

    @Resource
    CoursePublishMapper coursePublishMapper;

    @XxlJob("CoursePublishJobHandler")
    public void coursePublishJobHandler() {
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        log.debug("shardIndex=" + shardIndex + ",shardTotal=" + shardTotal);
        this.process(shardIndex, shardTotal, MESSAGE_TYPE, 5, 60);
    }

    @Override
    public boolean execute(MqMessage mqMessage) {

        long courseId = Integer.parseInt(mqMessage.getBusinessKey1());

        this.generateCourseHtml(mqMessage, courseId);

        this.saveCourseCache(mqMessage, courseId);

        this.saveCourseIndex(mqMessage, courseId);

        return false;
    }

    /**
    * @description 保存课程索引至引擎数据库
    * @param mqMessage 课程发布消息
     * @param courseId 课程id
    * @return void
    * @author TMC
    * @date 2023/2/18 16:56
    */
    private void saveCourseIndex(MqMessage mqMessage, long courseId) {

        log.debug("保存课程索引信息,课程id:{}", courseId);

        Long messageId = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        if (mqMessageService.getStageTwo(messageId) > 0) {
            log.debug("当前阶段是创建课程索引,已经完成不再处理,任务信息:{}", mqMessage);
            return;
        }
        Boolean result = coursePublishService.saveCourseIndex(courseId);
        if (result) {
            mqMessageService.completedStageTwo(messageId);
        }else {
            log.error("保存课程索引信息失败,课程id:{}", courseId);
        }

    }


    private void saveCourseCache(MqMessage mqMessage, long courseId) {
        log.debug("将课程信息缓存至redis,课程id:{}",courseId);
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        
    }

    /**
    * @description 生成课程静态化页面并上传至文件系统
    * @param mqMessage 课程发布消息
     * @param courseId 课程id
    * @return void
    * @author TMC
    * @date 2023/2/18 1:49
    */
    private void generateCourseHtml(MqMessage mqMessage, long courseId) {

        log.debug("开始进行课程静态化,课程id:{}", courseId);

        //1.保证任务幂等性
        Long messageId = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        if (mqMessageService.getStageOne(messageId) == 1) {
            log.debug("课程静态化已处理直接返回,课程id:{}", courseId);
            return;
        }
        //2.课程静态化
        try {
            File file = coursePublishService.generateCourseHtml(courseId);
            if (file != null) {
                coursePublishService.uploadCourseHtml(courseId, file);
            }else {
                throw new RuntimeException("课程静态化页面文件为空");
            }
        }catch (Exception e) {
            log.debug("课程静态化异常:{}", e.getMessage());
            return;
        }
        //3.更新消息表任务状态
        mqMessageService.completedStageOne(messageId);
    }
}
