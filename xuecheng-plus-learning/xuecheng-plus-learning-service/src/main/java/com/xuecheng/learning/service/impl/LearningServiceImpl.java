package com.xuecheng.learning.service.impl;

import com.xuecheng.base.exception.XcException;
import com.xuecheng.base.info.Dictionary;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.learning.feignclient.ContentServiceClient;
import com.xuecheng.learning.feignclient.MediaServiceClient;
import com.xuecheng.learning.model.dto.XcCourseTablesDto;
import com.xuecheng.learning.service.CourseTableService;
import com.xuecheng.learning.service.LearningService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @description 学习服务实现类
* @author TMC
* @date 2023/2/28 20:12
* @version 1.0
*/
@Service
@Slf4j
public class LearningServiceImpl implements LearningService {

    @Resource
    CourseTableService courseTableService;
    @Resource
    MediaServiceClient mediaServiceClient;
    @Resource
    ContentServiceClient contentServiceClient;

    @Override
    public RestResponse<String> getVideo(String userId, Long courseId, Long teachplanId, String mediaId) {

        CoursePublish coursePublish = contentServiceClient.getCoursePublish(courseId);
        if (coursePublish == null) {
            XcException.cast("课程发布信息不存在");
        }
        //用户已登录
        if (StringUtils.isNotEmpty(userId)) {
            XcCourseTablesDto courseTablesDto = courseTableService.queryCourseAccess(userId, courseId);
            String access = courseTablesDto.getLearnStatus();
            if (Dictionary.ChooseCourse.LearnStatus_QUALIFIED.getCode().equals(access)) {
                //远程调用媒资获取视频播放地址
                RestResponse<String> playUrl = mediaServiceClient.getPlayUrlByMediaId(mediaId);
                return playUrl;
            } else if (Dictionary.ChooseCourse.LearnStatus_EXPIRED.getCode().equals(access)) {
                return RestResponse.validfail("您的选课已过期,需要申请续期或重新支付");
            } else {
                return RestResponse.validfail("选课待支付");
            }
        }

        //用户未登录
        //该课程是否免费,如果免费可以继续学习
        String charge = coursePublish.getCharge();//收费规则
        if(Dictionary.CoursePublish.Charge_FREE.getCode().equals(charge)){
            return mediaServiceClient.getPlayUrlByMediaId(mediaId);
        }

        return RestResponse.validfail("请购买课程后继续学习");
    }
}
