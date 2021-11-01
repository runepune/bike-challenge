package com.bike.challenge.api.internal;

import lombok.Data;

@Data
public class BikeData implements Comparable<BikeData>{
    Long stationId;
    String name;
    String address;
    Integer capacity;
    Boolean isInstalled;
    Boolean isRenting;
    Integer numBikesAvailable;
    Integer numDocksAvailable;
    Boolean isReturning;

    @Override
    public int compareTo(BikeData bikeData) {
        return this.getName().compareTo(bikeData.getName());
    }
}
