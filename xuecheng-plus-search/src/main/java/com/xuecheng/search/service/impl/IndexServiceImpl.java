package com.xuecheng.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.xuecheng.search.service.IndexService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

/**
* @description 索引服务实现类
* @author TMC
* @date 2023/2/18 11:42
* @version 1.0
*/
@Service
@Slf4j
public class IndexServiceImpl implements IndexService {

    @Resource
    RestHighLevelClient client;

    @Override
    public Boolean addIndex(String indexName, String id, Object object) {

        //1.索引对象转为json格式
        String jsonObject = JSON.toJSONString(object);

        //2.创建添加索引请求
        IndexRequest indexRequest = new IndexRequest(indexName).id(id).source(jsonObject, XContentType.JSON);

        //3.创建索引响应对象
        IndexResponse indexResponse = null;

        //4.添加索引
        try {
            indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("添加索引出错:{}", e.getMessage());
            return false;
        }

        //5.添加索引的响应结果
        String name = indexResponse.getResult().name();
        return name.equalsIgnoreCase("created") || name.equalsIgnoreCase("updated");
    }
}
