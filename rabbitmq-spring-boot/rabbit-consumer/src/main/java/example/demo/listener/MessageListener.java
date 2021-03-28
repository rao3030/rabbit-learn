package example.demo.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Slf4j
@Component
public class MessageListener {

    @RabbitListener(queues = "demo.queue")
    public void process(@Payload String message, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) {
        log.info(Thread.currentThread().getName() + "=============接收到消息=============" + message + "--" + deliveryTag);
        //这里模拟业务异常
        try {
            if ("Demo18".equals(message)) {
                int i = 1 / 0;
            }
        } catch (Exception e) {
            //丢死信队列 basicNack第三个参数，就是决定是否重新queue
            try {
                channel.basicNack(deliveryTag, false, false);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        try {
            //批量ack 如果中间的message出错了，就会丢掉这个message， 一般是交给死信交换器，而不是重新入queue
            channel.basicAck(deliveryTag, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
