package ru.axyv.sslwebsocket.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import ru.axyv.sslwebsocket.model.Greeting;
import ru.axyv.sslwebsocket.model.HelloMessage;

import java.lang.reflect.Type;

@Slf4j
@Configuration
public class StompHandler extends StompSessionHandlerAdapter {
    private StompSession session;
    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        this.session = session;
        log.info("New session established : " + session.getSessionId());
        session.subscribe("/topic/greetings", this);
        log.info("Subscribed to /topic/greetings");
        StompSession.Receiptable send = session.send("/app/hello", new HelloMessage("123"));
        log.info("Message sent to websocket server");
    }

    public void send(){
        session.send("/app/hello", new HelloMessage("123"));
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        log.error("Exception in websocket session ", exception);
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return Greeting.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        Greeting msg = (Greeting) payload;
        log.info("Received : " + msg.getContent());
    }

}
