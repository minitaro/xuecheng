package com.xuecheng.generator.param;
/**
* @description learning数据库参数
* @author TMC
* @date 2023/3/2 1:40
* @version 1.0
*/
public class LearningParam {

    // TODO 修改服务名,数据库名,表名
    private static final String SERVICE_NAME = "learning"; //模块名称
    private static final String DataSource_DB_NAME  = "xc0_learning"; //数据库名称
    private static final String[] TABLE_NAMES = new String[]{

            "xc_learn_record",
            "xc_choose_course",
            "xc_course_tables"

    };
}
