package wooteco.subway.maps.map.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import wooteco.subway.maps.line.application.LineService;
import wooteco.subway.maps.map.domain.LineStationEdge;
import wooteco.subway.maps.map.domain.SubwayPath;
import wooteco.subway.members.member.domain.LoginMember;

@Service
public class FareService {
    private static final int EXTRA_FARE = 100;
    private static final int DEFAULT_FARE = 1250;
    private static final int DEFAULT_EXTRA_FARE = DEFAULT_FARE + EXTRA_FARE * 8;
    private static final int DEFAULT_DISTANCE_CRITERIA = 10;
    private static final int DISTANCE_CRITERIA = 50;
    private static final int DEDUCTION_FARE = 350;
    private static final int ADULT_CRITERIA = 19;
    private static final int YOUTH_CRITERIA = 13;
    private static final int CHILD_CRITERIA = 6;

    private final LineService lineService;

    public FareService(LineService lineService) {
        this.lineService = lineService;
    }

    public int calculateFare(LoginMember loginMember, SubwayPath subwayPath) {
        int distance = subwayPath.calculateDistance();
        int defaultFare = calculateFareByDistance(distance);
        int lineExtraFare = calculateLineExtraFare(subwayPath.getLineStationEdges());
        int totalFare = defaultFare + lineExtraFare;

        if (loginMember == null) {
            return totalFare;
        }
        return calculateAgeFare(loginMember, totalFare);
    }

    private int calculateAgeFare(LoginMember loginMember, int totalFare) {
        int age = loginMember.getAge();
        if (age >= ADULT_CRITERIA || age < CHILD_CRITERIA) {
            return totalFare;
        }
        if (age >= YOUTH_CRITERIA) {
            return (int)((totalFare - DEDUCTION_FARE) * 0.8);
        }
        return (int)((totalFare - DEDUCTION_FARE) * 0.5);
    }

    int calculateFareByDistance(int distance) {
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
        return extraFares.stream()
            .max(Integer::compareTo)
            .orElseThrow(() -> new IllegalAccessError("노선별 요금을 계산할 수 없습니다."));
    }
}
