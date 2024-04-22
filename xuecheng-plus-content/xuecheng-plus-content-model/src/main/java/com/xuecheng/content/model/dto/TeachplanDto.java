package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
* @description 课程计划响应dto
* @author TMC
* @date 2023/2/8 10:26
* @version 1.0
*/
@Data
@ToString
public class TeachplanDto extends Teachplan {

    /**
     * 课程计划关联的媒资
     */
    TeachplanMedia teachplanMedia;

    /**
     * 课程计划子节点集
     */
    List<TeachplanDto> teachPlanTreeNodes;


}
