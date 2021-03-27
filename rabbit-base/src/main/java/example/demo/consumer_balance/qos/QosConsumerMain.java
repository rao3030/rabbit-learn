package example.demo.consumer_balance.qos;


import com.rabbitmq.client.*;
import example.demo.exchange.direct.DirectProducer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 类说明：普通的消费者
 */
public class QosConsumerMain {

    public static void main(String[] argv)
            throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");

        // 打开连接和创建频道，与发送端一样
        Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();
        channel.exchangeDeclare(DirectProducer.EXCHANGE_NAME, "direct");

        /*声明一个队列*/
        String queueName = "focuserror";
        channel.queueDeclare(queueName, false, false, false, null);

        /*绑定，将队列和交换器通过路由键进行绑定*/
        String routekey = "error";/*表示只关注error级别的日志消息*/
        channel.queueBind(queueName, DirectProducer.EXCHANGE_NAME, routekey);


        System.out.println("......waiting for message........");

        /*声明了一个消费者*/
        final Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received[" + envelope.getRoutingKey()
                        + "]" + message);
                //TODO 单条确认 第二个参数是false,true代表批量确认
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };

        //TODO 如果是两个消费者(QOS ,批量)则轮询获取数据

        //TODO 150条预取(150都取出来 150， 210-150  60  ) 假如有500条，Qos 150 150 150 50 这样批量处理，跟自定义的BatchAckConsumer类似
        channel.basicQos(150, true);
        /*消费者正式开始在指定队列上消费消息*/
        channel.basicConsume(queueName, false, consumer);
        //TODO 自定义消费者批量确认
        //BatchAckConsumer batchAckConsumer = new BatchAckConsumer(channel);
        //channel.basicConsume(queueName,false,batchAckConsumer);


    }

}
