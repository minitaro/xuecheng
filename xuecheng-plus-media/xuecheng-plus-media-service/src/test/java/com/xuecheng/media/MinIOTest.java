package com.xuecheng.media;

import com.xuecheng.media.service.MediaFileService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.UploadObjectArgs;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;

/**
* @description 测试 MinIO 服务上传文件、删除文件、查询文件
* @author TMC
* @date 2023/2/11 14:28
* @version 1.0
*/
public class MinIOTest {

    @Autowired
    MediaFileService mediaFileService;

    //1.客户端连接
    static MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://192.168.101.65:9000") //MinIO服务地址
                    .credentials("minioadmin", "minioadmin") //用户账号和密码
                    .build();


    //2.上传文件
    @Test
    public void upload() {

        try {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket("testbucket") //桶名称
                    .object("test/RestResponse.java") //同一个桶内对象名不能重复
                    .filename("D:\\Download\\upload\\RestResponse.java") //待上传文件
                    .build();
            //上传
            minioClient.uploadObject(uploadObjectArgs);
            System.out.println("上传成功了");
        } catch (Exception e) {
            System.out.println("上传失败");
        }
    }

    //3.查询文件
    @Test
    public void getFile() {
        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket("testbucket")
                .object("test/RestResponse.java")
                .build();
        try(
                FilterInputStream inputStream = minioClient.getObject(getObjectArgs);
                FileOutputStream outputStream = new FileOutputStream(new File("D:\\Download\\upload\\fileQuery.java"));
        ) {

            if(inputStream!=null){
                IOUtils.copy(inputStream,outputStream);
            }
        } catch (Exception e) {
        }

    }

    //4.删除文件
    @Test
    public void delete() {

        try {
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                    .bucket("testbucket")
                    .object("test/RestResponse.java")
                    .build();
            minioClient.removeObject(removeObjectArgs);
        } catch (Exception e) {
        }

    }




}
