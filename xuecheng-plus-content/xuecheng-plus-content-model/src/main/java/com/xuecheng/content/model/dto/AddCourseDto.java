package com.xuecheng.content.model.dto;

import com.xuecheng.base.validation.constraints.Phone;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.*;

/**
* @description 新增课程请求dto
* @author TMC
* @date 2023/2/7 11:58
* @version 1.0
*/
@ApiModel(value="AddCourseDto", description="新增课程信息")
@Data
@ToString
public class AddCourseDto {


    /**
     * 课程名称
     */
    @ApiModelProperty(value = "课程名称", required = true)
    @NotBlank(message = "课程名称不能为空")
    private String name;

    /**
     * 适用人群
     */
    @ApiModelProperty(value = "适用人群", required = true)
    @Size(message = "适用人群内容长度不能少于10个字符", min = 10)
    @NotBlank(message = "适用人群不能为空")
    private String users;

    /**
     * 课程标签
     */
    @ApiModelProperty(value = "课程标签")
    private String tags;

    /**
     * 大分类
     */
    @ApiModelProperty(value = "大分类", required = true)
    @NotBlank(message = "课程分类不能为空")
    private String mt;

    /**
     * 小分类
     */
    @ApiModelProperty(value = "小分类", required = true)
    @NotBlank(message = "课程分类不能为空")
    private String st;

    /**
     * 课程等级
     */
    @ApiModelProperty(value = "课程等级", required = true)
    @NotBlank(message = "课程等级不能为空")
    private String grade;

    /**
     * 教学模式
     */
    @ApiModelProperty(value = "教学模式（普通，录播，直播等）", required = true)
    @NotBlank(message = "教学模式不能为空")
    private String teachmode;

    /**
     * 课程介绍
     */
    @ApiModelProperty(value = "课程介绍")
    private String description;

    /**
     * 课程图片
     */
    @ApiModelProperty(value = "课程图片", required = true)
    private String pic;

    /**
     * 收费规则
     */
    @ApiModelProperty(value = "收费规则,对应数据字典", required = true)
    @NotBlank(message = "收费规则不能为空")
    private String charge;

    /**
     * 价格
     */
    @ApiModelProperty(value = "价格")
    @NotNull(message = "价格不能为空")
    @PositiveOrZero(message = "价格必须大于等于0")
    private Float price;

    /**
     * 原价
     */
    @ApiModelProperty(value = "原价")
    @NotNull(message = "原价不能为空")
    @PositiveOrZero(message = "原价必须大于等于0")
    private Float originalPrice;

    /**
     * qq
     */
    @ApiModelProperty(value = "qq")
    private String qq;

    /**
     * 微信
     */
    @ApiModelProperty(value = "微信")
    private String wechat;

    /**
     * 电话
     */
    @ApiModelProperty(value = "电话")
    @Phone
    private String phone;

    /**
     * 有效期
     */
    @ApiModelProperty(value = "有效期")
    @NotNull(message = "有效期不能为空")
    @Min(value = 1, message = "有效期天数必须大于等于1")
    private Integer validDays;
}

