package com.xuecheng.generator.param;
/**
* @description orders数据库参数
* @author TMC
* @date 2023/3/2 1:34
* @version 1.0
*/
public class OrdersParam {

    // TODO 修改服务名,数据库名,表名
    private static final String Package_MODULE_NAME = "orders"; //模块名称
    private static final String DataSource_DB_NAME  = "xc0_orders"; //数据库名称
    private static final String[] TABLE_NAMES = new String[]{

            "xc_orders",
            "xc_orders_goods",
            "xc_pay_record"

    };
}
