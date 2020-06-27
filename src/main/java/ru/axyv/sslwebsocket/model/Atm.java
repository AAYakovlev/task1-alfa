package ru.axyv.sslwebsocket.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Atm {
    private Long deviceId;
    private String latitude;
    private String longitude;
    private String city;
    private String location;
    private Boolean payments;
}
