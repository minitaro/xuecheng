package com.xuecheng.media.api;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
* @description 媒资公开控制器
* @author TMC
* @date 2023/2/17 10:16
* @version 1.0
*/
@Api(value = "媒资公开控制器", tags = "媒资公开控制器")
@RestController
@RequestMapping("/open")
public class MediaOpenController {

    @Resource
    MediaFileService mediaFileService;

    @ApiOperation("预览媒资接口")
    @GetMapping("/preview/{mediaId}")
    @Cacheable(cacheNames = "mediafile-url", key = "'mediafile_' + #mediaId")
    public RestResponse<String> getPlayUrlByMediaId(@PathVariable("mediaId") String mediaId) {

        MediaFiles mediaFiles = mediaFileService.getFileById(mediaId);
        if (mediaFiles == null) {
            return RestResponse.validfail("媒体文件不存在");
        }
        String url = mediaFiles.getUrl();
        if (StringUtils.isBlank(url)) {
            return RestResponse.validfail("媒体文件还没有处理,请稍后预览");
        }
        return RestResponse.success(mediaFiles.getUrl());

    }

}
