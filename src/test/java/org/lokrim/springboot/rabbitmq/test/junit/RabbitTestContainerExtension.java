package org.lokrim.springboot.rabbitmq.test.junit;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

class RabbitTestContainerExtension implements BeforeAllCallback {

    private static final String PROPERTY_RABBIT_HOST = "spring.rabbitmq.host";
    private static final String PROPERTY_RABBIT_PORT = "spring.rabbitmq.port";

    private static RabbitTestContainer container;


    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        // Start the container only once for all the tests using it
        if (container == null) {
            container = new RabbitTestContainer();
            container.start();
        }

        String host = container.getContainerIpAddress();
        Integer port = container.getMappedPort(5672);
        System.setProperty(PROPERTY_RABBIT_HOST, host);
        System.setProperty(PROPERTY_RABBIT_PORT, port.toString());
    }


}