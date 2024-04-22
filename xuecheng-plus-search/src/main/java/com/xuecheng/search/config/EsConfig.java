package com.xuecheng.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
* @description Elasticsearch配置类
* @author TMC
* @date 2023/2/18 11:01
* @version 1.0
*/
@Configuration
public class EsConfig {

    @Value("${elasticsearch.hostlist}")
    private String[] hosts;

    @Bean
    public RestHighLevelClient restHighLevelClient(){

        HttpHost[] httpHosts = Arrays.stream(hosts).map(HttpHost::create).toArray(HttpHost[]::new);

        return new RestHighLevelClient(RestClient.builder(httpHosts));
    }

}
