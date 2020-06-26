package ru.axyv.sslwebsocket.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.axyv.sslwebsocket.configuration.StompHandler;

@RestController
@RequiredArgsConstructor
public class Controllers {
    private final StompHandler stompHandler;

    @GetMapping("/")
    public ResponseEntity<?> getMap(){
        stompHandler.send();
        return ResponseEntity.ok().build();
    }

}
