package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.j256.simplemagic.ContentType;
import com.xuecheng.base.exception.XcException;
import com.xuecheng.base.info.Dictionary;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.mapper.MediaProcessMapper;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaFileService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.UploadObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
* @description 媒资文件服务实现类
* @author TMC
* @date 2023/3/4 12:09
* @version 1.0
*/
@Service
@Slf4j
public class MediaFileServiceImpl implements MediaFileService {

    @Resource
    MediaFilesMapper mediaFilesMapper;

    @Resource
    MediaFileService currentProxy;

    @Resource
    MediaProcessMapper mediaProcessMapper;

    @Resource
    MinioClient minioClient;

    //普通文件桶
    @Value("${minio.bucket.files}")
    private String bucket_files;
    @Value("${minio.bucket.videofiles}")
    private String bucket_videoFiles;

    @Override
    public PageResult<MediaFiles> queryMediaFilesList(Long companyId, PageParams pageParams,
                                                      QueryMediaParamsDto queryMediaParamsDto) {

        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(queryMediaParamsDto.getFilename()), MediaFiles::getFilename,
                queryMediaParamsDto.getFilename());
        queryWrapper.eq(StringUtils.isNotEmpty(queryMediaParamsDto.getFileType()), MediaFiles::getFileType,
                queryMediaParamsDto.getFileType());

        // 构建结果集
        PageResult<MediaFiles> mediaListResult = new PageResult<>();

        if (pageParams.getPageNo() == null || pageParams.getPageSize() == null) {
            //无需分页
            Integer count = mediaFilesMapper.selectCount(queryWrapper);
            mediaListResult.setItems(mediaFilesMapper.selectList(queryWrapper));
            mediaListResult.setCounts(count);
            mediaListResult.setPage(1L);
            mediaListResult.setPageSize(count);
        }

        else {
            //分页对象
            Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
            // 查询数据内容获得结果
            Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
            // 获取数据列表
            // 获取数据总数
            mediaListResult.setItems(pageResult.getRecords());
            mediaListResult.setCounts(pageResult.getTotal());
            mediaListResult.setPage(pageParams.getPageNo());
            mediaListResult.setPageSize(pageParams.getPageSize());
        }

