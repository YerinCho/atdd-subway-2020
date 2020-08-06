package wooteco.subway.maps.map.acceptance;

import static wooteco.subway.maps.line.acceptance.step.LineStationAcceptanceStep.*;
import static wooteco.subway.maps.map.acceptance.step.PathAcceptanceStep.*;
import static wooteco.subway.members.member.acceptance.step.MemberAcceptanceStep.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import wooteco.security.core.TokenResponse;
import wooteco.subway.common.acceptance.AcceptanceTest;
import wooteco.subway.maps.line.acceptance.step.LineAcceptanceStep;
import wooteco.subway.maps.line.dto.LineResponse;
import wooteco.subway.maps.station.acceptance.step.StationAcceptanceStep;
import wooteco.subway.maps.station.dto.StationResponse;
import wooteco.subway.members.member.acceptance.step.MemberAcceptanceStep;

@DisplayName("로그인 사용자별 지하철 요금 관련 기능")
public class FareAcceptanceTest extends AcceptanceTest {

    public static final int ADULT_AGE = 22;
    public static final int YOUTH_AGE = 15;
    public static final int CHILD_AGE = 10;
    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        교대역 = 지하철역_등록되어_있음("교대역");
        강남역 = 지하철역_등록되어_있음("강남역");
        양재역 = 지하철역_등록되어_있음("양재역");
        남부터미널역 = 지하철역_등록되어_있음("남부터미널역");

        이호선 = 지하철_노선_등록되어_있음("2호선", "GREEN", 100);
        신분당선 = 지하철_노선_등록되어_있음("신분당선", "RED", 100);
        삼호선 = 지하철_노선_등록되어_있음("3호선", "ORANGE", 100);

        지하철_노선에_지하철역_등록되어_있음(이호선, null, 교대역, 0, 0);
        지하철_노선에_지하철역_등록되어_있음(이호선, 교대역, 강남역, 2, 2);

        지하철_노선에_지하철역_등록되어_있음(신분당선, null, 강남역, 0, 0);
        지하철_노선에_지하철역_등록되어_있음(신분당선, 강남역, 양재역, 2, 1);

        지하철_노선에_지하철역_등록되어_있음(삼호선, null, 교대역, 0, 0);
        지하철_노선에_지하철역_등록되어_있음(삼호선, 교대역, 남부터미널역, 1, 2);
        지하철_노선에_지하철역_등록되어_있음(삼호선, 남부터미널역, 양재역, 2, 2);
    }

    private Long 지하철_노선_등록되어_있음(String name, String color, int extraFare) {
        ExtractableResponse<Response> createLineResponse1 = LineAcceptanceStep.지하철_노선_등록되어_있음(name, color, extraFare);
        return createLineResponse1.as(LineResponse.class).getId();
    }

    private Long 지하철역_등록되어_있음(String name) {
        ExtractableResponse<Response> createdStationResponse1 = StationAcceptanceStep.지하철역_등록되어_있음(name);
        return createdStationResponse1.as(StationResponse.class).getId();
    }

    @Test
    @DisplayName("비로그인 사용자가 경로를 조회했을 때 요금 표시")
    void calculateFareWhenNonMember() {
        //when
        ExtractableResponse<Response> response = 거리_경로_조회_요청("DISTANCE", 1L, 3L);
        //then
        총_거리와_소요_시간_요금을_함께_응답함(response, 3, 4, 1250);
    }

    @Test
    @DisplayName("로그인한 성인 사용자가 경로를 조회한다.")
    void calculateFareAdult() {
        // when
        ExtractableResponse<Response> 성인 = MemberAcceptanceStep.회원_생성을_요청("a@a", "PASSWORD", ADULT_AGE);
        TokenResponse tokenResponse = 로그인_되어_있음("a@a", "PASSWORD");
        ExtractableResponse<Response> response = 사용자에_따른_거리_경로_조회_요청(tokenResponse, "DISTANCE", 1L, 3L);

        //then
        총_거리와_소요_시간_요금을_함께_응답함(response, 3, 4, 1250);
    }

    @Test
    @DisplayName("로그인한 청소년 사용자가 경로를 조회한다.")
    void calculateFareYouth() {
        //when
        ExtractableResponse<Response> 청소년 = MemberAcceptanceStep.회원_생성을_요청("b@b", "PASSWORD", YOUTH_AGE);
        TokenResponse tokenResponse = 로그인_되어_있음("b@b", "PASSWORD");
        ExtractableResponse<Response> response = 사용자에_따른_거리_경로_조회_요청(tokenResponse, "DISTANCE", 1L, 3L);

        //then
        총_거리와_소요_시간_요금을_함께_응답함(response, 3, 4, (int)((1250 - 350) * 0.8));
    }

    @Test
    @DisplayName("로그인한 어린이 사용자가 경로를 조회한다.")
    void calculateFareChild() {
        //when
        ExtractableResponse<Response> 어린이 = MemberAcceptanceStep.회원_생성을_요청("c@c", "PASSWORD", CHILD_AGE);
        TokenResponse tokenResponse = 로그인_되어_있음("c@c", "PASSWORD");
        ExtractableResponse<Response> response = 사용자에_따른_거리_경로_조회_요청(tokenResponse, "DISTANCE", 1L, 3L);

        //then
        총_거리와_소요_시간_요금을_함께_응답함(response, 3, 4, (int)((1250 - 350) * 0.5));
    }
}
