package org.example.chatws.config;

import org.example.chatws.utility.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuración de WebSocket para habilitar mensajería en tiempo real.
 * Esta clase configura el broker de mensajes y los endpoints de conexión WebSocket.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // Prefijo para los destinos de tipo topic (suscripciones)
    public static final String TOPIC = "/topic";
    // Prefijo para los destinos de la aplicación (mensajes enviados por clientes)
    public static final String APP = "/app";

    // mismos datos que usa spring.rabbitmq, así se configura todo en un solo lugar
    @Value("${spring.rabbitmq.host}")
    private String relayHost;
    @Value("${chat.rabbitmq.stomp-port}")
    private int relayPort;
    @Value("${spring.rabbitmq.username}")
    private String relayUser;
    @Value("${spring.rabbitmq.password}")
    private String relayPass;

    /**
     * Configura el broker de mensajes.
     * - enableStompBrokerRelay: Reenvía los mensajes de /topic (broadcast) y /queue (privado)
     *   a un RabbitMQ externo por STOMP, en lugar de usar el broker en memoria.
     * - setApplicationDestinationPrefixes: Define el prefijo para destinos de mensajes
     *   enviados desde el cliente al servidor (ej: /app/chat.sendMessage).
     *
     * @param config Registro de configuración del broker de mensajes
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // antes era enableSimpleBroker (en memoria), ahora todo pasa por el rabbit
        // el puerto es el del plugin stomp (61613), no el 5672 de amqp
        config.enableStompBrokerRelay(TOPIC, "/queue")
                .setRelayHost(relayHost)
                .setRelayPort(relayPort)
                // system es la conexión compartida del server, client la de cada usuario conectado
                .setSystemLogin(relayUser)
                .setSystemPasscode(relayPass)
                .setClientLogin(relayUser)
                .setClientPasscode(relayPass);
        config.setApplicationDestinationPrefixes(APP);
    }

    /**
     * Registra los endpoints STOMP para la conexión WebSocket.
     * - addEndpoint: Define la URL donde los clientes se conectarán al WebSocket.
     * - withSockJS: Habilita SockJS como fallback para navegadores que no soportan WebSocket nativo.
     *   Esto permite conexiones via HTTP long-polling si WebSocket no está disponible.
     * 
     * @param registry Registro de endpoints STOMP
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(Constants.ENDPOINT).withSockJS();
    }
}
