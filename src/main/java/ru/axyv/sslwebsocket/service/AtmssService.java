package ru.axyv.sslwebsocket.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.axyv.sslwebsocket.model.AAtmsResponse;
import ru.axyv.sslwebsocket.model.AlfaAtm;
import ru.axyv.sslwebsocket.model.Atm;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AtmssService {
    private final RestTemplate restTemplate;

    private List<AlfaAtm> statuses = new ArrayList<>();

    public List<Atm> getAtmList(){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-IBM-Client-Id","41ed65f4-63b2-4961-bf63-679923ef3fc0");

        HttpEntity entity = new HttpEntity(httpHeaders);

        ResponseEntity<AAtmsResponse> forEntity = restTemplate.exchange("https://apiws.alfabank.ru/alfabank/alfadevportal/atm-service/atms", HttpMethod.GET, entity, AAtmsResponse.class);

        return forEntity.getBody().getData().getAtms().stream().map(alfaAtm -> {
            Atm atm = new Atm();
            atm.setDeviceId(alfaAtm.getDeviceId());
            atm.setCity(alfaAtm.getAddress().getCity());
            atm.setLocation(alfaAtm.getAddress().getLocation());
            atm.setLatitude(alfaAtm.getCoordinates().getLatitude());
            atm.setLongitude(alfaAtm.getCoordinates().getLongitude());
            atm.setPayments(
                    statuses.stream().filter(alfaAtm1 -> alfaAtm1.getDeviceId().equals(alfaAtm.getDeviceId())).findAny()
                            .map(a->a.getAvailableNow().getPayments().equals("Y")).orElse(false));
            return atm;
        }).collect(Collectors.toList());
    }

    @EventListener(ApplicationReadyEvent.class)
    public void getAtmStatus(){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-IBM-Client-Id","41ed65f4-63b2-4961-bf63-679923ef3fc0");

        HttpEntity entity = new HttpEntity(httpHeaders);

        ResponseEntity<AAtmsResponse> forEntity = restTemplate.exchange("https://apiws.alfabank.ru/alfabank/alfadevportal/atm-service/atms/status", HttpMethod.GET, entity, AAtmsResponse.class);

        statuses =  forEntity.getBody().getData().getAtms();
    }
}
