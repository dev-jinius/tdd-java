package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.exception.TddCustomException;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.dto.UserPointDto;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import static io.hhplus.tdd.point.TransactionType.CHARGE;

/**
 * 포인트 충전 서비스
 */
@Service
@AllArgsConstructor
public class ChargeService {
    private static final Logger log = LoggerFactory.getLogger(ChargeService.class);

    private final UserPointTable userPointTable;        // 유저 포인트 DB 테이블
    private final PointHistoryTable pointHistoryTable;  // 포인트 내역 테이블
    private final PointService pointService;            // 포인트 조회 서비스

    /**
     * 포인트 충전
     * @param userId
     * @param amount
     * @return
     */
    public UserPointDto chargeUserPoint(long userId, long amount) {
        long totalPoint = 0;                        // 누적 포인트
        UserPoint resultUserPoint = null;           // 충전된 유저 포인트
        PointHistory resultPointHistory = null;     // 충전된 포인트 내역

        try {
            //유저의 포인트 조회
            UserPointDto originUserPoint = pointService.selectPointByUserId(userId);
            if (!ObjectUtils.isEmpty(originUserPoint)) {
                totalPoint = originUserPoint.getPoint() + amount;
                //누적해서 유저 포인트 업데이트
                resultUserPoint = stackPoint(userId, totalPoint);
                //충전 내역 추가
                resultPointHistory = addChargeHistory(userId, amount);
            }

            return resultUserPoint.toDto();
        } catch (NumberFormatException e) {
            throw new TddCustomException("err-02", "DB 파라미터의 타입과 맞지 않습니다.");
        } catch (NullPointerException e) {
            throw new TddCustomException("err-01", "DB에 유저가 존재하지 않습니다.");
        }
    }

    /**
     * 포인트 누적
     * @param userId
     * @param amount
     * @return UserPoint
     */
    public UserPoint stackPoint(long userId, long amount) {
        return userPointTable.insertOrUpdate(userId, amount);
    }

    /**
     * 포인트 충전 내역 추가
     * @param userId
     * @param amount
     * @return PointHistory
     */
    public PointHistory addChargeHistory(long userId, long amount) {
        return pointHistoryTable.insert(userId, amount, CHARGE, System.currentTimeMillis());
    }
}
