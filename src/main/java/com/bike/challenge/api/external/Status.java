package com.bike.challenge.api.external;

import lombok.Data;

@Data
public class Status {
    String lastUpdated;
    StationDataStatus data;
}
