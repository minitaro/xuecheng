package com.xuecheng.generator.param;
/**
* @description media数据库参数
* @author TMC
* @date 2023/3/2 0:20
* @version 1.0
*/
public class MediaParam {

    // TODO 修改服务名,数据库名,表名
    private static final String Package_MODULE_NAME = "media"; //模块名
    private static final String DataSource_DB_NAME  = "xc0_media"; //数据库名称
    private static final String[] TABLE_NAMES = new String[]{
            "media_files",
            "media_process",
            "media_process_history"
    };

}
