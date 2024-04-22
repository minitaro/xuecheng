package com.xuecheng.generator.param;
/**
* @description users数据库参数
* @author TMC
* @date 2023/3/2 1:30
* @version 1.0
*/
public class UsersParam {

    // TODO 修改服务名,数据库名,表名
    private static final String Package_MODULE_NAME = "ucenter";
    private static final String DataSource_DB_NAME  = "xc0_users"; //数据库名称
    private static final String[] TABLE_NAMES = new String[]{
            "xc_company",
            "xc_company_user",
            "xc_menu",
            "xc_permission",
            "xc_role",
            "xc_teacher",
            "xc_user",
            "xc_user_role"
    };
}
