package com.xuecheng.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;

import java.util.List;


public interface TeachplanMapper extends BaseMapper<Teachplan> {

    /**
    * @description 查询指定id课程的课程计划(树形结构)
    * @param courseId 课程id
    * @return java.util.List<com.xuecheng.content.model.dto.TeachplanDto>
    * @author TMC
    * @date 2023/2/8 19:17
    */
    List<TeachplanDto> selectTreeNodes(Long courseId);

}
