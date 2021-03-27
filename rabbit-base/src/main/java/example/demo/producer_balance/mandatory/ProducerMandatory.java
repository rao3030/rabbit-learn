package example.demo.producer_balance.mandatory;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 类说明：生产者——失败确认模式
 */
public class ProducerMandatory {

    public final static String EXCHANGE_NAME = "mandatory_test";

    public static void main(String[] args)
            throws IOException, TimeoutException, InterruptedException {
        /**
         * 创建连接连接到RabbitMQ
         */
        ConnectionFactory factory = new ConnectionFactory();

        // 设置MabbitMQ所在主机ip或者主机名 rabbitMQ 是在本机，用户、密码
        factory.setHost("127.0.0.1");
        // 创建一个连接
        Connection connection = factory.newConnection();
        // 创建一个信道
        Channel channel = connection.createChannel();
        // 指定Direct交换器
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        //TODO  回调
        //连接关闭时执行
        connection.addShutdownListener(new ShutdownListener() {
            public void shutdownCompleted(ShutdownSignalException e) {

            }
        });

        //TODO 回调
        //信道关闭时执行
        channel.addShutdownListener(new ShutdownListener() {
            public void shutdownCompleted(ShutdownSignalException e) {

            }
        });

        //TODO
        //todo 失败通知 回调 这个message没有发送到任何一个queue中，所谓的消息丢失的一种
        channel.addReturnListener(new ReturnListener() {
            public void handleReturn(int replycode, String replyText, String exchange, String routeKey, AMQP.BasicProperties basicProperties, byte[] bytes) throws IOException {
                String message = new String(bytes);
                //replycode-replyText 类似于response的状态码
                System.out.println("返回的replycode:" + replycode);//404
                System.out.println("返回的replyText:" + replyText);//not found
                System.out.println("返回的exchange:" + exchange);//exchange的name
                System.out.println("返回的routeKey:" + routeKey);//routeKey
                // channel.basicPublish(EXCHANGE_NAME, routekey, true, null, message.getBytes()); //下面的一行代码就是message
                System.out.println(new String(bytes, "UTF-8"));
                //todo 可以在这里做失败重试，再次执行channel.basicPublish(EXCHANGE_NAME, routekey, true, null, message.getBytes()); //下面的一行代码就是message
            }
        });


        String[] routekeys = {"king", "mark", "james"};
        for (int i = 0; i < 3; i++) {
            String routekey = routekeys[i % 3];
            // 发送的消息
            String message = "Hello World_" + (i + 1)
                    + ("_" + System.currentTimeMillis());
            //TODO
            channel.basicPublish(EXCHANGE_NAME, routekey, true, null, message.getBytes());
            System.out.println("----------------------------------");
            System.out.println(" Sent Message: [" + routekey + "]:'"
                    + message + "'");
            Thread.sleep(200);
        }

        // 关闭频道和连接
        channel.close();
        connection.close();
    }


}
