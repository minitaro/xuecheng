package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xuecheng.base.exception.XcException;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.dto.SaveTeacherDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
* @description 课程师资服务实现类
* @author TMC
* @date 2023/2/8 15:07
* @version 1.0
*/
@Slf4j
@Service
public class CourseTeacherServiceImpl implements CourseTeacherService {

    @Resource
    CourseTeacherMapper courseTeacherMapper;

    @Resource
    CourseBaseMapper courseBaseMapper;

    @Override
    public List<CourseTeacher> queryCourseTeacherList(Long courseId) {

        //1.构造查询条件:根据课程id查询
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId, courseId);
        //2.查询课程师资列表并返回
        return courseTeacherMapper.selectList(queryWrapper);

    }

    @Transactional
    @Override
    public CourseTeacher saveCourseTeacher(Long companyId, SaveTeacherDto saveTeacherDto) {

        //1.校验权限
        //1.1.查询课程所属机构
        Long courseId = saveTeacherDto.getCourseId();
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            XcException.cast("教师教授的课程信息不存在");
        }
        else if (!companyId.equals(courseBase.getCompanyId())) {
            XcException.cast("只允许修改所属机构的课程教师信息");
        }

        Long teacherId = saveTeacherDto.getId();
        if (teacherId == null) {
            //2.添加教师信息
            CourseTeacher courseTeacher = new CourseTeacher(); //2.1.创建po
            BeanUtils.copyProperties(saveTeacherDto, courseTeacher); //2.2.复制信息
            int insert = courseTeacherMapper.insert(courseTeacher); //2.3.提交po
            if (insert < 1 ) {
                log.error("添加课程教师出错:{}", saveTeacherDto);
                XcException.cast("添加课程教师失败");
            }
            teacherId = courseTeacher.getId();
        }
        else {
            //3.修改教师信息
            CourseTeacher courseTeacher = courseTeacherMapper.selectById(teacherId);  //2.1.查询po
            BeanUtils.copyProperties(saveTeacherDto, courseTeacher);  //2.2.复制信息
            int update = courseTeacherMapper.updateById(courseTeacher); //2.3.提交po
            if (update < 1) {
                log.error("修改课程教师过程中出错:{}", saveTeacherDto);
                XcException.cast("修改课程教师失败");
            }
        }

        //4.返回修改结果
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getId, teacherId);
        queryWrapper.eq(CourseTeacher::getCourseId, courseId);
        return courseTeacherMapper.selectOne(queryWrapper);
    }

    @Transactional
    @Override
    public void deleteCourseTeacher(Long companyId, Long courseId, Long teacherId) {

        //1.校验权限
        //1.1.查询课程所属机构
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            XcException.cast("教师教授的课程信息不存在");
        }
        else if (!companyId.equals(courseBase.getCompanyId())) {
            XcException.cast("只允许修改所属机构的课程教师信息");
        }
        //2.删除指定课程教师
        LambdaUpdateWrapper<CourseTeacher> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CourseTeacher::getCourseId, courseId);
        updateWrapper.eq(CourseTeacher::getId, teacherId);
        courseTeacherMapper.delete(updateWrapper);

    }
}
