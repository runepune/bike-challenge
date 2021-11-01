package com.bike.challenge.api.external;

import lombok.Data;

@Data
public class StationStatus {
    Long stationId;

    Boolean isInstalled;
    Boolean isRenting;
    Integer numBikesAvailable;
    Integer numDocksAvailable;
    String lastReported;
    Boolean isReturning;

}
