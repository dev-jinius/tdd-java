package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.exception.TddCustomException;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.dto.UserPointDto;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static io.hhplus.tdd.point.TransactionType.USE;

/**
 * 포인트 사용 서비스
 */
@Service
@RequiredArgsConstructor
public class UseService {
    private static final Logger log = LoggerFactory.getLogger(ChargeService.class);

    private final UserPointTable userPointTable;        // 유저 포인트 DB 테이블
    private final PointHistoryTable pointHistoryTable;  // 포인트 내역 테이블
    private final PointService pointService;            // 포인트 조회 서비스

    /**
     * 포인트 사용
     * @param userId
     * @param usePoint
     * @return
     */
    public synchronized UserPointDto useUserPoint(long userId, long usePoint) {
        long totalPoint = 0;                            // 누적 포인트
        UserPoint resultUserPoint = null;               // 포인트 사용 후 남은 포인트
        PointHistory resultPointHistory = null;         // 포인트 사용 후 추가된 포인트 내역

        try {
            //유저의 포인트 조회
            UserPointDto originUserPointDto = pointService.selectPointByUserId(userId);
            //남은 포인트를 크게 해서 포인트 부족한 예외를 제외시킴.
//            originUserPointDto.setPoint(999999L);

            totalPoint = originUserPointDto.getPoint() - usePoint;
            log.info("기존 포인트[{}] / 사용한 포인트[{}] => 누적 포인트[{}]", originUserPointDto.getPoint(), usePoint, totalPoint);

            //사용할 포인트를 차감한 누적 포인트가 0보다 크거나 같아야 한다.
            if (totalPoint < 0) throw new TddCustomException("err-03", "포인트가 부족합니다.");
            else {
                //차감해서 유저 포인트 업데이트
                resultUserPoint = subtractPoint(userId, totalPoint);
                //포인트 사용 내역 추가
                addUseHistory(userId, usePoint);
            }
            return resultUserPoint.toDto();
        } catch (NumberFormatException e) {
            throw new TddCustomException("err-02", "DB 파라미터의 타입과 맞지 않습니다.");
        }
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
     * @param userId
     * @param usePoint
     * @return PointHistory
     */
    private PointHistory addUseHistory(long userId, long usePoint) {
        return pointHistoryTable.insert(userId, usePoint, USE, System.currentTimeMillis());
    }
}
