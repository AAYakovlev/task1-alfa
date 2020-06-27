package ru.axyv.sslwebsocket.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Slf4j
@Service
@RequiredArgsConstructor
public class SslService {
    private final RestTemplate restTemplate;

//    @EventListener(ApplicationReadyEvent.class)
    public void dowork() {
        ResponseEntity<String> entity = restTemplate.getForEntity("https://localhost", String.class);

        log.info(entity.toString());
    }



}
