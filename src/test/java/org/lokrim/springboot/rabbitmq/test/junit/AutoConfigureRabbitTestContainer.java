package org.lokrim.springboot.rabbitmq.test.junit;

import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Testcontainers(disabledWithoutDocker = true)
@ExtendWith(RabbitTestContainerExtension.class)
public @interface AutoConfigureRabbitTestContainer {

}
