package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.hhplus.tdd.point.TransactionType.CHARGE;
import static io.hhplus.tdd.point.TransactionType.USE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * 포인트 충전/사용 내역 조회 테스트
 */
public class HistoryServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(HistoryServiceTest.class);

    private PointHistoryTable pointHistoryTable = new PointHistoryTable();

    private static final long user1Id = 1L;
    private static final long user2Id = 2L;
    private static final long user3Id = 3L;

    private long randomPoint;   // 0 ~ 10000
    private long randomType;    // 0 or 1

    /**
     * DB 포인트 충전/사용 내역 더미 데이터 생성
     * 총 30개 데이터
     */
    @BeforeEach
    public void setup() {
        setHistory(user1Id);
        setHistory(user2Id);
        setHistory(user3Id);
        logger.info("충전/사용 내역 데이터 생성");
    }

    /**
     * 각 유저별 10개 충전/사용 내역 데이터 랜덤 생성
     * @param userId
     */
    public void setHistory(long userId) {
        for (int i = 0; i < 10; i++) {
            randomPoint = (long)(Math.random() * 10001);
            randomType = (long)(Math.random() * 2);
            pointHistoryTable.insert(userId, randomPoint, (randomType == 1 ? TransactionType.CHARGE : USE), System.currentTimeMillis());
        }
    }

    /**
     * 유저 포인트 충전 및 사용 내역 조회 테스트 - 성공
     */
    @Test
    public void selectPointHistory() {
        logger.info("[포인트 충전 및 사용 내역 조회 테스트] 시작!");
        //given
        //요청한 유저의 id
        long userId = user1Id;

        //when
        //유저 포인트 충전 내역 DB 조회
        List<PointHistory> pointChargeHistoryList = selectPointChargeHistoryList(userId);
        //유저 포인트 사용 내역 DB 조회
        List<PointHistory> pointUseHistoryList = selectPointUseHistoryList(userId);
        //유저 포인트 충전,사용 모든 내역 DB 조회
        List<PointHistory> pointHistoryList = Stream.concat(pointChargeHistoryList.stream(), pointUseHistoryList.stream()).collect(Collectors.toList());
        logger.info("DB 조회 결과 : 총 건수[{}] 데이터 {}", pointHistoryList.size(), pointHistoryList);

        //then
        //요청한 유저만 존재한다.
        //요청한 유저의 포인트 충전 내역과 사용 내역이 모두 존재한다.
        assertThat(pointHistoryList)
                .extracting("userId")
                .doesNotContain(user2Id,user3Id)
                .contains(userId);
        assertThat(pointHistoryList)
                .extracting("type")
                .contains(CHARGE, USE);
        logger.info("[포인트 충전 및 사용 내역 조회 테스트] 성공!");
    }

    /**
     * 포인트 충전 내역 조회 - 성공
     */
    @Test
    public void selectPointChargeHistory() {
        //given
        long userId = user1Id;

        //when
        //충전 내역 DB 조회
        List<PointHistory> pointChargeHistoryList = selectPointChargeHistoryList(userId);
        logger.info(pointChargeHistoryList.toString());

        //then
        //요청한 유저만 존재한다.
        //요청한 유저의 포인트 충전 내역만 포함하며, 사용 내역은 포함하지 않는다.
        assertThat(pointChargeHistoryList)
                .extracting("userId")
                .doesNotContain(user2Id,user3Id);
        assertThat(pointChargeHistoryList)
                .extracting("type")
                .contains(CHARGE)
                .doesNotContain(USE);
        logger.info("포인트 충전 내역 조회 성공");
    }

    /**
     * 포인트 사용 내역 조회 - 성공
     */
    @Test
    public void selectPointUseHistory() {
        //given
        long userId = user1Id;

        //when
        //사용 내역 DB 조회
        List<PointHistory> pointUseHistoryList = selectPointUseHistoryList(userId);
        logger.info(pointUseHistoryList.toString());

        //then
        //요청한 유저만 존재한다.
        //요청한 유저의 포인트 사용 내역만 포함하며, 충전 내역은 포함하지 않는다.
        assertThat(pointUseHistoryList)
                .extracting("userId")
                .doesNotContain(user2Id,user3Id);
        assertThat(pointUseHistoryList)
                .extracting("type")
                .contains(USE)
                .doesNotContain(CHARGE);
        logger.info("포인트 사용 내역 조회 성공");
    }

    /**
     * 포인트 충전 내역 DB 조회
     * @param userId
     * @return List<PointHistory>
     */
    public List<PointHistory> selectPointChargeHistoryList(long userId) {
        List<PointHistory> chargeHistoryList = pointHistoryTable.selectAllByUserId(userId);
        return chargeHistoryList.stream().filter(chargeHistory ->
                chargeHistory.userId() == userId && chargeHistory.type().equals(TransactionType.CHARGE)
        ).toList();
    }

    /**
     * 포인트 사용 내역 DB 조회
     *
     * @param userId
     * @return List<PointHistory>
     */
    public List<PointHistory> selectPointUseHistoryList(long userId) {
        List<PointHistory> useHistoryList = pointHistoryTable.selectAllByUserId(userId);
        return useHistoryList.stream().filter(useHistory ->
                useHistory.userId() == userId && useHistory.type().equals(USE)
        ).toList();
    }
}