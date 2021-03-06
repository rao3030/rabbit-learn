package example.demo.rejectmsg;


import com.rabbitmq.client.*;
import example.demo.exchange.direct.DirectProducer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**

 *类说明：普通的消费者
 */
public class RejectRequeuConsumer {

    public static void main(String[] argv)
            throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");

        // 打开连接和创建频道，与发送端一样
        Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();
        channel.exchangeDeclare(DirectProducer.EXCHANGE_NAME,
                "direct");

        /*声明一个队列*/
        String queueName = "rejectrequeue";
        channel.queueDeclare(queueName,false,false,
                false,null);

        /*绑定，将队列和交换器通过路由键进行绑定*/
        String routekey = "error";/*表示只关注error级别的日志消息*/
        channel.queueBind(queueName,DirectProducer.EXCHANGE_NAME,routekey);

        System.out.println("waiting for message........");

        /*声明了一个消费者*/
        final Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                try{
                    String message = new String(body, "UTF-8");
                    System.out.println("Received["
                            +envelope.getRoutingKey()
                            +"]"+message);
                    throw new RuntimeException("处理异常"+message);
                }catch (Exception e){
                    e.printStackTrace();
                    //TODO Reject方式拒绝(这里第2个参数决定是否重新投递) , 将requeue设置为true的话，会死循环
                    channel.basicReject(envelope.getDeliveryTag(),false);

                    //TODO Nack方式的拒绝（第2个参数决定是否批量）
//                    channel.basicNack(envelope.getDeliveryTag(), false, false);
                }

            }
        };
        /*消费者正式开始在指定队列上消费消息*/
        channel.basicConsume(queueName,false,consumer);


    }

}
