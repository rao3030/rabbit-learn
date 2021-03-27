package example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

@Configuration
public class RabbitmqConfig {

    //声明交换机
    @Bean
    public Exchange bootExchange(){
        //durable(true) 持久化，mq重启之后交换机还在
        return ExchangeBuilder.topicExchange("boot-exchange").durable(false).build();
    }

    @Bean
    public Queue bootQueue(){
        return new Queue("boot-queue");
    }

    @Bean
    public Binding BINDING_QUEUE_INFORM_EMAIL(@Qualifier("bootQueue") Queue queue,
                                              @Qualifier("bootExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("boot-key").noargs();
    }
}
