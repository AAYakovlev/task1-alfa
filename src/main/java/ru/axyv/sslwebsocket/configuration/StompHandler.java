package ru.axyv.sslwebsocket.configuration;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import ru.axyv.sslwebsocket.model.AlficResp;
import ru.axyv.sslwebsocket.model.AlfikReq;

import java.lang.reflect.Type;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

@Slf4j
@Getter
@Configuration
public class StompHandler extends StompSessionHandlerAdapter {
    private StompSession session;

    private final CyclicBarrier cyclicBarrier = new CyclicBarrier(2);

    private volatile long lastAlfikCount;

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        this.session = session;
        log.info("New session established : " + session.getSessionId());
        session.subscribe("/topic/alfik", this);
        log.info("Subscribed to /topic/alfik");
    }

    public void send(Integer id){
        session.send("/", new AlfikReq(id));
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        log.error("Exception in websocket session ", exception);
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return AlficResp.class;
    }

    @Override
    @SneakyThrows
    public void handleFrame(StompHeaders headers, Object payload) {
        AlficResp msg = (AlficResp) payload;
        log.info("Received : " + msg);
        lastAlfikCount = msg.getAlfik();
        cyclicBarrier.await(5, TimeUnit.SECONDS);
    }

}
