package com.xuecheng.search.service;
/**
* @description 索引服务
* @author TMC
* @date 2023/2/18 11:37
* @version 1.0
*/
public interface IndexService {


    /**
    * @description 添加索引
    * @param indexName 索引名称
     * @param id 主键
     * @param object 索引对象
    * @return java.lang.Boolean
    * @author TMC
    * @date 2023/2/18 11:40
    */
    Boolean addIndex(String indexName, String id, Object object);

}
