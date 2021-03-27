package example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RabbitProducerApp {
    public static void main(String[] args) {
        SpringApplication.run(RabbitProducerApp.class, args);
    }
}
