package com.xuecheng.base.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
* @description 分页查询响应模型类
* @author TMC
* @date 2023/2/5 17:11
* @version 1.0
*/
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> implements Serializable {

    /**
     * 查询记录列表
     */
    private List<T> items;

    /**
     * 总记录数
     */
    private long counts;

    /**
     * 当前页码
     */
    private long page;

    /**
     * 每页记录数
     */
    private long pageSize;

}
