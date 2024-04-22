package com.xuecheng.orders.jobhandler;

import com.alibaba.fastjson.JSON;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MessageProcessAbstract;
import com.xuecheng.messagesdk.service.MqMessageService;
import com.xuecheng.orders.config.PayNotifyConfig;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
* @description 支付通知任务类
* @author TMC
* @date 2023/3/1 14:19
* @version 1.0
*/
@Component
@Slf4j
public class PayNotifyTask extends MessageProcessAbstract {

    @Resource
    RabbitTemplate rabbitTemplate;

    @Resource
    MqMessageService mqMessageService;

    //任务调度入口
    @XxlJob("NotifyPayResultJobHandler")
    public void notifyPayResultJobHandler() throws Exception {
        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        log.debug("shardIndex=" + shardIndex + ",shardTotal=" + shardTotal);
        //只查询支付通知的消息
        process(shardIndex, shardTotal, PayNotifyConfig.MESSAGE_TYPE, 100, 60);
    }

    //执行任务的具体方法
    @Override
    public boolean execute(MqMessage mqMessage) {
        log.debug("向消息队列发送支付结果通知消息:{}", mqMessage);

        //发布消息
        this.send(mqMessage);

        return false;
    }

    /**
     * @description 发送支付结果通知
     * @param message 消息内容
     * @return void
     * @author Mr.M
     * @date 2022/9/20 9:43
     */
    private void send(MqMessage message){
        //要发送的消息
        String msgJsonString = JSON.toJSONString(message);

        //开始发送消息,使用fanout交换机,通过广播模式发送消息
        rabbitTemplate.convertAndSend(PayNotifyConfig.PAYNOTIFY_EXCHANGE_FANOUT, "", msgJsonString);

        log.debug("向消息队列发送支付结果通知消息完成:{}", message);

    }

    //监听回复队列,接收回复
    @RabbitListener(queues = PayNotifyConfig.PAYNOTIFY_REPLY_QUEUE)
    public void receive(String message) {
        log.debug("收到支付结果通知回复:{}", message);
        MqMessage mqMessage = JSON.parseObject(message, MqMessage.class);
        //完成消息发送,最终将该消息删除
        mqMessageService.completed(mqMessage.getId());
    }

}
