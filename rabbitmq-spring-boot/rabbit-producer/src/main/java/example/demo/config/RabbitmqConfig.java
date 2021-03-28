package example.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Configuration
@Slf4j
public class RabbitmqConfig {

    /**
     * durable 持久化消息队列，默认true
     * 3、auto-delete 消息队列没有在使用时自动删除，默认false
     * 4、exclusive 是否有排他性，就是是否只允许一个消费者消费，默认false
     */
    @Bean(name = "message")
    public Queue queueMessage() {
        //todo 配置死信交换器，消费者消费这个message出错了 一般是将这个message加入到死信交换器中，而不是重新入queue
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "demo.dead.ex");//死信交换器的name
        arguments.put("x-dead-letter-routing-key", "demo.dead.rk");//死信交换器
        return new Queue("demo.queue", true, false, false, arguments);
    }

    @Bean(name = "exchange")
    public DirectExchange exchange() {
        return new DirectExchange("demo.exchange", true, false);
    }

    @Bean(name = "deadqueueMesage")
    public Queue deadqueueMesage() {
        return new Queue("demo.dead.queue", true);
    }

    @Bean(name = "deadexchange")
    public DirectExchange deadexchange() {
        return new DirectExchange("demo.dead.ex", true, false);
    }

    @Bean
    Binding bindingExchangeMessage(@Qualifier("message") Queue queueMessage,
                                   @Qualifier("exchange") DirectExchange exchange) {
        return BindingBuilder.bind(queueMessage).to(exchange).
                with("demo.routeKey");
    }

    @Bean
    Binding bindingDeadExchangeMessage(@Qualifier("deadqueueMesage") Queue queueMessage,
                                       @Qualifier("deadexchange") DirectExchange exchange) {
        return BindingBuilder.bind(queueMessage).to(exchange).
                with("demo.dead.rk");
    }



    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//        rabbitTemplate.setMessageConverter(converter());
        // 消息是否成功发送到Exchange
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("!!!!!!!!!!!!!消息成功发送到Exchange" + correlationData.getId());
            } else {
                //重新发送到mq
                log.info("!!!!!!!!!!!!!消息发送到Exchange失败, {}, cause: {}", correlationData, cause);
            }
        });

        // 触发setReturnCallback回调必须设置mandatory=true, 否则Exchange没有找到Queue就会丢弃掉消息, 而不会触发回调
        rabbitTemplate.setMandatory(true);
        // 消息是否从Exchange路由到Queue, 注意: 这是一个失败回调, 只有消息从Exchange路由到Queue失败才会回调这个方法
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            log.info("!!!!!!!!!!!!!消息从Exchange路由到Queue失败: exchange: {}, route: {}, replyCode: {}, replyText: {}, message: {}", exchange, routingKey, replyCode, replyText, message);

        });

        return rabbitTemplate;
    }
}
