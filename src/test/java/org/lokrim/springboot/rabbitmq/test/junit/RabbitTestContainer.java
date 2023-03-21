package org.lokrim.springboot.rabbitmq.test.junit;

import org.testcontainers.containers.RabbitMQContainer;
import static org.testcontainers.utility.DockerImageName.*;

public class RabbitTestContainer extends RabbitMQContainer {

    public static final String IMAGE_NAME = "rabbitmq";
    public static final String IMAGE_TAG = "3.8.5";

    public RabbitTestContainer() {
        super(parse(IMAGE_NAME).withTag(IMAGE_TAG));
    }

}
