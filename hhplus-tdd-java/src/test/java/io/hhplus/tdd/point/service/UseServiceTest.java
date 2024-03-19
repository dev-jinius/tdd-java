package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.exception.TddCustomException;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.dto.UserPointDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import static io.hhplus.tdd.point.TransactionType.CHARGE;
import static io.hhplus.tdd.point.TransactionType.USE;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 포인트 사용 서비스 테스트
 */
public class UseServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(UseServiceTest.class);

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
     * 포인트 사용 - 성공
     */
    @Test
    public void useUserPoint() {
        //given
        UserPointDto requestUser = createUser();

        long usePoint = requestUser.getPoint();         // 요청한 사용할 포인트
        long totalPoint = 0;                            // 누적 포인트
        UserPoint resultUserPoint = null;               // 포인트 사용 후 남은 포인트
        PointHistory resultPointHistory = null;         // 포인트 사용 후 추가된 포인트 내역

        //when
        //유저의 포인트 조회
        UserPoint originUserPoint = pointServiceTest.selectUserPointByUserId(requestUser.getId());
        //남은 포인트를 크게 해서 포인트 부족한 예외를 제외시킴.
        UserPointDto dto = originUserPoint.toDto();
        dto.setPoint(999999L);

        totalPoint = dto.getPoint() - usePoint;
        logger.info("기존 포인트[{}] / 사용한 포인트[{}] => 누적 포인트[{}]", dto.getPoint(), usePoint, totalPoint);
        //사용할 포인트를 차감한 누적 포인트가 0보다 크거나 같아야 한다.
        if (totalPoint < 0) throw new TddCustomException("err-03", "포인트가 부족합니다.");
        else {
            //차감해서 유저 포인트 업데이트
            resultUserPoint = subtractPoint(requestUser.getId(), totalPoint);
            //포인트 사용 내역 추가
            resultPointHistory = addUseHistory(requestUser);
        }

        //then
        //사용 후 포인트는 기존에 있는 포인트에서 요청 포인트를 차감한 값과 같다.
        //유저 포인트 테이블과 포인트 내역 테이블에 모두 추가되어야 한다.
        assertEquals(resultUserPoint.point(), totalPoint);
        assertNotNull(resultUserPoint);
        assertNotNull(resultPointHistory);
    }

    /**
     * 포인트 사용 실패
     * - 남은 유저 포인트보다 사용 포인트가 더 큰 경우
     */
    @Test
    public void failedUsePoint_biggerThanPoint() {
        //given
        UserPointDto requestUser = createUser();
        long usePoint = requestUser.getPoint();         // 요청한 사용할 포인트
        logger.info("{} - {} = {}", Long.parseLong("1"), Long.parseLong("10"), Long.parseLong("1")-Long.parseLong("10"));
        //when
        Throwable exception = assertThrows(TddCustomException.class, () -> {
            long totalPoint;                            // 누적 포인트

            //유저의 포인트 조회
            UserPoint originUserPoint = pointServiceTest.selectUserPointByUserId(requestUser.getId());
            totalPoint = originUserPoint.point() - usePoint;
            logger.info("{} - {} = {}", originUserPoint.point(), usePoint, originUserPoint.point()-usePoint);

            //사용할 포인트를 차감한 누적 포인트가 0보다 크거나 같아야 한다.
            if (totalPoint < 0) throw new TddCustomException("err-03", "포인트가 부족합니다.");
            else {
                subtractPoint(requestUser.getId(), totalPoint);     //차감해서 유저 포인트 업데이트
                addUseHistory(requestUser);                         //사용 내역 추가
            }
        });
        logger.info(exception.toString());

        //then
        assertInstanceOf(TddCustomException.class, exception);
    }

    /**
     * DB 데이터 추가 실패 테스트
     * - 파라미터 타입이 맞지 않는 경우
     */
    @Test
    public void failed_dbInsert() {
        Throwable exception = assertThrows(NumberFormatException.class, () -> {
            long param = Long.parseLong("aa");
            subtractPoint(param, 10000L);
        });
        logger.info(exception.toString());

        assertInstanceOf(NumberFormatException.class, exception);
    }


    /**
     * 사용 포인트 차감
     * @param userId
     * @param point
     * @return UserPoint
     */
    private UserPoint subtractPoint(long userId, long point) {
        return userPointTable.insertOrUpdate(userId, point);
    }

    /**
     * 사용 내역 추가
     * @param userPointDto
     * @return PointHistory
     */
    private PointHistory addUseHistory(UserPointDto userPointDto) {
        return pointHistoryTable.insert(userPointDto.getId(), userPointDto.getPoint(), USE, System.currentTimeMillis());
    }
}