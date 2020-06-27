package ru.axyv.sslwebsocket.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlfaAtm {
    private Long deviceId;
    private Coordinates coordinates;
    private Address address;
    private String location;
    private Boolean payments;
    private Availability availableNow;
}
