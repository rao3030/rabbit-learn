package example.demo.producer_balance.confirm;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 类说明：生产者——发送方确认模式--一般确认
 */
public class ProducerConfirm {

    public final static String EXCHANGE_NAME = "producer_confirm";
    private final static String ROUTE_KEY = "king";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        /**
         * 创建连接连接到RabbitMQ
         */
        ConnectionFactory factory = new ConnectionFactory();
        // 设置MabbitMQ所在主机ip或者主机名
        factory.setHost("127.0.0.1");
        // 创建一个连接
        Connection connection = factory.newConnection();
        // 创建一个信道
        Channel channel = connection.createChannel();
        // 指定转发
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        //TODO
        // 添加失败通知监听器
//        channel.addReturnListener(new ReturnListener() {
//            public void handleReturn(int replyCode, String replyText,
//                                     String exchange, String routingKey,
//                                     AMQP.BasicProperties properties,
//                                     byte[] body)
//                    throws IOException {
//                String message = new String(body);
//                System.out.println("RabbitMq返回的replyCode:  "+replyCode);
//                System.out.println("RabbitMq返回的replyText:  "+replyText);
//                System.out.println("RabbitMq返回的exchange:  "+exchange);
//                System.out.println("RabbitMq返回的routingKey:  "+routingKey);
//                System.out.println("RabbitMq返回的message:  "+message);
//            }
//        });
        //TODO
        // 启用发送者确认模式
        channel.confirmSelect();

        //所有日志严重性级别
        for (int i = 0; i < 2; i++) {
            // 发送的消息
            String message = "Hello World_" + (i + 1);
            //参数1：exchange name
            //参数2：routing key
            channel.basicPublish(EXCHANGE_NAME, ROUTE_KEY, true, null, message.getBytes());
            System.out.println(" Sent Message: [" + ROUTE_KEY + "]:'" + message + "'");
            //TODO
            // 确认是否成功(true成功)，单条message的确认
            if (channel.waitForConfirms()) {
                System.out.println("send success");
            } else {
                System.out.println("send failure");
            }
        }
        // 关闭信道和连接
        channel.close();
        connection.close();
    }

}
