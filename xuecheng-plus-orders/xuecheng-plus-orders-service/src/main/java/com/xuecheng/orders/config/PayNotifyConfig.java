package com.xuecheng.orders.config;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
* @description 支付通知配置类
* @author TMC
* @date 2023/3/8 1:53
* @version 1.0
*/
@Configuration
public class PayNotifyConfig {

    public static final String PAYNOTIFY_EXCHANGE_FANOUT = "paynotify_exchange_fanout";

    public static final String PAYNOTIFY_REPLY_QUEUE = "paynotify_reply_queue";

    public static final String MESSAGE_TYPE = "payresult_notify"; //通知消息类型

    //交换机
    @Bean(PAYNOTIFY_EXCHANGE_FANOUT)
    public FanoutExchange paynotify_exchange_fanout(){
        // 三个参数：交换机名称、是否持久化、当没有queue与其绑定时是否自动删除
        return new FanoutExchange(PAYNOTIFY_EXCHANGE_FANOUT, true, false);
    }

    //回复队列
    @Bean(PAYNOTIFY_REPLY_QUEUE)
    public Queue msgnotify_result_queue(){
        return QueueBuilder.durable(PAYNOTIFY_REPLY_QUEUE).build();
    }

}