        return mediaListResult;

    }

    @Override
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, byte[] bytes,
                                          String folder, String objectName) {

        //1.生成待上传文件的md5值作为文件id
        String fileId = DigestUtils.md5Hex(bytes);

        //2.待上传文件的名称
        String filename = uploadFileParamsDto.getFilename();

        //3.构造上传对象名称(md5.ext)
        if (StringUtils.isBlank(objectName)) {
            objectName = fileId + filename.substring(filename.lastIndexOf("."));
        }

        //4.上传目录格式
        if (StringUtils.isEmpty(folder)) {
            //4.1.未指定目录则新建目录,格式为yyyy/MM/dd/
            folder = this.getFileFolder(new Date(), true, true, true);
        } else if (!folder.endsWith("/")) {
            //4.2.指定目录确保结尾为"/"
            folder = folder + "/";
        }

        //5.上传对象绝对路径
        objectName = folder + objectName;

        MediaFiles mediaFiles;
        try {
            //6.上传小文件至MinIO
            this.putToMinio(bytes, bucket_files, objectName);

            //7.已上传文件登记入库
            mediaFiles = currentProxy.addMediaFilesToDb(companyId, fileId, uploadFileParamsDto, bucket_files, objectName);

            //8.返回已登记入库的文件信息
            UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
            BeanUtils.copyProperties(mediaFiles, uploadFileResultDto);
            return uploadFileResultDto;

        } catch (Exception e) {
            log.debug("上传小文件失败:{}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }


    @Override
    public RestResponse<Boolean> checkFile(String fileMd5) {

        //1.查询文件在媒资表media_files是否登记
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles == null) {
            //1.1.媒资表没有记录,文件不存在
            return RestResponse.success(false);
        }
        //2.查询文件在文件管理系统是否存在
        return RestResponse.success(this.isInMinio(mediaFiles.getBucket(), mediaFiles.getFilePath()));
    }

    @Override
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex) {

        //1.获取文件的分块目录
        String chunkFileFolderPath = this.getChunkFileFolderPath(fileMd5);
        //2.获取分块文件路径
        String chunkFilePath = chunkFileFolderPath + chunkIndex;
        //3.查询分块文件在文件管理系统是否存在
        return RestResponse.success(this.isInMinio(bucket_videoFiles, chunkFilePath));
    }

    @Override
    public RestResponse<Boolean> uploadChunk(String fileMd5, int chunkIndex, byte[] bytes) {

        //1.获取文件的分块目录
        String chunkFileFolderPath = this.getChunkFileFolderPath(fileMd5);
        //2.获取分块文件路径
        String chunkFilePath = chunkFileFolderPath + chunkIndex;
        //3.分块文件写入MinIO
        this.putToMinio(bytes, bucket_videoFiles, chunkFilePath);
        return RestResponse.success(true);
    }

    @Override
    public RestResponse<Boolean> mergeChunks(Long companyId, String fileMd5, int chunkTotal,
                                             UploadFileParamsDto uploadFileParamsDto) {

        //1.下载文件所有分块
        File[] chunks = this.downloadChunksFromMinio(fileMd5, chunkTotal);

        //2.获取合并文件的拓展名
        String filename = uploadFileParamsDto.getFilename();
        String extension = filename.substring(filename.lastIndexOf("."));

        //3.合并与上传文件
        File tempMergeFile = null;
        try {

            //3.1.创建临时合并文件
            try {
                tempMergeFile = File.createTempFile(fileMd5, extension);
            }catch (Exception e) {
                log.error("创建临时合并文件出错");
                XcException.cast("合并文件失败");
            }

            //3.2.合并文件
            try (RandomAccessFile raf_write = new RandomAccessFile(tempMergeFile, "rw")) {
                byte[] buffer = new byte[1024];
                for (File chunk : chunks) {
                    try (RandomAccessFile raf_read = new RandomAccessFile(chunk, "r")) {
                        int len;
                        while ((len = raf_read.read(buffer)) != -1) {
                            //向合并文件写数据
                            raf_write.write(buffer, 0, len);
                        }
                    }
                }
            }catch (Exception e) {
                log.error("合并文件出错");
                XcException.cast("合并文件失败");
            }

            //3.3.校验合并后的文件是否正确
            try(FileInputStream mergeFileStream = new FileInputStream(tempMergeFile)) {
                String mergeFileMd5Hex = DigestUtils.md5Hex(mergeFileStream);
                if (!fileMd5.equals(mergeFileMd5Hex)) {
                    log.error("合并文件校验不通过,文件路径:{},原始文件md5:{}", tempMergeFile.getAbsoluteFile(), fileMd5);
                    XcException.cast("合并文件失败");
                }
            } catch (Exception e) {
                log.error("合并文件校验出错,文件路径:{},原始文件md5:{}", tempMergeFile.getAbsolutePath(), fileMd5);
                XcException.cast("合并文件失败");
            }

            //3.4.将合并文件上传至MinIO
            //3.4.1.上传对象名称
            String objectName = this.getFilePathByMd5(fileMd5, extension);
            //3.4.2.上传
            this.uploadToMinio(tempMergeFile.getAbsolutePath(), bucket_videoFiles, objectName);
            //3.4.3.上传文件信息入库
            uploadFileParamsDto.setFileSize(tempMergeFile.length());
            this.addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucket_videoFiles, objectName);

            return RestResponse.success(true);

        }finally {
            //3.5.删除临时分块文件
            for (File chunk : chunks) {
                if (chunk.exists()) {
                    chunk.delete();
                }
            }
            //3.6.删除临时合并文件
            if (tempMergeFile != null) {
                tempMergeFile.delete();
            }
        }
    }

    @Override
    public MediaFiles getFileById(String id) {
        return mediaFilesMapper.selectById(id);
    }


    @Transactional
    @Override
    public MediaFiles addMediaFilesToDb(Long companyId, String fileId, UploadFileParamsDto uploadFileParamsDto,
                                        String bucket, String objectName){

        //1.查询文件是否存在
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileId);
        if (mediaFiles == null) {
            //2.文件不存在则登记入库
            mediaFiles = new MediaFiles();
            //2.1.设置基本信息
            BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
            mediaFiles.setId(fileId);
            mediaFiles.setFileId(fileId);
            mediaFiles.setCompanyId(companyId);
            mediaFiles.setBucket(bucket);
            mediaFiles.setFilePath(objectName);
            mediaFiles.setAuditStatus(Dictionary.MediaFiles.AuditStatus_APPROVED.getCode());
            mediaFiles.setStatus(Dictionary.MediaFiles.Status_NORMAL.getCode());
            //2.2.设置url
            //2.2.1.获取文件的媒体类型
            String mimeType = null;
            String filename = uploadFileParamsDto.getFilename();
            if (filename.indexOf(".") >= 0) {
                mimeType = this.getMimeTypeByExtension(filename.substring(filename.lastIndexOf(".")));
            }
            //2.2.2.图片和mp4视频可以设置url
            if (mimeType != null && (mimeType.indexOf("image") >= 0 || mimeType.indexOf("mp4") >= 0)) {
                mediaFiles.setUrl("/" + bucket + "/" + objectName);
            }
            //2.3.保存文件信息到媒体文件表media_files
            int insertMediaFiles = mediaFilesMapper.insert(mediaFiles);
            if (insertMediaFiles < 1) {
                XcException.cast("添加媒体文件信息失败");
            }
            //2.4.对avi视频添加到待处理任务表media_process
            if (ContentType.AVI.getMimeType().equals(mimeType)) {
                MediaProcess mediaProcess = new MediaProcess();
                BeanUtils.copyProperties(mediaFiles, mediaProcess);
                //设置状态为未处理
                mediaProcess.setStatus(Dictionary.MediaProcess.Status_TO_BE_PROCESSED.getCode());
                int insertMediaProces = mediaProcessMapper.insert(mediaProcess);
                if (insertMediaProces < 1) {
                    XcException.cast("添加媒体待处理任务失败");
                }
            }

        }
        //3.返回入库的媒体文件信息
        return mediaFiles;
    }


    @Override
    public void uploadToMinio(String filePath, String bucket, String objectName) {

        try {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .filename(filePath)
                    .build();
            minioClient.uploadObject(uploadObjectArgs);
            log.debug("文件上传成功:{}", filePath);
        } catch (Exception e) {
            log.error("上传文件到文件系统出错");
            XcException.cast("上传文件失败");
        }
    }



    /**
     * @description 将内存文件写入MinIO
     * @param bytes 文件字节数组
     * @param bucket 写入桶名称
     * @param objectName 写入对象名称
     * @author TMC
     * @date 2023/2/11 17:51
     */
    private void putToMinio(byte[] bytes, String bucket, String objectName) {

        //1.资源的媒体类型,默认为未知二进制流
        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;

        //2.通过写入对象名称的拓展名获取资源的媒体类型
        if (objectName.indexOf(".") >= 0) {
            String extension = objectName.substring(objectName.lastIndexOf("."));
            contentType = this.getMimeTypeByExtension(extension);
        }

        //3.文件字节数组转化为流并写入MinIO
        try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)) {
            //3.1.构建写入参数对象
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    //InputStream stream, long objectSize 对象大小, long partSize 分片大小(-1表示5M,最大不要超过5T，分片数量最多10000)
                    .stream(byteArrayInputStream, byteArrayInputStream.available(), -1)
                    .contentType(contentType)
                    .build();
            //3.2.写入
            minioClient.putObject(putObjectArgs);
        } catch (Exception e) {
            log.debug("文件写入文件系统:{}", e.getMessage());
            e.printStackTrace();
            XcException.cast("文件写入文件系统出错");
        }
    }

    /**
     * @description 查询文件是否存在于MinIO
     * @param bucket 桶名称
     * @param objectName 文件路径
     * @return com.xuecheng.base.model.RestResponse<java.lang.Boolean>
     * @author TMC
     * @date 2023/2/11 23:42
     */
    private Boolean isInMinio(String bucket, String objectName) {
        //1.构建查询条件
        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket(bucket)
                .object(objectName)
                .build();
        //2.文件管理系统MinIO中查询文件
        try (InputStream inputStream = minioClient.getObject(getObjectArgs)){
            if (inputStream == null) {
                //2.1.文件管理系统没有该文件
                return false;
            }
        }catch (Exception e) {
            //2.2.文件管理系统没有该文件
            return false;
        }

        //3.文件存在于MinIO中
        return true;
    }

    @Override
    public File downloadFromMinio(File file, String bucket, String objectName) {
        //1.构造查询参数
        GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket(bucket).object(objectName).build();
        //2.获取指定下载对象
        try(InputStream inputStream = minioClient.getObject(getObjectArgs);
            FileOutputStream outputStream = new FileOutputStream(file))
        {
            //3.拷贝至存储对象
            IOUtils.copy(inputStream, outputStream);
            return file;

        }catch (Exception e) {
            log.error("下载文件出错");
            XcException.cast("下载文件失败");
        }
        return null;
    }

    /**
     * @description 从MinIO下载指定文件的所有分块
     * @param fileMd5 文件md5
     * @param chunkTotal 分块总数
     * @return java.io.File[]
     * @author TMC
     * @date 2023/2/12 11:12
     */
    private File[] downloadChunksFromMinio(String fileMd5, int chunkTotal) {
        //1.获取文件分块所在目录
        String chunkFileFolderPath = this.getChunkFileFolderPath(fileMd5);
        //2.创建存放文件分块的数组
        File[] chunkFiles = new File[chunkTotal];
        File tempChunkFile = null;
        //3.逐个分块下载并存入数组
        for (int i = 0; i < chunkTotal; i++) {
            try {
                //3.1.创建分块临时文件
                tempChunkFile = File.createTempFile("chunk" + i, null);
            }catch (Exception e) {
                log.error("创建分块临时文件出错");
                XcException.cast("下载文件失败");
            }
            //3.2.分块对象名称
            String chunkObjectName = chunkFileFolderPath + i;
            //3.3.下载分块
            this.downloadFromMinio(tempChunkFile, bucket_videoFiles, chunkObjectName);
            //3.4.存入数组
            chunkFiles[i] = tempChunkFile;
        }
        //4.返回文件分块数组
        return chunkFiles;
    }

    /**
     * @description 根据日期创建目录(格式:yyyy/MM/dd/)
     * @param date 日期
     * @param year 年份
     * @param month 月份
     * @param day 日
     * @return java.lang.String
     * @author TMC
     * @date 2023/2/11 22:59
     */
    private String getFileFolder(Date date, boolean year, boolean month, boolean day) {

        //1.获取指定格式的日期字符串
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateFormat.format(date);
        //2.获取日期年,月,日
        String[] dateStringArray = dateString.split("-");

        //3.创建目录
        StringBuffer folderString = new StringBuffer();
        //4.拼接并返回目录
        if (year) {
            folderString.append(dateStringArray[0]);
            folderString.append("/");
        }
        if (month) {
            folderString.append(dateStringArray[1]);
            folderString.append("/");
        }
        if (day) {
            folderString.append(dateStringArray[2]);
            folderString.append("/");
        }
        return folderString.toString();
    }



    /**
     * @description 根据文件md5命名文件分块目录
     * @param fileMd5 文件md5
     * @return java.lang.String
     * @author TMC
     * @date 2023/2/11 23:06
     */
    private String getChunkFileFolderPath(String fileMd5) {

        return fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/" + "chunk" + "/";

    }

    /**
     * @description 根据文件md5命名文件
     * @param fileMd5 文件md5
     * @param extension 文件拓展名
     * @return java.lang.String
     * @author TMC
     * @date 2023/2/12 13:09
     */
    private String getFilePathByMd5(String fileMd5, String extension) {

        return fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/" + fileMd5 + extension;

    }

    /**
     * @description 根据扩展名获取匹配的媒体类型
     * @param extension 拓展名
     * @return java.lang.String
     * @author TMC
     * @date 2023/2/12 16:21
     */
    private String getMimeTypeByExtension(String extension) {
        //1.媒体类型默认为未知二进制流
        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        //2.通过拓展名获取MimeType
        if (StringUtils.isNotEmpty(extension)) {
            ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
            if (extensionMatch != null) {
                contentType = extensionMatch.getMimeType();
            }
        }
        return contentType;
    }
}
