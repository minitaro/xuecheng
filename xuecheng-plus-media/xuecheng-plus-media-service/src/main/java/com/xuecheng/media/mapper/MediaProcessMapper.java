package com.xuecheng.media.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.media.model.po.MediaProcess;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


public interface MediaProcessMapper extends BaseMapper<MediaProcess> {

    /**
    * @description 查询待处理任务
    * @param shardTotal 分片总数
     * @param shardIndex 分片序号
     * @param count 查询记录条数
    * @return java.util.List<com.xuecheng.media.model.po.MediaProcess>
    * @author TMC
    * @date 2023/2/15 17:20
    */
    @Select("SELECT * FROM media_process t WHERE t.id % #{shardTotal} = #{shardIndex} LIMIT #{count}")
    List<MediaProcess> selectListByShardIndex(@Param("shardTotal") int shardTotal, @Param("shardIndex") int shardIndex,
                                           @Param("count") int count);

}
