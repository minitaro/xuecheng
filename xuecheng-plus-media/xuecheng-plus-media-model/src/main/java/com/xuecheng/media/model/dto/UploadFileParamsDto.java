package com.xuecheng.media.model.dto;

import lombok.Data;
import lombok.ToString;

/**
* @description 上传小文件请求dto
* @author TMC
* @date 2023/2/11 16:07
* @version 1.0
*/
@Data
@ToString
public class UploadFileParamsDto {

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 资源的媒体类型 mimeType
     */
    private String contentType;

    /**
     * 文件类型（文档，音频，视频）
     */
    private String fileType;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 标签
     */
    private String tags;

    /**
     * 上传人
     */
    private String username;

    /**
     * 备注
     */
    private String remark;

}
