package com.xuecheng.media.api;

import com.xuecheng.base.info.Dictionary;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.service.MediaFileService;
import com.xuecheng.media.utils.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
* @description 大文件控制器
* @author TMC
* @date 2023/2/11 22:19
* @version 1.0
*/
@Api(value = "大文件控制器", tags = "大文件控制器")
@RestController
public class BigFilesController {

    @Resource
    MediaFileService mediaFileService;

    @ApiOperation(value = "检查文件接口")
    @PostMapping("/upload/checkfile")
    public RestResponse<Boolean> checkFile(@RequestParam("fileMd5") String fileMd5){

        return mediaFileService.checkFile(fileMd5);

    }

    @ApiOperation(value = "检查分块接口")
    @PostMapping("/upload/checkchunk")
    public RestResponse<Boolean> checkChunk(@RequestParam("fileMd5") String fileMd5,
                                            @RequestParam("chunk") int chunk){
        return mediaFileService.checkChunk(fileMd5, chunk);

    }

    @ApiOperation(value = "上传分块接口")
    @PostMapping("/upload/uploadchunk")
    public RestResponse<Boolean> uploadChunk(@RequestParam("file") MultipartFile file,
                                             @RequestParam("fileMd5") String fileMd5,
                                             @RequestParam("chunk") int chunk) throws Exception{

        return mediaFileService.uploadChunk(fileMd5, chunk, file.getBytes());

    }

    @ApiOperation(value = "合并分块接口")
    @PostMapping("/upload/mergechunks")
    public RestResponse<Boolean> mergeChunks(@RequestParam("fileMd5") String fileMd5,
                                             @RequestParam("fileName") String fileName,
                                             @RequestParam("chunkTotal") int chunkTotal){

        Long companyId = Long.parseLong(SecurityUtil.getUser().getCompanyId());

        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        uploadFileParamsDto.setFilename(fileName);
        uploadFileParamsDto.setFileType(Dictionary.MediaFiles.FileType_VIDEO.getCode());//视频
        return mediaFileService.mergeChunks(companyId, fileMd5, chunkTotal, uploadFileParamsDto);

    }


}
