package wooteco.subway.maps.map.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import wooteco.subway.maps.line.application.LineService;
import wooteco.subway.maps.map.domain.LineStationEdge;
import wooteco.subway.maps.map.domain.SubwayPath;

@Service
public class FareService {
    private static final int EXTRA_FARE = 100;
    private static final int DEFAULT_FARE = 1250;
    private static final int DEFAULT_EXTRA_FARE = DEFAULT_FARE + EXTRA_FARE * 8;
    private static final int DEFAULT_DISTANCE_CRITERIA = 10;
    private static final int DISTANCE_CRITERIA = 50;

    private final LineService lineService;

    public FareService(LineService lineService) {
        this.lineService = lineService;
    }

    public int calculateFare(SubwayPath subwayPath) {
        int distance = subwayPath.calculateDistance();
        int defaultFare = calculateFareByDistance(distance);
        int lineExtraFare = calculateLineExtraFare(subwayPath.getLineStationEdges());
        return defaultFare + lineExtraFare;
    }

    private int calculateFareByDistance(int distance) {
        if (distance > DISTANCE_CRITERIA) {
            return (int)((Math.ceil((distance - DISTANCE_CRITERIA - 1) / 8) + 1) * EXTRA_FARE) + DEFAULT_EXTRA_FARE;
        }
        if (distance > DEFAULT_DISTANCE_CRITERIA) {
            return (int)((Math.ceil((distance - DEFAULT_DISTANCE_CRITERIA - 1) / 5) + 1) * EXTRA_FARE) + DEFAULT_FARE;
        }
        return DEFAULT_FARE;
    }

    private int calculateLineExtraFare(List<LineStationEdge> lineStationEdges) {
        List<Integer> extraFares = lineStationEdges.stream()
            .map(lineStationEdge -> lineService.findLineById(lineStationEdge.getLineId()).getExtraFare())
            .collect(Collectors.toList());
        return extraFares.stream().max(Integer::compareTo).get();
    }
}
