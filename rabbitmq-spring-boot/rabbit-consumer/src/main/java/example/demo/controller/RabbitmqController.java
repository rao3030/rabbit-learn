package example.demo.controller;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class RabbitmqController {
    @Autowired
    RabbitTemplate rabbitTemplate;

    //http://localhost:8111/sendMessage
    @RequestMapping("/sendMessage")
    public String sendMessage() {
        rabbitTemplate.send("boot-exchange", "boot-key", new Message("哇哈哈哈-大千世界".getBytes(), new MessageProperties()));
        return "send message succeed";
    }
}
