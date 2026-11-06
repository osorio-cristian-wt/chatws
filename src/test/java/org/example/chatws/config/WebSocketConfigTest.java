package org.example.chatws.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.simp.broker.SimpleBrokerMessageHandler;
import org.springframework.messaging.simp.stomp.StompBrokerRelayMessageHandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// no necesita el rabbit levantado, solo mira cómo quedó armado el broker
@SpringBootTest(properties = {
        "spring.rabbitmq.host=rabbit-de-prueba",
        "chat.rabbitmq.stomp-port=61999"
})
class WebSocketConfigTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void usaElRelayDeRabbitEnVezDelBrokerEnMemoria() {
        // si alguien vuelve a poner enableSimpleBroker esto salta
        assertTrue(context.getBeansOfType(SimpleBrokerMessageHandler.class).isEmpty());
        assertEquals(1, context.getBeansOfType(StompBrokerRelayMessageHandler.class).size());
    }

    @Test
    void elRelayTomaHostYPuertoDeLaConfig() {
        StompBrokerRelayMessageHandler relay = context.getBean(StompBrokerRelayMessageHandler.class);

        assertEquals("rabbit-de-prueba", relay.getRelayHost());
        assertEquals(61999, relay.getRelayPort());
        assertTrue(relay.getDestinationPrefixes().contains("/topic"));
        assertTrue(relay.getDestinationPrefixes().contains("/queue"));
    }
}
