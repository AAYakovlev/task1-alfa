package ru.axyv.sslwebsocket.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.axyv.sslwebsocket.configuration.StompHandler;
import ru.axyv.sslwebsocket.model.Atm;
import ru.axyv.sslwebsocket.model.StatusResponse;
import ru.axyv.sslwebsocket.service.AtmssService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AtmController {
    private final AtmssService atmssService;
    private final StompHandler stompHandler;

    @GetMapping("/atms/{deviceId}")
    public ResponseEntity<?> getAtm(@PathVariable Long deviceId) {
        Atm atm = atmssService.getAtmList().stream()
                .filter(atm1 -> atm1.getDeviceId().equals(deviceId))
                .findAny()
                .orElse(null);
        if (atm != null) {
            return ResponseEntity.ok(atm);
        } else {
            return new ResponseEntity<>(new StatusResponse("atm not found"), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/atms/nearest")
    public ResponseEntity<?> getAtmNear(@RequestParam String latitude, @RequestParam String longitude, @RequestParam(required = false) Boolean payments) {
        double lat = Double.valueOf(latitude);
        double longt = Double.valueOf(longitude);
        log.info("Nearest request {} {} {}", latitude, longitude, payments);
        List<Atm> atm = atmssService.getAtmList();
        if (payments != null) {
            atm = atm.stream().filter(Atm::getPayments).collect(Collectors.toList());
        }
        Atm closest = getAtm(lat, longt, atm);

        log.info("Nearest is {}", closest);
        return ResponseEntity.ok(closest);
    }

    @GetMapping("/atms/nearest-with-alfik")
    @SneakyThrows
    public ResponseEntity<?> getAtmNearAlf(@RequestParam String latitude, @RequestParam String longitude, @RequestParam(required = false) Boolean payments,
                                           @RequestParam Integer alfik) {
        double lat = Double.valueOf(latitude);
        double longt = Double.valueOf(longitude);
        List<Atm> res = new ArrayList<>();
        log.info("Nearest ALFIK request {} {} {} {}", latitude, longitude, payments, alfik);
        List<Atm> atm = atmssService.getAtmList();
        if (payments != null) {
            atm = atm.stream().filter(Atm::getPayments).collect(Collectors.toList());
        }
        boolean needMore = false;
        do {
            Atm closest = getAtm(lat, longt, atm);
            res.add(closest);
            stompHandler.send(closest.getDeviceId().intValue());
            atm = atm.stream().filter(a -> !a.getDeviceId().equals(closest.getDeviceId())).collect(Collectors.toList());

            stompHandler.getCyclicBarrier().await(5, TimeUnit.SECONDS);

            if (alfik > stompHandler.getLastAlfikCount()) {
                needMore = true;
                alfik = alfik - (int) stompHandler.getLastAlfikCount();
                log.info("Alfik remain {}", alfik);
            } else {
                needMore = false;
            }
        } while (needMore);

        log.info("Nearest  ALFIK is {}", res);
        return ResponseEntity.ok(res);
    }

    private Atm getAtm(double lat, double longt, List<Atm> atm) {
        Atm closest = atm.get(0);
        double closesDist = Double.MAX_VALUE;
        for (Atm a : atm) {
            try {
                double latC = Double.valueOf(a.getLatitude());
                double longtC = Double.valueOf(a.getLongitude());
                double distance = distance(lat, longt, latC, longtC);
                if (closesDist > distance) {
                    closesDist = distance;
                    closest = a;
                }
            } catch (NullPointerException e) {
//                log.warn("Invalid coordinates {}", a);
            }
        }
        return closest;
    }

//    @GetMapping("/atms")
//    public ResponseEntity<?> getAtm() {
//        List<Atm> atm = atmssService.getAtmList();
//        return ResponseEntity.ok(atm);
//    }

//    @GetMapping("/status")
//    public ResponseEntity<?> getAtmS() {
//        List<Atm> atm = atmssService.getAtmStatus();
//        return ResponseEntity.ok(atm);
//    }

    public static double distance(double lat1, double lng1,
                                  double lat2, double lng2) {
        double a = (lat1 - lat2) * distPerLat(lat1);
        double b = (lng1 - lng2) * distPerLng(lat1);
        return Math.sqrt(a * a + b * b);
    }

    private static double distPerLng(double lat) {
        return 0.0003121092 * Math.pow(lat, 4)
                + 0.0101182384 * Math.pow(lat, 3)
                - 17.2385140059 * lat * lat
                + 5.5485277537 * lat + 111301.967182595;
    }

    private static double distPerLat(double lat) {
        return -0.000000487305676 * Math.pow(lat, 4)
                - 0.0033668574 * Math.pow(lat, 3)
                + 0.4601181791 * lat * lat
                - 1.4558127346 * lat + 110579.25662316;
    }
}
