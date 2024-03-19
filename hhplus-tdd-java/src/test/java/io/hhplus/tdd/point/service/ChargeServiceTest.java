package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.exception.TddCustomException;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.dto.UserPointDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.NestedCheckedException;
import org.springframework.util.ObjectUtils;

import java.io.UncheckedIOException;

import static io.hhplus.tdd.point.TransactionType.CHARGE;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 포인트 충전 서비스 테스트
 */
public class ChargeServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(ChargeServiceTest.class);

    private UserPointTable userPointTable = new UserPointTable();
    private PointHistoryTable pointHistoryTable = new PointHistoryTable();
    private PointServiceTest pointServiceTest = new PointServiceTest();

    /**
     * DB 유저 더미 데이터 생성
     * 단, id는 1 ~ 5까지 정수
     */
    @BeforeEach
    public void setup() {
        for (int i = 0; i < 5; i++) {
            userPointTable.insertOrUpdate((long)(i+1), 0L);
        }
        logger.info("테스트 유저 데이터 생성");
    }

    /**
     * 요청한 유저 생성
     * @return UserPointDto
     */
    public UserPointDto createUser() {
        long id = (long)(Math.random() * 5) + 1;        //요청한 유저 id (1 ~ 5까지의 정수)
        long point =  (long)(Math.random() * 10001);    //충전할 포인트 (0 ~ 10000까지의 정수)
        UserPointDto user = new UserPointDto(id, point, System.currentTimeMillis());     //요청한 유저 생성
        logger.info("유저 생성 - id[" + user.getId() + "]");

        return user;
    }

    /**
     * 포인트 충전 - 성공
     */
    @Test
    public void chargeUserPoint() {
        //given
        //요청한 유저 생성
        UserPointDto requestUser = createUser();

        long chargePoint = requestUser.getPoint();  // 요청한 충전 포인트
        long totalPoint = 0;                        // 누적 포인트
        UserPoint resultUserPoint = null;           // 충전된 유저 포인트
        PointHistory resultPointHistory = null;     // 충전된 포인트 내역

        //when
        //유저의 포인트 조회
        UserPoint originUserPoint = pointServiceTest.selectUserPointByUserId(requestUser.getId());
        if (!ObjectUtils.isEmpty(originUserPoint)) {
            totalPoint = originUserPoint.point() + chargePoint;
            //누적해서 유저 포인트 업데이트
            resultUserPoint = stackPoint(requestUser.getId(), totalPoint);
            //충전 내역 추가
            resultPointHistory = addChargeHistory(requestUser);
        }
        logger.info("기존 포인트[{}] / 충전한 포인트[{}] => 누적 포인트[{}]", originUserPoint.point(), chargePoint, totalPoint);
        logger.info("UserPoint {}", resultUserPoint.toDto().toString());
        logger.info("resultPointHistory {}", resultPointHistory.toDto().toString());

        //then
        //충전 후 유저 포인트는 기존에 있는 포인트와 요청 포인트를 합친 값과 같다.
        //유저 포인트 테이블과 포인트 내역 테이블에 모두 추가되어야 한다.
        assertEquals(resultUserPoint.point(), totalPoint);
        assertNotNull(resultUserPoint);
        assertNotNull(resultPointHistory);
    }

    /**
     * 포인트 충전 - 실패
     * DB 조회한 유저가 NULL인 경우 예외 발생 테스트 (throw)
     */
    @Test
    public void notFoundUser() {
        pointServiceTest.notFoundUser();
    }

    /**
     * 포인트 충전 - 실패
     * 파라미터 타입이 맞지 않는 경우 DB insert 예외 발생 테스트 (throw)
     */
    @Test
    public void failedStackPoint() {
        //given
        //when
        Throwable exception = assertThrows(NumberFormatException.class, () -> {
            long param = Long.parseLong("aa");
            userPointTable.insertOrUpdate(param, 1000L);
        });
        logger.info(exception.toString());
        logger.info("exception == NumberFormatException ? [{}]", String.valueOf(exception instanceof NumberFormatException));

        //then
        assertInstanceOf(NumberFormatException.class, exception);
    }

    /**
     * DB 조회한 유저가 NULL인 경우 커스텀 예외 처리 테스트 (try-catch)
     */
    @Test
    public void tryCatchNotFoundUser() {
        pointServiceTest.tryCatchTest();
    }

    /**
     * 파라미터 타입이 맞지 않는 경우 DB insert 예외 처리 테스트 (try-catch)
     */
    @Test
    public void tryCatchFailedStackPoint() {
        // given
        //when
        Throwable exception = assertThrows(TddCustomException.class, () -> {
            try {
                long param = Long.parseLong("aa");
                userPointTable.insertOrUpdate(param, 1000L);
            } catch (NumberFormatException e) {
                throw new TddCustomException("err-02", "DB 파라미터의 타입과 맞지 않습니다.");
            }
        });
        logger.info(exception.toString());
        logger.info(String.valueOf(exception instanceof TddCustomException));

        //then
        assertInstanceOf(TddCustomException.class, exception);
        assertEquals("DB 파라미터의 타입과 맞지 않습니다.", exception.getMessage());
    }

    /**
     * 포인트 누적
     */
    public UserPoint stackPoint(long userId, long amount) {
        return userPointTable.insertOrUpdate(userId, amount);
    }

    /**
     * 포인트 충전 내역 추가
     * @param userPointDto
     * @return PointHistory
     */
    public PointHistory addChargeHistory(UserPointDto userPointDto) {
        return pointHistoryTable.insert(userPointDto.getId(), userPointDto.getPoint(), CHARGE, System.currentTimeMillis());
    }
}