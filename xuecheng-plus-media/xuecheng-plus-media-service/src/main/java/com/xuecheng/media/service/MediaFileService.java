package com.xuecheng.media.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

/**
* @description 媒资文件服务
* @author TMC
* @date 2023/2/12 21:28
* @version 1.0
*/
public interface MediaFileService {

    /**
    * @description 媒资文件列表查询
    * @param companyId 机构id
     * @param pageParams 分页参数
     * @param queryMediaParamsDto 查询条件
    * @return com.xuecheng.base.model.PageResult<com.xuecheng.media.model.po.MediaFiles>
    * @author TMC
    * @date 2023/2/12 21:30
    */
    public PageResult<MediaFiles> queryMediaFilesList(Long companyId, PageParams pageParams,
                                                      QueryMediaParamsDto queryMediaParamsDto);


    /**
     * @description 上传小文件
     * @param companyId 机构id
     * @param uploadFileParamsDto 待上传文件信息
     * @param bytes 文件字节数组
     * @param folder 桶下边的子目录
     * @param objectName 上传对象名称
     * @return com.xuecheng.media.model.dto.UploadFileResultDto
     * @author TMC
     * @date 2023/2/11 16:10
     */
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, byte[] bytes,
                                          String folder, String objectName);


    /**
     * @description 上传文件信息登记入库
     * @param companyId 机构id
     * @param fileId 上传文件id
     * @param uploadFileParamsDto 待上传文件信息
     * @param bucket 桶名称
     * @param objectName 上传对象名称
     * @return com.xuecheng.media.model.po.MediaFiles
     * @author TMC
     * @date 2023/2/11 18:46
     */
    @Transactional
    public MediaFiles addMediaFilesToDb(Long companyId, String fileId, UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName);


    /**
     * @description 检查文件是否存在
     * @param fileMd5 文件的md5
     * @return com.xuecheng.base.model.RestResponse<java.lang.Boolean>
     * @author TMC
     * @date 2023/2/11 22:37
     */
    RestResponse<Boolean> checkFile(String fileMd5);

    /**
     * @description 检查分块是否存在
     * @param fileMd5 分块的md5
     * @param chunkIndex 分块序号
     * @return com.xuecheng.base.model.RestResponse<java.lang.Boolean>
     * @author TMC
     * @date 2023/2/11 22:37
     */
    RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex);

    /**
     * @description 上传分块文件
     * @param fileMd5 分块文件的md5
     * @param chunkIndex 分块序号
     * @param bytes 分块文件的字节数组
     * @return com.xuecheng.base.model.RestResponse
     * @author TMC
     * @date 2023/2/11 22:39
     */
    RestResponse<Boolean> uploadChunk(String fileMd5, int chunkIndex, byte[] bytes);

    /**
     * @description 合并分块
     * @param companyId 机构id
     * @param fileMd5 分块文件的md5
     * @param chunkTotal 分块总数
     * @param uploadFileParamsDto 待上传的文件信息
     * @return com.xuecheng.base.model.RestResponse
     * @author TMC
     * @date 2023/2/11 22:40
     */
    RestResponse<Boolean> mergeChunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto);

    /**
     * @description 根据id查询媒体文件信息
     * @param id 文件id
     * @return com.xuecheng.media.model.po.MediaFiles
     * @author TMC
     * @date 2023/2/12 16:41
     */
    MediaFiles getFileById(String id);

    /**
     * @description 将本地文件上传MinIO
     * @param filePath 本地文件路径
     * @param bucket 桶名称
     * @param objectName 上传对象名称
     * @return void
     * @author TMC
     * @date 2023/2/12 13:22
     */
    void uploadToMinio(String filePath, String bucket, String objectName);

    /**
     * @description 从MinIO下载文件至本地
     * @param file 存储文件对象
     * @param bucket 桶名称
     * @param objectName 待下载对象名称
     * @return File
     * @author TMC
     * @date 2023/2/12 10:43
     */
    File downloadFromMinio(File file, String bucket, String objectName);
}


