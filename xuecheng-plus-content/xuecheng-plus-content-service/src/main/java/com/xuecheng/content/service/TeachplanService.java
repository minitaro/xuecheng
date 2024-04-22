package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.TeachplanMedia;

import java.util.List;

/**
* @description 课程计划服务
* @author TMC
* @date 2023/2/8 10:45
* @version 1.0
*/
public interface TeachplanService {

    /**
    * @description 根据课程id查询课程计划
    * @param courseId 课程id
    * @return java.util.List<com.xuecheng.content.model.dto.TeachplanDto>
    * @author TMC
    * @date 2023/2/8 12:01
    */
    List<TeachplanDto> getTeachplanById(Long courseId);


    /**
    * @description 课程计划增改
    * @param saveTeachplanDto 增改课程计划请求dto
    * @return java.util.List<com.xuecheng.content.model.dto.TeachplanDto>
    * @author TMC
    * @date 2023/2/8 12:02
    */
    void saveTeachplan(SaveTeachplanDto saveTeachplanDto);


    /**
    * @description 删除指定id的课程计划
    * @param id 课程计划id
    * @return void
    * @author TMC
    * @date 2023/2/8 22:06
    */
    void deleteTeachplanById(Long id);

    /**
    * @description 向上移动指定id的课程计划
    * @param id 课程计划id
    * @return void
    * @author TMC
    * @date 2023/2/9 14:24
    */
    void moveUpTeachplan(Long id);

    /**
    * @description 向下移动指定id的课程计划
    * @param id 课程计划id
    * @return void
    * @author TMC
    * @date 2023/2/9 14:25
    */
    void moveDownTeachplan(Long id);


    /**
    * @description 教学计划与媒资绑定
    * @param bindTeachplanMediaDto 教学计划绑定媒资请求dto
    * @return com.xuecheng.content.model.po.TeachplanMedia
    * @author TMC
    * @date 2023/2/14 15:38
    */
    TeachplanMedia bindTeachplanMedia(BindTeachplanMediaDto bindTeachplanMediaDto);

    /**
    * @description 教学计划与媒资解绑
    * @param teachplanId 教学计划id
     * @param mediaId 媒资id
    * @return void
    * @author TMC
    * @date 2023/2/14 22:28
    */
    void unbindTeachplanMedia(Long teachplanId, String mediaId);
}
