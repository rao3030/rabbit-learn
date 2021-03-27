package example.demo.rabbit;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ReceiveMessageHandler {

    @RabbitListener(queues = {"boot-queue"})
    public void receive_email(Object msg, Message message, Channel channel) {
        System.out.println("boot-queue receive the message--" + new String(message.getBody()));
    }

}
