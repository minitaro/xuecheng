package com.xuecheng.generator.param;
/**
* @description message表参数
* @author TMC
* @date 2023/3/2 1:37
* @version 1.0
*/
public class MessageParam {

    // TODO 修改服务名,数据库名,表名
    private static final String Package_MODULE_NAME = "messagesdk"; //模块名称
    private static final String DataSource_DB_NAME  = "xc0_content"; //数据库名称
    private static final String[] TABLE_NAMES = new String[]{

            "mq_message",
            "mq_message_history"

    };

}
