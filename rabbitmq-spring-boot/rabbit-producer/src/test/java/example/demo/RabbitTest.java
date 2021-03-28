package example.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RabbitTest {

    @Autowired
    RabbitmqSender sender;

    @Test
    public void rabbitTest() {
        for (int i = 0; i < 100; i++) {
            if (i == 50) {
                sender.sendMessage("demo.exchange", "demo.wahaha", "Demo" + i);
                continue;
            }
            sender.sendMessage("demo.exchange", "demo.routeKey", "Demo" + i);
        }
    }
}
