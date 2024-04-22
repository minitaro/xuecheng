package com.xuecheng.media.api;

import com.xuecheng.base.exception.XcException;
import com.xuecheng.base.info.Dictionary;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import com.xuecheng.media.utils.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
* @description 媒资文件控制器
* @author TMC
* @date 2023/3/4 11:53
* @version 1.0
*/
@Api(value = "媒资文件控制器", tags = "媒资文件控制器")
@RestController
public class MediaFileController {

    @Resource
    MediaFileService mediaFileService;

    @ApiOperation("媒资列表查询接口")
    @PostMapping("/files")
    public PageResult<MediaFiles> queryMediaFilesList(PageParams pageParams,
                                                      @RequestBody QueryMediaParamsDto queryMediaParamsDto){
        Long companyId = Long.parseLong(SecurityUtil.getUser().getCompanyId());
        return mediaFileService.queryMediaFilesList(companyId, pageParams, queryMediaParamsDto);
    }


    @ApiOperation("上传小文件接口")
    @RequestMapping(value = "/upload/coursefile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public UploadFileResultDto upload(@RequestPart(value = "filedata") MultipartFile filedata,
                                      @RequestParam(value = "folder", required=false) String folder,
                                      @RequestParam(value= "objectName", required=false) String objectName) {

        //1.封装上传小文件请求dto
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        String contentType = filedata.getContentType();
        uploadFileParamsDto.setContentType(contentType); //文件媒体类型
        if (contentType.indexOf("image") >= 0) {
            uploadFileParamsDto.setFileType(Dictionary.MediaFiles.FileType_IMAGE.getCode()); //媒体类型:图片
        } else {
            uploadFileParamsDto.setFileType(Dictionary.MediaFiles.FileType_OTHERS.getCode());
        }
        uploadFileParamsDto.setFileSize(filedata.getSize()); //文件大小
        uploadFileParamsDto.setFilename(filedata.getOriginalFilename()); //文件名称
        uploadFileParamsDto.setRemark("");

        //2.上传小文件,并返回响应dto
        Long companyId = 1232141425L;
        UploadFileResultDto uploadFileResultDto = null;
        try {
            uploadFileResultDto = mediaFileService.uploadFile(companyId, uploadFileParamsDto, filedata.getBytes(), folder, objectName);
        } catch (Exception e) {
            XcException.cast("上传小文件失败");
        }

        return uploadFileResultDto;
    }

    @ApiOperation("预览文件")
    @GetMapping("/preview/{mediaId}")
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
