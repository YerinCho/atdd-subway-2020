package wooteco.subway.maps.map.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.common.collect.Lists;
import wooteco.subway.common.TestObjectUtils;
import wooteco.subway.maps.line.application.LineService;
import wooteco.subway.maps.line.domain.Line;
import wooteco.subway.maps.line.domain.LineStation;
import wooteco.subway.maps.map.domain.PathType;
import wooteco.subway.maps.map.domain.SubwayPath;
import wooteco.subway.maps.station.domain.Station;

@ExtendWith(MockitoExtension.class)
class FareServiceTest {
    private Line line1;
    private Line line2;
    private Line line3;
    private List<Line> lines;
    @Mock
    private PathService pathService;
    @Mock
    private LineService lineService;
    private FareService fareService;

    @BeforeEach
    void setUp() {
        Map<Long, Station> stations = new HashMap<>();
        stations.put(1L, TestObjectUtils.createStation(1L, "교대역"));
        stations.put(2L, TestObjectUtils.createStation(2L, "강남역"));
        stations.put(3L, TestObjectUtils.createStation(3L, "양재역"));
        stations.put(4L, TestObjectUtils.createStation(4L, "남부터미널역"));
        stations.put(5L, TestObjectUtils.createStation(5L, "잠실역"));

        line1 = TestObjectUtils.createLine(1L, "2호선", "GREEN", 100);
        line1.addLineStation(new LineStation(1L, null, 0, 0));
        line1.addLineStation(new LineStation(2L, 1L, 2, 2));

        line2 = TestObjectUtils.createLine(2L, "신분당선", "RED", 200);
        line2.addLineStation(new LineStation(2L, null, 0, 0));
        line2.addLineStation(new LineStation(3L, 2L, 2, 1));

        line3 = TestObjectUtils.createLine(3L, "3호선", "ORANGE", 0);
        line3.addLineStation(new LineStation(1L, null, 0, 0));
        line3.addLineStation(new LineStation(5L, 1L, 1, 2));
        line3.addLineStation(new LineStation(3L, 4L, 28, 2));
        line3.addLineStation(new LineStation(4L, 5L, 66, 2));

        lines = Lists.newArrayList(line1, line2, line3);
        pathService = new PathService();
        fareService = new FareService(lineService);
    }

    @Test
    @DisplayName("10km 미만 기본요금")
    void calculateFareUnder10() {
        SubwayPath subwayPath = pathService.findPath(lines, 1L, 2L, PathType.DISTANCE);
        assertThat(fareService.calculateFare(subwayPath)).isEqualTo(1250);
    }

    @Test
    @DisplayName("10~50 요금 계산")
    void calculateFare10To50() {
        SubwayPath subwayPath = pathService.findPath(lines, 3L, 4L, PathType.DURATION);
        assertThat(fareService.calculateFare(subwayPath)).isEqualTo(1650);
    }

    @Test
    void calculateFareOver50() {
        SubwayPath subwayPath = pathService.findPath(lines, 4L, 5L, PathType.DURATION);
        assertThat(fareService.calculateFare(subwayPath)).isEqualTo(2250);
    }

    @Test
    @DisplayName("노선별 추가요금이 있는 노선을 탔을 때 추가요금 적용")
    void calculateLineExtraFare() {
        when(lineService.findLineById(1L)).thenReturn(line1);
        when(lineService.findLineById(2L)).thenReturn(line2);

        SubwayPath subwayPath = pathService.findPath(lines, 1L, 3L, PathType.DURATION);

        //기본요금 + 200원 추가요금
        assertThat(fareService.calculateFare(subwayPath)).isEqualTo(1450);
    }

}
