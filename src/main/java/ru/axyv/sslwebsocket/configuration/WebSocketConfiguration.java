package ru.axyv.sslwebsocket.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@Slf4j
@Configuration
public class WebSocketConfiguration {
    private static String URL = "ws://130.193.51.107:8100/";

    @Bean
    public WebSocketStompClient webSocket(StompHandler sessionHandler) {
        WebSocketClient client = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(client);

        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        ListenableFuture<StompSession> sessionListenableFuture = stompClient.connect(URL, sessionHandler);
        // TODO: 25.06.2020 reconnect on failure
        sessionListenableFuture.addCallback(
                (StompSession s) -> log.info("Connected"),
                (Throwable ex) -> log.error("Connect failed", ex));
        return stompClient;
    }
}
