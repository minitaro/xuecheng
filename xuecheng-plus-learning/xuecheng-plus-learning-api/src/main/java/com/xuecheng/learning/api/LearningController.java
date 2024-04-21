package com.xuecheng.learning.api;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.learning.service.LearningService;
import com.xuecheng.learning.utils.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
* @description 学习服务控制器
* @author TMC
* @date 2023/2/28 20:08
* @version 1.0
*/
@Api(value = "学习服务控制器", tags = "学习服务控制器")
@Slf4j
@RestController
public class LearningController {

    @Resource
    LearningService learningService;


    @ApiOperation("获取视频接口")
    @GetMapping("/open/learn/getvideo/{courseId}/{teachplanId}/{mediaId}")
    public RestResponse<String> getvideo(@PathVariable("courseId") Long courseId,
                                         @PathVariable("teachplanId") Long teachplanId,
                                         @PathVariable("mediaId") String mediaId) {

        String userId = SecurityUtil.getUser().getId();
        return learningService.getVideo(userId, courseId, teachplanId, mediaId);

    }

}
