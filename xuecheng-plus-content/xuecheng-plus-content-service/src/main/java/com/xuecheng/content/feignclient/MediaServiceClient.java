package com.xuecheng.content.feignclient;

import com.xuecheng.content.config.MultipartSupportConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
* @description 媒资服务远程接口
* @author TMC
* @date 2023/2/17 19:10
* @version 1.0
*/
@FeignClient(value = "media-api", configuration = MultipartSupportConfig.class, fallbackFactory =
        MediaServiceClientFallbackFactory.class)
public interface MediaServiceClient {

    @RequestMapping(value = "/media/upload/coursefile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    String upload(@RequestPart("filedata") MultipartFile filedata,
                  @RequestParam(value = "folder", required = false) String folder,
                  @RequestParam(value = "objectName", required = false) String objectName);

}
