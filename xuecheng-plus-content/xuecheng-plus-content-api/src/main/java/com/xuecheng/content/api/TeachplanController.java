package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
* @description 课程计划控制器
* @author TMC
* @date 2023/2/8 10:33
* @version 1.0
*/
@Api(value = "课程计划控制器", tags = {"课程计划控制器"})
@RestController
public class TeachplanController {

    @Resource
    TeachplanService teachplanService;

    @ApiOperation("根据课程id查询课程计划接口")
    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachplanDto> getTeachplanById(@PathVariable Long courseId) {
        return teachplanService.getTeachplanById(courseId);
    }

    @ApiOperation("课程计划增改接口")
    @PostMapping("/teachplan")
    public void saveTeachplan(@RequestBody @Validated SaveTeachplanDto saveTeachplanDto) {

        teachplanService.saveTeachplan(saveTeachplanDto);
    }

    @ApiOperation("根据id删除课程计划接口")
    @DeleteMapping("/teachplan/{id}")
    public void deleteTeachplanById(@PathVariable Long id) {

        teachplanService.deleteTeachplanById(id);

    }

    @ApiOperation("向上移动指定id的课程计划接口")
    @PostMapping("/teachplan/moveup/{id}")
    public void moveUpTeachplan(@PathVariable Long id) {

        teachplanService.moveUpTeachplan(id);

    }

    @ApiOperation("向下移动指定id的课程计划接口")
    @PostMapping("/teachplan/movedown/{id}")
    public void moveDownTeachplan(@PathVariable Long id) {

        teachplanService.moveDownTeachplan(id);

    }

    @ApiOperation("课程计划与媒资绑定接口")
    @PostMapping("/teachplan/association/media")
    public TeachplanMedia bindTeachplanMedia(@RequestBody @Validated BindTeachplanMediaDto bindTeachplanMediaDto) {

        return this.teachplanService.bindTeachplanMedia(bindTeachplanMediaDto);

    }

    @ApiOperation("课程计划与媒资解绑接口")
    @DeleteMapping("/teachplan/association/media/{teachplanId}/{mediaId}")
    public void unbindTeachplanMedia(@PathVariable Long teachplanId, @PathVariable String mediaId) {

        this.teachplanService.unbindTeachplanMedia(teachplanId, mediaId);

    }



}
