package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XcException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
* @description 课程计划服务实现类
* @author TMC
* @date 2023/2/8 10:49
* @version 1.0
*/
@Slf4j
@Service
public class TeachplanServiceImpl implements TeachplanService {

    @Resource
    TeachplanMapper teachplanMapper;

    @Resource
    TeachplanMediaMapper teachplanMediaMapper;

    @Override
    public List<TeachplanDto> getTeachplanById(Long courseId) {

        //1.查询指定课程id下的所有课程计划
        List<TeachplanDto> firstNodes = teachplanMapper.selectTreeNodes(courseId);
        if (firstNodes != null && firstNodes.size() > 0) {
            //2.过滤已经逻辑删除的课程计划
            firstNodes = firstNodes.stream()
                    .filter(firstNode -> firstNode.getStatus() == 1)
                    .peek(firstNode -> {
                        List<TeachplanDto> secondNodes = firstNode.getTeachPlanTreeNodes().stream()
                                .filter(secondNode -> secondNode.getStatus() == 1)
                                .collect(Collectors.toList());
                        firstNode.setTeachPlanTreeNodes(secondNodes);
                    })
                    .collect(Collectors.toList());
        }
        return firstNodes;

    }

    @Transactional
    @Override
    public void saveTeachplan(SaveTeachplanDto saveTeachplanDto) {

        Long teachplanId = saveTeachplanDto.getId();

        if (teachplanId != null) {
            //1.修改课程计划
            Teachplan teachplanUpdate = teachplanMapper.selectById(teachplanId); //1.1.查询po
            if (teachplanUpdate == null) {
                XcException.cast("该课程计划不存在");
            }
            BeanUtils.copyProperties(saveTeachplanDto, teachplanUpdate); //1.2.修改po
            int updateTeachplan = teachplanMapper.updateById(teachplanUpdate); //1.3.提交po
            if (updateTeachplan < 1) {
                log.error("修改课程计划出错:{}", teachplanUpdate);
                XcException.cast("保存课程计划失败");
            }
        } else {
            //2.新增课程计划
            Teachplan teachplanAdd = new Teachplan(); //2.1.新增po
            BeanUtils.copyProperties(saveTeachplanDto, teachplanAdd); //2.2.po赋值
            Long courseId = saveTeachplanDto.getCourseId();
            Long parentid = saveTeachplanDto.getParentid();
            int count = this.getTeachplanCount(courseId, parentid); //获取章节排序号
            teachplanAdd.setOrderby(count + 1);  //2.3.设置课程计划的章节排序
            int insertTeachplan = teachplanMapper.insert(teachplanAdd); //2.4.提交po
            if (insertTeachplan < 1) {
                log.error("新增课程计划出错:{}", teachplanAdd);
                XcException.cast("保存课程计划失败");
            }
        }

    }

    @Transactional
    @Override
    public void deleteTeachplanById(Long id) {

        //1.查询待删除课程计划
        Teachplan teachplanDelete = teachplanMapper.selectById(id);
        if (teachplanDelete == null) {
            XcException.cast("该课程计划不存在");
        }

        //2.查询待删除课程计划是否含有子级信息
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, teachplanDelete.getCourseId());
        queryWrapper.eq(Teachplan::getParentid, teachplanDelete.getId());
        queryWrapper.eq(Teachplan::getStatus, 1);
        List<Teachplan> teachplans = teachplanMapper.selectList(queryWrapper);
        if (teachplans != null && teachplans.size() > 0) {
            XcException.cast("该课程计划信息还有子级信息,无法删除");
        }

        //3.设置待删除课程计划为删除状态(0)并保存
        teachplanDelete.setStatus(0);
        int deleteTeachplan = teachplanMapper.updateById(teachplanDelete);
        if (deleteTeachplan < 1) {
            log.error("删除课程计划出错:{}", deleteTeachplan);
            XcException.cast("删除课程计划失败");
        }

