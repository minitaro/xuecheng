package com.xuecheng.search.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.search.dto.SearchCourseParamDto;
import com.xuecheng.search.dto.SearchPageResultDto;
import com.xuecheng.search.po.CourseIndex;

/**
* @description 课程搜索服务
* @author TMC
* @date 2023/2/18 12:25
* @version 1.0
*/
public interface CourseSearchService {

    /**
    * @description 搜索课程列表
    * @param pageParams 分页参数
     * @param searchCourseParamDto 搜索课程请求dto
    * @return com.xuecheng.search.dto.SearchPageResultDto<com.xuecheng.search.po.CourseIndex>
    * @author TMC
    * @date 2023/2/18 12:27
    */
    SearchPageResultDto<CourseIndex> queryCoursePubIndex(PageParams pageParams, SearchCourseParamDto searchCourseParamDto);
}
