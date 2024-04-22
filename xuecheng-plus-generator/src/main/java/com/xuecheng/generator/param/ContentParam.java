package com.xuecheng.generator.param;
/**
* @description content数据库参数
* @author TMC
* @date 2023/3/2 0:31
* @version 1.0
*/
public class ContentParam {

    // TODO 修改服务名,数据库名,表名
    private static final String Package_MODULE_NAME = "content"; //模块名
    private static final String DataSource_DB_NAME  = "xc0_content"; //数据库名称
    private static final String[] TABLE_NAMES = new String[]{
            "course_base",
            "course_market",
            "course_teacher",
            "course_publish",
            "course_publish_pre",
            "course_category",
            "teachplan",
            "teachplan_media"
    }; //数据库表名
}
