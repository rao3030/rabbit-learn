package example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class RabbitmqSender {
    @Autowired
    private RabbitTemplate amqpTemplate;

    public void sendMessage(String exchange,String roukekey,String content) {
        try {
            log.info("============发送消息=========" + content);
            amqpTemplate.convertAndSend(exchange, roukekey,
                    content);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String exchange, String roukekey, String content, CorrelationData correlationData) {
        try {
            log.info("============发送消息=========" + content);
            amqpTemplate.convertAndSend(exchange, roukekey,
                    content,correlationData);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
