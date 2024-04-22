package com.xuecheng.search.dto;

import com.xuecheng.base.model.PageResult;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
* @description 搜索课程响应dto
* @author TMC
* @date 2023/2/18 12:18
* @version 1.0
*/
@Data
@ToString
public class SearchPageResultDto<T> extends PageResult {

    //大分类列表
    List<String> mtList;

    //小分类列表
    List<String> stList;


    public SearchPageResultDto(List items, long counts, long page, long pageSize) {
        super(items, counts, page, pageSize);
    }
}
