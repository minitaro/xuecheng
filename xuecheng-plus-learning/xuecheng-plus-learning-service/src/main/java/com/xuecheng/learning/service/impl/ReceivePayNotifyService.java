package com.xuecheng.learning.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xuecheng.base.exception.XcException;
import com.xuecheng.base.info.Dictionary;
import com.xuecheng.learning.config.PayNotifyConfig;
import com.xuecheng.learning.mapper.XcChooseCourseMapper;
import com.xuecheng.learning.model.po.XcChooseCourse;
import com.xuecheng.learning.service.CourseTableService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
* @description 接收支付通知服务
* @author TMC
* @date 2023/3/1 14:34
* @version 1.0
*/
@Slf4j
@Component
public class ReceivePayNotifyService {
    @Resource
    XcChooseCourseMapper chooseCourseMapper;

    @Resource
    CourseTableService courseTablesService;

    @Resource
    RabbitTemplate rabbitTemplate;

    //监听支付结果通知队列
    @RabbitListener(queues = {PayNotifyConfig.PAYNOTIFY_QUEUE})
    public void receive(String message) {

        //1.解析消息
        MqMessage mqMessage = JSON.parseObject(message, MqMessage.class);
        //2.判断该消息是否需要处理
        String messageType = mqMessage.getMessageType();
        String businessKey2 = mqMessage.getBusinessKey2();
        //3.只处理支付结果通知的消息,并且是学生购买课程的订单的消息
        if (PayNotifyConfig.MESSAGE_TYPE.equals(messageType) && Dictionary.Orders.OrderType_COURSE_PURCHASE.getCode().equals(businessKey2)) {

            //4.查询选课记录
            String businessKey1 = mqMessage.getBusinessKey1();
            XcChooseCourse xcChooseCourse = chooseCourseMapper.selectById(businessKey1);
            if (xcChooseCourse == null) {
                log.info("收到支付结果通知,查询不到选课记录,businessKey1:{}", businessKey1);
                return;
            }
            //5.更新选课记录
            XcChooseCourse chooseCourseUpdate = new XcChooseCourse();
            chooseCourseUpdate.setStatus(Dictionary.ChooseCourse.Status_SUCCESS.getCode());//选课成功
            int update = chooseCourseMapper.update(chooseCourseUpdate,
                    new LambdaUpdateWrapper<XcChooseCourse>().eq(XcChooseCourse::getId, businessKey1));
            if (update < 1) {
                XcException.cast("更新选课状态失败");
            }

            //6.查询最新的选课记录,更新课程表
            xcChooseCourse = chooseCourseMapper.selectById(businessKey1);
            courseTablesService.addCourseTables(xcChooseCourse);

            //7.发送回复
            this.send(mqMessage);

        }
    }

    /**
     * @description 回复消息
     * @param message  回复消息
     * @return void
     * @author Mr.M
     * @date 2022/9/20 9:43
     */
    public void send(MqMessage message){
        //转json
        String msg = JSON.toJSONString(message);
        // 发送消息
        rabbitTemplate.convertAndSend(PayNotifyConfig.PAYNOTIFY_REPLY_QUEUE, msg);
        log.debug("学习中心服务向订单服务回复消息:{}", message);
    }

}
