package example.demo.pushpull;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;


public class RabbitUtils {
    private static ConnectionFactory connectionFactory = new ConnectionFactory();

    static {
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/");
    }

    public static Connection getConnection() throws Exception {
        Connection conn = null;

        try {
            conn = connectionFactory.newConnection();
        } catch (Exception e) {
            throw new Exception(e);
        }
        return conn;

    }
}
