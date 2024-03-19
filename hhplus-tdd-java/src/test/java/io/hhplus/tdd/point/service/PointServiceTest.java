package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.exception.TddCustomException;
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
 * 포인트 조회 테스트
 */
public class PointServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(PointServiceTest.class);

    private UserPointTable userPointTable = new UserPointTable();


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
        assertNotNull(dbUser.toDto().getPoint());
    }

    /**
     * RuntimeException 예외 발생 테스트
     */
    @Test
    public void exceptionTest() {
        //given
        UserPointDto dto = createUser();

        //when
        Throwable exception = assertThrows(Exception.class, () -> {
            userPointTable.selectById(dto.getPoint());
            throw new RuntimeException("에러 발생");
        });

        logger.info(String.valueOf(exception instanceof Exception));
        logger.info(String.valueOf(exception instanceof RuntimeException));
        logger.info(String.valueOf(exception instanceof NullPointerException));

        //then
        assertEquals("에러 발생", exception.getMessage());
        assertInstanceOf(RuntimeException.class, exception);
        assertInstanceOf(Exception.class, exception);
    }

    /**
     * DB 조회한 유저가 NULL인 경우 예외 발생 테스트 (throw)
     */
    @Test
    public void throwExceptionTest() {
        // given
        UserPoint dbUserPointEntity = null;

        //when
        Throwable exception = assertThrows(NullPointerException.class, () -> {
            UserPointDto dbUser = dbUserPointEntity.toDto();
        });
        logger.info(String.valueOf(exception instanceof RuntimeException));
        logger.info(String.valueOf(exception instanceof NullPointerException));

        //then
        assertInstanceOf(NullPointerException.class, exception);
    }

    /**
     * DB 조회한 유저가 NULL인 경우 커스텀 예외 처리 테스트 (try-catch)
     */
    @Test
    public void tryCatchTest() {
        // given
        UserPoint dbUserPointEntity = null;

        //when
        Throwable exception = assertThrows(RuntimeException.class, () -> {
            try {
                UserPointDto dbUser = dbUserPointEntity.toDto();
            } catch (NullPointerException e) {
                throw new TddCustomException("err-01", "DB에 유저가 존재하지 않습니다.");
            }
        });
        logger.info(String.valueOf(exception instanceof RuntimeException));
        logger.info(String.valueOf(exception instanceof NullPointerException));

        //then
        assertInstanceOf(RuntimeException.class, exception);
        assertEquals("DB에 유저가 존재하지 않습니다.", exception.getMessage());
    }
}