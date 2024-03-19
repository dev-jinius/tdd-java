package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.exception.TddCustomException;
import io.hhplus.tdd.point.PointController;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.dto.UserPointDto;
import lombok.AllArgsConstructor;
import org.apache.catalina.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 유저의 포인트 조회 서비스
 */
@Service
@AllArgsConstructor
public class PointService {
    private static final Logger log = LoggerFactory.getLogger(PointService.class);

    private final UserPointTable userPointTable;    // 유저 포인트 DB 테이블

    /**
     * 유저 id로 포인트 조회
     * @param userId
     * @return
     */
    public UserPointDto selectPointByUserId(long userId) {
        try {
            UserPoint dbUserPointEntity = userPointTable.selectById(userId); // userId로 DB 조회
            UserPointDto dbUser = dbUserPointEntity.toDto();
            log.info("조회한 유저 정보 : {}", dbUser.toString());
            return dbUser;
        } catch (NullPointerException e) {
            throw new TddCustomException("err-01", "DB에 유저가 존재하지 않습니다.");
        }
    }
}
