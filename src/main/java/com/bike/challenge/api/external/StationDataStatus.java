package com.bike.challenge.api.external;

import lombok.Data;
import java.util.List;

@Data
public class StationDataStatus {
    List<StationStatus> stations;
}
