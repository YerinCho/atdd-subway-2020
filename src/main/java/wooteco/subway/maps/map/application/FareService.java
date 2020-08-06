package wooteco.subway.maps.map.application;

import org.springframework.stereotype.Service;

@Service
public class FareService {
    private static final int EXTRA_FARE = 100;
    private static final int DEFAULT_FARE = 1250;
    private static final int DEFAULT_EXTRA_FARE = DEFAULT_FARE + EXTRA_FARE * 8;
    private static final int DEFAULT_DISTANCE_CRITERIA = 10;
    private static final int DISTANCE_CRITERIA = 50;

    public int calculateFare(int distance) {
        if (distance > DISTANCE_CRITERIA) {
            return (int)((Math.ceil((distance - DISTANCE_CRITERIA - 1) / 8) + 1) * EXTRA_FARE) + DEFAULT_EXTRA_FARE;
        }
        if (distance > DEFAULT_DISTANCE_CRITERIA) {
            return (int)((Math.ceil((distance - DEFAULT_DISTANCE_CRITERIA - 1) / 5) + 1) * EXTRA_FARE) + DEFAULT_FARE;
        }
        return DEFAULT_FARE;
    }
}
