package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.dto.UserPointDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

/**
 * 포인트 조회 기능 테스트
 */
public class PointServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(PointServiceTest.class);

    private UserPointTable userPointTable = new UserPointTable();


    /**
     * DB 유저 더미 데이터 생성
     * 단, id는 1 ~ 5까지 정수
     */
    @BeforeEach
    public void addUserData() {
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
        long id = (int)(Math.random() * 5) + 1;             //요청한 유저 id (1 ~ 5까지의 정수)
        UserPointDto user = new UserPointDto(id, 10000L, System.currentTimeMillis());           //요청한 유저 생성
        logger.info("유저 생성 - id[" + user.getId() + "]");

        return user;
    }

    /**
     *  포인트 조회 - 성공
     */
    @Test
    void selectPointByUserId() {
        logger.info("[포인트 조회 테스트] selectPointByUserId");
        //given
        UserPointDto requestUser = createUser();    //사용자 생성
        
        //when
        UserPoint dbUser = userPointTable.selectById(requestUser.getId());

        //then
        //요청한 유저의 id로 DB 조회한 유저가 존재한다.
        assertNotNull(dbUser);
        //조회 요청한 유저와 DB 조회한 유저의 id가 같다
        assertEquals(requestUser.getId(), dbUser.id());
        //포인트 조회에 성공한다.
        assertNotNull(dbUser.point());
    }

    /**
     * 포인트 조회 - 유저 조회 실패
     */
    @Test
    void notFoundUser() {
        logger.info("[포인트 조회 실패 테스트] notFoundUser");
        //given
        UserPointDto dto = createUser();

        //when
        //DB에 없는 유저 조회
        UserPoint dbUser = userPointTable.selectById(dto.getId());
        dbUser = null;

        //then
        assertNull(dbUser);
    }
}