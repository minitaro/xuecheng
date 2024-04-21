package com.xuecheng.learning.service;

import com.xuecheng.base.model.RestResponse;

/**
* @description 学习服务
* @author TMC
* @date 2023/2/28 20:11
* @version 1.0
*/
public interface LearningService {

    /**
    * @description 获取教学视频
    * @param userId 用户id
     * @param courseId 课程id
     * @param teachplanId 教学计划id
     * @param mediaId 媒资id
    * @return com.xuecheng.base.model.RestResponse<java.lang.String>
    * @author TMC
    * @date 2023/2/28 20:11
    */
    RestResponse<String> getVideo(String userId, Long courseId, Long teachplanId, String mediaId);
}
