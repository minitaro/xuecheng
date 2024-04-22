package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.po.CoursePublish;

import java.io.File;

/**
* @description 课程发布服务
* @author TMC
* @date 2023/2/16 16:18
* @version 1.0
*/
public interface CoursePublishService {

    /**
    * @description 预览课程
    * @param courseId 课程id
    * @return com.xuecheng.content.model.dto.CoursePreviewDto
    * @author TMC
    * @date 2023/2/17 11:05
    */
    CoursePreviewDto previewCourse(Long courseId);

    /**
    * @description 课程提交审核
    * @param companyId 机构id
     * @param courseId 课程id
    * @return void
    * @author TMC
    * @date 2023/2/17 11:07
    */
    void commitAudit(Long companyId, Long courseId);

    /**
    * @description 发布课程
    * @param companyId 机构id
     * @param courseId 课程id
    * @return void
    * @author TMC
    * @date 2023/2/17 14:24
    */
    void publishCourse(Long companyId, Long courseId);

    /**
    * @description 生成课程静态化页面
    * @param courseId 课程id
    * @return java.io.File
    * @author TMC
    * @date 2023/2/18 0:12
    */
    File generateCourseHtml(Long courseId);

    /**
    * @description 上传课程静态页面到文件系统
    * @param courseId 课程id
     * @param file 待上传静态化文件
    * @return void
    * @author TMC
    * @date 2023/2/18 0:13
    */
    void uploadCourseHtml(Long courseId, File file);


    /**
    * @description 保存课程索引至搜索引擎数据库
    * @param courseId 课程id
    * @return java.lang.Boolean
    * @author TMC
    * @date 2023/2/18 17:04
    */
    Boolean saveCourseIndex(Long courseId);

    /**
    * @description 查询课程发布信息
    * @param courseId 课程id
    * @return com.xuecheng.content.model.po.CoursePublish
    * @author TMC
    * @date 2023/2/24 21:03
    */
    CoursePublish getCoursePublish(Long courseId);

}
