package com.xuecheng.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.search.dto.SearchCourseParamDto;
import com.xuecheng.search.dto.SearchPageResultDto;
import com.xuecheng.search.po.CourseIndex;
import com.xuecheng.search.service.CourseSearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
* @description 课程搜索接口实现类
* @author TMC
* @date 2023/2/18 12:30
* @version 1.0
*/
@Slf4j
@Service
public class CourseSearchServiceImpl implements CourseSearchService {

    @Value("${elasticsearch.course.index}")
    private String index;
    @Value("${elasticsearch.course.source_fields}")
    private String[] sourceFields;

    @Resource
    RestHighLevelClient client;

    @Override
    public SearchPageResultDto<CourseIndex> queryCoursePubIndex(PageParams pageParams, SearchCourseParamDto searchCourseParamDto) {

        //1.布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //1.1.多字段匹配关键字查询
        String keywords = searchCourseParamDto.getKeywords();
        if (StringUtils.isNotBlank(keywords)) {
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders
                    .multiMatchQuery(keywords, "name", "description") //name,description字段匹配
                    .minimumShouldMatch("70%") //设置匹配占比
                    .field("name", 10); //设置name字段10倍权重
            boolQueryBuilder.must(multiMatchQueryBuilder); //不为空,根据关键字查询
        }else {
            boolQueryBuilder.must(QueryBuilders.matchAllQuery()); //为空,查询所有
        }
        //1.2.根据课程分类与等级过滤
        String mt = searchCourseParamDto.getMt();
        if (StringUtils.isNotBlank(mt)) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("mtName", mt));
        }
        String st = searchCourseParamDto.getSt();
        if (StringUtils.isNotBlank(st)) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("stName", st));
        }
        String grade = searchCourseParamDto.getGrade();
        if (StringUtils.isNotBlank(grade)) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("grade", grade));
        }

        //2.分页参数
        Long pageNo = pageParams.getPageNo();
        Long pageSize = pageParams.getPageSize();
        int start = (int) ((pageNo - 1) * pageSize);
        int size = Math.toIntExact(pageSize);

        //3.高亮设置
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder
                .preTags("<font class='eslight'>")
                .postTags("</font>")
                .fields().add(new HighlightBuilder.Field("name")); //设置高亮字段

        //4.课程分类聚合
        TermsAggregationBuilder mtAgg = AggregationBuilders
                .terms("mtAgg")
                .field("mtName")
                .size(100);
        TermsAggregationBuilder stAgg = AggregationBuilders
                .terms("stAgg")
                .field("stName")
                .size(100);

        //5.构造搜索源
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder
                .query(boolQueryBuilder) //布尔查询
                .from(start) //页码
                .size(size) //每页记录数
                .highlighter(highlightBuilder) //高亮设置
                .fetchSource(sourceFields, new String[]{}) //查询字段
                .aggregation(mtAgg) //大分类聚合
                .aggregation(stAgg); //小分类聚合


        //6.创建搜索请求,添加搜索源
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.source(searchSourceBuilder);

        //7.搜索
        SearchResponse searchResponse = null;
        SearchPageResultDto<CourseIndex> pageResultDto = new SearchPageResultDto<>(new ArrayList<CourseIndex>(),
                0, 0, 0);
        try {
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("课程搜索异常:{}", e.getMessage());
            return pageResultDto;
        }

        //8.封装结果集返回
        SearchHits searchHits = searchResponse.getHits();
        long total = searchHits.getTotalHits().value; //文档数目
        pageResultDto.setCounts(total);
        pageResultDto.setPage(pageNo);
        pageResultDto.setPageSize(pageSize);

        List<CourseIndex> courseIndices = pageResultDto.getItems();
        SearchHit[] hits = searchHits.getHits(); //文档数组
        //遍历文档数组,将name字段内容替换为高亮字段内容
        for (SearchHit hit : hits) {
            CourseIndex courseIndex = JSON.parseObject(hit.getSourceAsString(), CourseIndex.class);
            Map<String, HighlightField> map = hit.getHighlightFields();
            if(map != null && !map.isEmpty()){
                HighlightField nameField = map.get("name");
                if(nameField != null){
                    Text[] fragments = nameField.getFragments();
                    StringBuffer buffer = new StringBuffer();
                    for (Text str : fragments) {
                        buffer.append(str.string());
                    }
                    courseIndex.setName(buffer.toString());
                }
            }
            courseIndices.add(courseIndex);
        }
        //封装查询所得课程分类
        List<String> mtList = this.getAggregation(searchResponse.getAggregations(), "mtAgg");
        List<String> stList = this.getAggregation(searchResponse.getAggregations(), "stAgg");
        pageResultDto.setMtList(mtList);
        pageResultDto.setStList(stList);
        
        return pageResultDto;
    }

    /**
    * @description 获取聚合列表
    * @param aggregations 聚合结果
     * @param aggName 聚合名称
    * @return java.util.List<java.lang.String>
    * @author TMC
    * @date 2023/3/6 3:05
    */
    private List<String> getAggregation(Aggregations aggregations, String aggName) {
        // 1.根据聚合名称获取聚合结果
        Terms terms = aggregations.get(aggName);
        // 2.获取buckets
        List<? extends Terms.Bucket> buckets = terms.getBuckets();
        // 3.遍历
        List<String> list = new ArrayList<>();
        for (Terms.Bucket bucket : buckets) {
            // 4.获取key
            String key = bucket.getKeyAsString();
            list.add(key);
        }
        return list;
    }


}
