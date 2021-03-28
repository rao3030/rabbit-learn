package example.demo.producer_balance.confirm;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 类说明：生产者——发送方确认模式--批量确认，比一般确认XIAO
 */
public class ProducerBatchConfirm {

    public final static String EXCHANGE_NAME = "producer_wait_confirm";
    private final static String ROUTE_KEY = "king";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        /**
         * 创建连接连接到RabbitMQ
         */
        ConnectionFactory factory = new ConnectionFactory();
        // 设置RabbitMQ所在主机ip或者主机名
        factory.setHost("127.0.0.1");
        // 创建一个连接
        Connection connection = factory.newConnection();
        // 创建一个信道
        Channel channel = connection.createChannel();
        // 指定转发
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        //TODO
        // 添加失败通知监听器
        channel.addReturnListener(new ReturnListener() {
            public void handleReturn(int replyCode, String replyText,
                                     String exchange, String routingKey,
                                     AMQP.BasicProperties properties,
                                     byte[] body)
                    throws IOException {
                String message = new String(body);
                System.out.println("RabbitMq返回的replyCode:  " + replyCode);
                System.out.println("RabbitMq返回的replyText:  " + replyText);
                System.out.println("RabbitMq返回的exchange:  " + exchange);
                System.out.println("RabbitMq返回的routingKey:  " + routingKey);
                System.out.println("RabbitMq返回的message:  " + message);
            }
        });
        //TODO
        // 启用发送者确认模式，-->channel.basicPublish(EXCHANGE_NAME, ROUTE_KEY, true, null, message.getBytes());就不会自动把消息发送到rabbitmq
        // 将这些message缓存到一个容器中<A>
        channel.confirmSelect();

        //所有日志严重性级别
        for (int i = 0; i < 10; i++) {
            // 发送的消息
            String message = "Hello World_" + (i + 1);
            //参数1：exchange name
            //参数2：routing key
            channel.basicPublish(EXCHANGE_NAME, ROUTE_KEY, true, null, message.getBytes());
            System.out.println(" Sent Message: [" + ROUTE_KEY + "]:'" + message + "'");
        }
        //TODO
        // 启用发送者确认模式（批量确认）
        // 将容器<A>的消息发送到rabbitmq中，如果message没发送到rabbitmq的queue中，就会回调channel.addReturnListener的方法
        channel.waitForConfirmsOrDie();//todo channel.waitForConfirmsOrDie()等待channel.basicPublish执行完成之后，才会往下走，同步
        // 关闭频道和连接
        channel.close();
        connection.close();
    }

}
