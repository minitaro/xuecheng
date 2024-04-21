package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @description 课程分类服务实现类
* @author TMC
* @date 2023/2/6 18:59
* @version 1.0
*/
@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {

    @Resource
    CourseCategoryMapper courseCategoryMapper;

    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {

        //1.查询得到根节点下所有节点的列表
        List<CourseCategoryTreeDto> courseCategoryDtos = courseCategoryMapper.selectTreeNodes(id);

        //2.节点列表封装成树形结果集
        List<CourseCategoryTreeDto> resultTreeDtos = new ArrayList<>(); //2.1.定义树形结果集
        Map<String, CourseCategoryTreeDto> nodeMap = new HashMap<>(); //2.2.定义map临时存储节点
        courseCategoryDtos.forEach(item -> {
            nodeMap.put(item.getId(), item); //2.3.节点储存在临时map中,方便后续获取其父节点
            if (id.equals(item.getParentid())) {
                resultTreeDtos.add(item); //2.4.将根节点的直接子节点放入树形结果集
            }
            CourseCategoryTreeDto parentNode = nodeMap.get(item.getParentid()); //2.5.获取节点的父节点
            if (parentNode != null) {
                if (parentNode.getChildrenTreeNodes() == null) {
                    parentNode.setChildrenTreeNodes(new ArrayList<>());
                }
                parentNode.getChildrenTreeNodes().add(item); //2.6.将节点存入其父节点的子节点列表
            }
        });

        //3.返回树形结果集
        return resultTreeDtos;
    }
}
