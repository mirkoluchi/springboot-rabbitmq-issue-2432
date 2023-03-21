package org.lokrim.springboot.rabbitmq.test;

import org.junit.jupiter.api.Test;
import org.lokrim.springboot.rabbitmq.test.QueueRedeclarationTest.Configuration;
import org.lokrim.springboot.rabbitmq.test.junit.AutoConfigureRabbitTestContainer;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = Configuration.class)
@EnableAutoConfiguration
@AutoConfigureRabbitTestContainer
class QueueRedeclarationTest {

    static final String EXCHANGE = "lokrim-exchange";
    static final String QUEUE = "lokrim-queue";
    static final long TIMEOUT = 30_000; // 30 seconds

    static class Configuration {

        @Bean
        FanoutExchange exchange() {
            return ExchangeBuilder.fanoutExchange(EXCHANGE).build();
        }

        @Bean
        Queue queue() {
            return QueueBuilder.durable(QUEUE).build();
        }

        @Bean
        Binding binding(FanoutExchange exchange, Queue queue) {
            return BindingBuilder.bind(queue).to(exchange);
        }

        @RabbitListener(queues = QueueRedeclarationTest.QUEUE, autoStartup = "true")
        void consumeMessage(Message m) {
        }
    }

    @Autowired
    AmqpAdmin rabbit;


    @Test
    void test_whenQueueIsDeclaredUsingBean_andIsDeleted_itIsAutomaticallyRedeclared() {
        // Assert that initially the queue exists
        assertTrue(queueExists(QUEUE));

        // Delete queue
        rabbit.deleteQueue(QUEUE);

        // Assert that queue is redeclared within a reasonable timeout
        await().atMost(TIMEOUT, TimeUnit.MILLISECONDS).until(() -> queueExists(QUEUE));
    }

    boolean queueExists(String queueName) {
        QueueInformation queueInfo = rabbit.getQueueInfo(queueName);
        return queueInfo != null;
    }

}
