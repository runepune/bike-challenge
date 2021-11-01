package com.bike.challenge.api.external;

import lombok.Data;

@Data
public class StationInfo {
    Long stationId;

    String name;
    String address;
    RentalUris rentalUris;
    Double lat;
    Double lon;
    Integer capacity;
}
