package example.demo.setmsg;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**

 *类说明：消息的属性的控制
 */
public class ReplyToConsumer {

    public static void main(String[] argv)
            throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");

        // 打开连接和创建频道，与发送端一样
        Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();
        /*创建交换器*/
        channel.exchangeDeclare(ReplyToProducer.EXCHANGE_NAME,
                "direct",false);

        /*声明一个队列*/
        String queueName = "replyto";
        channel.queueDeclare(queueName,false,false,
                false,null);

        /*绑定，将队列和交换器通过路由键进行绑定*/
        String routekey = "error";/*表示只关注error级别的日志消息*/
        channel.queueBind(queueName,ReplyToProducer.EXCHANGE_NAME,routekey);

        System.out.println("waiting for message........");

        /*声明了一个消费者*/
        final Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received["+envelope.getRoutingKey()
                        +"]"+message);
                AMQP.BasicProperties respProp
                        = new AMQP.BasicProperties.Builder()
                        .replyTo(properties.getReplyTo())
                        .correlationId(properties.getMessageId())
                        .build();

                channel.basicPublish("", respProp.getReplyTo() ,
                        respProp ,
                        ("Hi,"+message).getBytes("UTF-8"));
           }
        };
        /*消费者正式开始在指定队列上消费消息*/
        channel.basicConsume(queueName,true,consumer);


    }

}