        //4.将已删除课程计划关联的视频信息也删除
        LambdaQueryWrapper<TeachplanMedia> mediaQueryWrapper = new LambdaQueryWrapper<>();
        mediaQueryWrapper.eq(TeachplanMedia::getTeachplanId, teachplanDelete.getId());
        teachplanMediaMapper.delete(mediaQueryWrapper);

    }

    @Transactional
    @Override
    public void moveUpTeachplan(Long id) {

        //1.查询待上移的课程计划
        Teachplan teachplanMoveUp = teachplanMapper.selectById(id);
        if (teachplanMoveUp == null) {
            XcException.cast("该课程计划不存在");
        }

        if (teachplanMoveUp.getOrderby() > 1) {
            //2.查询待下移的课程计划
            LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Teachplan::getCourseId, teachplanMoveUp.getCourseId());
            queryWrapper.eq(Teachplan::getParentid, teachplanMoveUp.getParentid());
            queryWrapper.eq(Teachplan::getOrderby, teachplanMoveUp.getOrderby() - 1);
            List<Teachplan> teachplans = teachplanMapper.selectList(queryWrapper);
            if (teachplans != null && teachplans.size() > 0) {
                Teachplan teachplanMoveDown = teachplans.get(0);

                //3.交换课程计划的排序字段
                int order = teachplanMoveUp.getOrderby();
                teachplanMoveUp.setOrderby(teachplanMoveDown.getOrderby());
                teachplanMoveDown.setOrderby(order);

                //4.保存更改信息
                teachplanMoveUp.setChangeDate(LocalDateTime.now());
                teachplanMoveDown.setChangeDate(LocalDateTime.now());
                int moveUp = teachplanMapper.updateById(teachplanMoveUp);
                int moveDown = teachplanMapper.updateById(teachplanMoveDown);
                if (moveUp < 1 || moveDown < 1) {
                    XcException.cast("课程计划上移失败");
                }
            }
        }
    }

    @Transactional
    @Override
    public void moveDownTeachplan(Long id) {
        //1.查询待下移的课程计划
        Teachplan teachplanMoveDown = teachplanMapper.selectById(id);
        if (teachplanMoveDown == null) {
            XcException.cast("该课程计划不存在");
        }

        int count = this.getTeachplanCount(teachplanMoveDown.getCourseId(), teachplanMoveDown.getParentid());
        if (teachplanMoveDown.getOrderby() < count) {
            //2.查询待上移的课程计划
            LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Teachplan::getCourseId, teachplanMoveDown.getCourseId());
            queryWrapper.eq(Teachplan::getParentid, teachplanMoveDown.getParentid());
            queryWrapper.eq(Teachplan::getOrderby, teachplanMoveDown.getOrderby() + 1);
            List<Teachplan> teachplans = teachplanMapper.selectList(queryWrapper);
            if (teachplans != null && teachplans.size() > 0) {
                Teachplan teachplanMoveUp = teachplans.get(0);

                //3.交换课程计划的排序字段
                int order = teachplanMoveDown.getOrderby();
                teachplanMoveDown.setOrderby(teachplanMoveUp.getOrderby());
                teachplanMoveUp.setOrderby(order);

                //4.保存课程计划的更改
                teachplanMoveDown.setChangeDate(LocalDateTime.now());
                teachplanMoveUp.setChangeDate(LocalDateTime.now());
                int moveUp = teachplanMapper.updateById(teachplanMoveUp);
                int moveDown = teachplanMapper.updateById(teachplanMoveDown);
                if (moveUp < 1 || moveDown < 1) {
                    XcException.cast("课程计划下移失败");
                }
            }
        }
    }

    @Transactional
    @Override
    public TeachplanMedia bindTeachplanMedia(BindTeachplanMediaDto bindTeachplanMediaDto) {

        //1.请求dto数据拆封
        Long teachplanId = bindTeachplanMediaDto.getTeachplanId(); //教学计划id
        String mediaId = bindTeachplanMediaDto.getMediaId();       //媒资id
        String fileName = bindTeachplanMediaDto.getFileName();     //媒资文件名称

        //2.约束校验
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        if (teachplan == null) {
            XcException.cast("课程计划不存在");
        }
        if (teachplan.getGrade() != 2) {
            XcException.cast("只允许二级目录绑定视频");
        }

        //3.解除已有绑定
        LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeachplanMedia::getTeachplanId, teachplanId);
        teachplanMediaMapper.delete(queryWrapper);

        //4.设置绑定信息并插入记录
        TeachplanMedia teachplanMedia = new TeachplanMedia();
        teachplanMedia.setTeachplanId(teachplanId);
        teachplanMedia.setMediaId(mediaId);
        teachplanMedia.setMediaFilename(fileName);
        teachplanMedia.setCourseId(teachplan.getCourseId());
        int insert = teachplanMediaMapper.insert(teachplanMedia);
        if (insert < 1) {
            log.error("添加教学计划与媒资绑定记录出错");
            XcException.cast("课程计划添加视频失败");
        }
        return teachplanMedia;
    }

    @Transactional
    @Override
    public void unbindTeachplanMedia(Long teachplanId, String mediaId) {
        LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeachplanMedia::getTeachplanId, teachplanId);
        queryWrapper.eq(TeachplanMedia::getMediaId, mediaId);
        int deleteTeachplanMedia = teachplanMediaMapper.delete(queryWrapper);
        if (deleteTeachplanMedia < 1) {
            log.error("课程计划与媒资解绑出错");
            XcException.cast("课程计划与媒资解绑失败");
        }
    }

    /**
     * @param courseId 课程id
     * @param parentid 父节点id
     * @return int
     * @description 获取最新的排序号
     * @author TMC
     * @date 2023/2/8 21:43
     */
    private int getTeachplanCount(Long courseId, Long parentid) {

        //1.构建查询条件
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getParentid, parentid); //1.1.根据父节点id查询
        queryWrapper.eq(Teachplan::getCourseId, courseId); //1.2.根据课程id查询

        //2.查询同一门课程同一章下的课程计划数量并返回
        return teachplanMapper.selectCount(queryWrapper);

    }
}
