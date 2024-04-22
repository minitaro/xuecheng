package com.xuecheng.base.info;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
* @description 数据字典
* @author TMC
* @date 2023/2/24 22:39
* @version 1.0
*/
public class Dictionary {

    /**
     * @description 课程基本信息
     * @author TMC
     * @date 2023/2/24 22:10
     */
    @AllArgsConstructor
    @Getter
    public enum CourseBase {

        AuditStatus_NOT_APPROVED("202001", "审核未通过"),
        AuditStatus_TO_BE_SUBMITTED("202002", "未提交"),
        AuditStatus_SUBMITTED("202003","已提交"),
        AuditStatus_APPROVED("202004","审核通过"),
        Status_UNPUBLISHED("203001", "未发布"),
        Status_PUBLISHED("203002", "已发布"),
        Status_SUSPENDED("203003", "下线"),
        ;

        private final String code;
        private final String desc;

    }

    /**
     * @description 课程营销信息
     * @author TMC
     * @date 2023/2/24 22:10
     */
    @AllArgsConstructor
    @Getter
    public enum CourseMarket {

        Charge_FREE("201000", "免费"),
        Charge_PAID("201001", "收费"),
        ;

        private final String code;
        private final String desc;

    }

    /**
     * @description 课程发布
     * @author TMC
     * @date 2023/2/24 22:10
     */
    @AllArgsConstructor
    @Getter
    public enum CoursePublish {

        Charge_FREE("201000", "免费"),
        Charge_PAID("201001", "收费"),
        Status_UNPUBLISHED("203001","未发布"),
        Status_PUBLISHED("203002","已发布"),
        Status_SUSPENDED("203003","下线"),
        ;

        private final String code;
        private final String desc;

    }


    /**
    * @description 选课
    * @author TMC
    * @date 2023/2/24 22:47
    */
    @AllArgsConstructor
    @Getter
    public enum ChooseCourse {

        LearnStatus_QUALIFIED("702001", "正常学习"),
        LearnStatus_UNQUALIFIED("702002", "没有选课或选课后没有支付"),
        LearnStatus_EXPIRED("702003", "已过期需要申请续期或重新支付"),
        OrderType_FREE("700001", "免费课程"),
        OrderType_PAID("700002", "收费课程"),
        Status_SUCCESS("701001", "选课成功"),
        Status_TO_BE_PAID("701002", "待支付"),
        ;

        private final String code;
        private final String desc;

    }

    /**
     * @description 媒体文件
     * @author TMC
     * @date 2023/2/24 22:10
     */
    @AllArgsConstructor
    @Getter
    public enum MediaFiles {

        AuditStatus_NOT_APPROVED("002001", "审核未通过"),
        AuditStatus_TO_BE_AUDITTED("002002", "未审核"),
        AuditStatus_APPROVED("002003","审核通过"),
        FileType_IMAGE("001001", "图片"),
        FileType_VIDEO("001002", "视频"),
        FileType_OTHERS("001003", "其他"),
        Status_NORMAL("1", "正常"),
        Status_NOT_DISPLAYED("0", "不展示"),

        ;

        private final String code;
        private final String desc;
    }

    /**
     * @description 媒体处理
     * @author TMC
     * @date 2023/2/24 22:10
     */
    @AllArgsConstructor
    @Getter
    public enum MediaProcess{

        Status_TO_BE_PROCESSED("1", "未处理"),
        Status_SUCCESS("2", "处理成功"),
        Status_FAILURE("3", "处理失败"),

        ;

        private final String code;
        private final String desc;

    }



    /**
     * @description 订单
     * @author TMC
     * @date 2023/2/24 22:47
     */
    @AllArgsConstructor
    @Getter
    public enum Orders {

        OrderType_COURSE_PURCHASE("60201", "购买课程"),
        OrderType_LEARNING_MATERIAL("60202", "学习资料"),
        Status_TO_BE_PAID("600001", "待支付"),
        Status_SUCCESS("600002", "已支付"),
        Status_CLOSED("600003", "已关闭"),
        Status_REFUND_SUCCESS("600004", "已退款"),
        Status_FINISHED("600005", "已完成"),

        ;

        private final String code;
        private final String desc;

    }

    /**
     * @description 交易记录
     * @author TMC
     * @date 2023/2/24 22:47
     */
    @AllArgsConstructor
    @Getter
    public enum PayRecord {

        Status_TO_BE_PAID("601001", "待支付"),
        Status_SUCCESS("601002", "已支付"),
        Status_REFUND_SUCCESS("601003", "已退款"),
        OutPayChannel_WXPAY("603001", "微信支付"),
        OutPayChannel_ALIPAY("603002", "支付宝"),

        ;

        private final String code;
        private final String desc;

    }

    /**
    * @description 用户角色信息
    * @author TMC
    * @date 2023/3/6 23:36
    */
    @AllArgsConstructor
    @Getter
    public enum XcRole{
        Id_STUDENT("17", "学生"),
        Id_TEACHER("18", "老师"),
        Id_TEACHMANAGER("20", "教学管理员"),
        Id_ADMIN("6", "管理员"),
        Id_SUPER("8", "超级管理员"),

        ;

        private final String code;
        private final String desc;
    }

    /**
    * @description 用户信息
    * @author TMC
    * @date 2023/3/6 23:26
    */
    @AllArgsConstructor
    @Getter
    public enum XcUser{

        Utype_STUDENT("101001", "学生"),
        Utype_TEACHER("101002", "老师"),
        Utype_ADMIN("101003", "管理员"),

        Status_VALID("1", "使用态"),
        Status_DELETED("0", "删除态"),
        Status_TEMPORARY("-1", "暂时态");

        ;

        private final String code;
        private final String desc;

    }






}
