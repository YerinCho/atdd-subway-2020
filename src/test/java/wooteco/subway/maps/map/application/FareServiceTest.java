package wooteco.subway.maps.map.application;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;
import wooteco.subway.common.TestObjectUtils;
import wooteco.subway.maps.line.domain.Line;
import wooteco.subway.maps.line.domain.LineStation;
import wooteco.subway.maps.map.domain.PathType;
import wooteco.subway.maps.map.domain.SubwayPath;
import wooteco.subway.maps.station.domain.Station;

class FareServiceTest {
    private List<Line> lines;
    private PathService pathService;
    private FareService fareService;

    @BeforeEach
    void setUp() {
        Map<Long, Station> stations = new HashMap<>();
        stations.put(1L, TestObjectUtils.createStation(1L, "교대역"));
        stations.put(2L, TestObjectUtils.createStation(2L, "강남역"));
        stations.put(3L, TestObjectUtils.createStation(3L, "양재역"));
        stations.put(4L, TestObjectUtils.createStation(4L, "남부터미널역"));
        stations.put(5L, TestObjectUtils.createStation(5L, "잠실역"));

        Line line1 = TestObjectUtils.createLine(1L, "2호선", "GREEN");
        line1.addLineStation(new LineStation(1L, null, 0, 0));
        line1.addLineStation(new LineStation(2L, 1L, 2, 2));

        Line line2 = TestObjectUtils.createLine(2L, "신분당선", "RED");
        line2.addLineStation(new LineStation(2L, null, 0, 0));
        line2.addLineStation(new LineStation(3L, 2L, 2, 1));

        Line line3 = TestObjectUtils.createLine(3L, "3호선", "ORANGE");
        line3.addLineStation(new LineStation(1L, null, 0, 0));
        line3.addLineStation(new LineStation(5L, 1L, 1, 2));
        line3.addLineStation(new LineStation(3L, 4L, 28, 2));
        line3.addLineStation(new LineStation(4L, 5L, 66, 2));

        lines = Lists.newArrayList(line1, line2, line3);
        pathService = new PathService();
        fareService = new FareService();
    }

    @Test
    @DisplayName("10km 미만 기본요금")
    void calculateFareUnder10() {
        SubwayPath subwayPath = pathService.findPath(lines, 1L, 2L, PathType.DISTANCE);
        assertThat(fareService.calculateFare(subwayPath.calculateDistance())).isEqualTo(1250);
    }

    @Test
    @DisplayName("10~50 요금 계산")
    void calculateFare10To50() {
        SubwayPath subwayPath = pathService.findPath(lines, 3L, 4L, PathType.DURATION);
        assertThat(subwayPath.calculateDistance()).isEqualTo(28);
        //1250 + 100 * 3
        assertThat(fareService.calculateFare(subwayPath.calculateDistance())).isEqualTo(1650);
    }

    @Test
    void calculateFareOver50() {
        SubwayPath subwayPath = pathService.findPath(lines, 4L, 5L, PathType.DURATION);
        //
        assertThat(fareService.calculateFare(subwayPath.calculateDistance())).isEqualTo(2250);
    }

}
