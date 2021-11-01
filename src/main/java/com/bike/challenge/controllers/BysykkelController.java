package com.bike.challenge.controllers;

import com.bike.challenge.api.external.Information;
import com.bike.challenge.api.external.StationInfo;
import com.bike.challenge.api.external.StationStatus;
import com.bike.challenge.api.external.Status;
import com.bike.challenge.api.internal.BikeData;
import com.bike.challenge.config.ErrorHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpMethod.GET;

@Slf4j
@RestController
public class BysykkelController {
    @Value("${bike.challenge.base-url}")
    private String BASE_URL;
    @Value("${bike.challenge.station-info}")
    private String STATION_INFO;
    @Value("${bike.challenge.station-status}")
    private String STATION_STATUS;
    @Value("${bike.challenge.company-info}")
    private String COMPANY_INFO;


    private RestTemplate restTemplate;
    private HttpEntity entity;

    @Autowired
    public BysykkelController(RestTemplateBuilder builder){
        this.restTemplate = builder
                .errorHandler(new ErrorHandler())
                .build();

        //The API vendor wants to receive info about who's fetching the data
        HttpHeaders headers = new HttpHeaders();
        headers.set("Client-Identifier", COMPANY_INFO);
        entity = new HttpEntity(headers);
    }

    @RequestMapping("/challenge")
    public ResponseEntity<List<BikeData>> challenge(){
        //1. Fetch relevant data
        Information info = getStationInformation().getBody();
        Status status = getStationStatus().getBody();

        //Due to funky API structure these objects needs merging, data struct can be merged on id field
        List<BikeData> bikeDatas = mergeData(info, status);

        return ResponseEntity.ok(bikeDatas);
    }

    private ResponseEntity<Information> getStationInformation() {
        String url = BASE_URL + STATION_INFO;
        //Fetch StationInfo & ErrorHandler will handle errors
        ResponseEntity<Information> response = restTemplate.exchange(url, GET, entity, Information.class);

        return response;
    }

    private ResponseEntity<Status> getStationStatus() {
        String url = BASE_URL + STATION_STATUS;
        //Fetch StationStatus & ErrorHandler will handle errors
        ResponseEntity<Status> response = restTemplate.exchange(url, GET, entity, Status.class);

        return response;
    }

    private List<BikeData> mergeData(Information info, Status status) {
        //Uses map for merging on id field
        Map<Long, StationInfo> infoMap;
        if (info != null && info.getData() != null && info.getData().getStations() != null) {
            infoMap = info.getData().getStations().stream().collect(Collectors.toMap(StationInfo::getStationId, stationInfo -> {
                return stationInfo;
            }));
        } else {
            //Here we could throw an application exception to notify the user of missing data...
            //Just creates an empty one for now
            infoMap = new HashMap<>();
        }

        Map<Long, StationStatus> statusMap;
        if (status != null && status.getData() != null && status.getData().getStations() != null) {
            statusMap = status.getData().getStations().stream().collect(Collectors.toMap(StationStatus::getStationId, stationStatus -> {
                return stationStatus;
            }));
        } else {
            //Here we could throw an application exception to notify the user of missing data...
            //Just creates an empty one for now
            statusMap = new HashMap<>();
        }

        //Merge the two data structures to our internal API
        List<BikeData> bikeDatas = new ArrayList<>();
        infoMap.forEach((id, stationInfo) -> {

            BikeData bikeData = new BikeData();
            BeanUtils.copyProperties(stationInfo, bikeData);

            if (statusMap.containsKey(id)) {
                BeanUtils.copyProperties(statusMap.get(id), bikeData, "stationId");
            }
            bikeDatas.add(bikeData);
        });

        Collections.sort(bikeDatas);

        return bikeDatas;
    }

}
