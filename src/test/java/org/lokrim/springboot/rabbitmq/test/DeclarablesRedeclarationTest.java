package org.lokrim.springboot.rabbitmq.test;

import org.junit.jupiter.api.Test;
import org.lokrim.springboot.rabbitmq.test.DeclarablesRedeclarationTest.Configuration;
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
class DeclarablesRedeclarationTest {

    static final String EXCHANGE = "lokrim-declarables-exchange";
    static final String QUEUE = "lokrim-declarables-queue";
    static final long TIMEOUT = 30_000; // 30 seconds


    static class Configuration {

        @Bean
        Declarables declarables() {
            FanoutExchange exchange = ExchangeBuilder.fanoutExchange(EXCHANGE).build();
            Queue queue = QueueBuilder.durable(QUEUE).build();
            Binding binding = BindingBuilder.bind(queue).to(exchange);
            return new Declarables(exchange, queue, binding);
        }

        @RabbitListener(queues = DeclarablesRedeclarationTest.QUEUE, autoStartup = "true")
        void consumeMessage(Message m) {
        }
    }

    @Autowired
    AmqpAdmin rabbit;

    @Test
    void test_whenQueueIsDeclaredUsingDeclarables_andIsDeleted_itIsAutomaticallyRedeclared() {
        // Assert that initially the queue exists
        assertTrue(queueExists(QUEUE));

        // Delete queue
        rabbit.deleteQueue(QUEUE);

        // Assert that queue is not redeclared within a reasonable timeout
        await().atMost(TIMEOUT, TimeUnit.MILLISECONDS).until(() -> queueExists(QUEUE));
    }

    boolean queueExists(String queueName) {
        QueueInformation queueInfo = rabbit.getQueueInfo(queueName);
        return queueInfo != null;
    }

}
