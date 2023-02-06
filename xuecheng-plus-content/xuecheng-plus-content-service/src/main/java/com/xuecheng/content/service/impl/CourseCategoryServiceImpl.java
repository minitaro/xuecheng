package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
* @description 课程分类树形结构查询业务接口实现类
* @author TMC
* @date 2023/2/6 18:59
* @version 1.0
*/
@Slf4j
@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {

    @Autowired
    CourseCategoryMapper courseCategoryMapper;
    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {

        //1.查询得到根结点下所有层级的子结点
        List<CourseCategoryTreeDto> categoryTreeDtos = courseCategoryMapper.selectTreeNodes(id);
        //2.封装结果集
        //2.1.定义List作为最终返回的数据
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = new ArrayList<>();
        //2.2.为了方便找子结点的父结点，定义map
        HashMap<String, CourseCategoryTreeDto> nodeMap = new HashMap<>();
        //2.3.将数据封装到List
        categoryTreeDtos.stream().forEach(item->{
            nodeMap.put(item.getId(), item);
            //2.3.1.只将根节点的下级节点放入list
            if (item.getParentid().equals(id)) {
                courseCategoryTreeDtos.add(item);
            }
            //2.3.2.节点的下级节点集属性赋值
            CourseCategoryTreeDto parentNode = nodeMap.get(item.getParentid());
            if (parentNode != null) {
                if (parentNode.getChildrenTreeNodes() == null) {
                    parentNode.setChildrenTreeNodes(new ArrayList<CourseCategoryTreeDto>());
                }
                parentNode.getChildrenTreeNodes().add(item);
            }
        });

        //3.返回的list只包括了根结点的直接下属结点
        return courseCategoryTreeDtos;
    }
}
