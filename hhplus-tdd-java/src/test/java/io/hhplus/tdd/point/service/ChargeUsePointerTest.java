package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.dto.UserPointDto;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 포인트 충전, 사용 서비스 테스트
 */
public class ChargeUsePointerTest {

    private static final Logger logger = LoggerFactory.getLogger(ChargeUsePointerTest.class);

    private UserPointTable userPointTable = new UserPointTable();
    private PointHistoryTable pointHistoryTable = new PointHistoryTable();
    private PointService pointService = new PointService(userPointTable);
    private ChargeService chargeService = new ChargeService(userPointTable, pointHistoryTable, pointService);
    private UseService useService = new UseService(userPointTable, pointHistoryTable, pointService);

    /**
     * 포인트 충전과 사용 요청 통합 테스트
     *  - 조건 1) 잔고가 부족할 경우, 포인트 사용은 실패
     *  - 조건 2) 여러 건의 포인트 충전과 사용 요청이 들어오는 경우, 순차적 처리 테스트 (synchronized)
     */
    @Test
    public void chargeAndUseSynchronizedTest() throws InterruptedException {
        long userId = 1L;
        int core = 10;
        ExecutorService executor = Executors.newFixedThreadPool(core);
        CountDownLatch latch = new CountDownLatch(core);
        List<String> startThreadList = new ArrayList<>();
        List<String> finishedThreadList = new ArrayList<>();

        for (int i = 0; i < core; i++) {
            long chargePoint = (long)(Math.random()*5+1)*1000;
            executor.submit(() -> {
                synchronized (this) {
                    long randomNum = (long)(Math.random()*2+1);
                    logger.info("randomNum : [{}]", randomNum);
                    logger.info("현재 스레드 : [{}]", Thread.currentThread().getName());
                    startThreadList.add(Thread.currentThread().getName());
                    try {
                        if (randomNum == 1) {
                            useService.useUserPoint(userId, chargePoint);
                        } else {
                            chargeService.chargeUserPoint(userId, chargePoint);
                        }
                    } catch (Exception e) {
                        logger.info(e.getMessage());
                    } finally {
                        logger.info("완료한 스레드 : [{}]", Thread.currentThread().getName());
                        finishedThreadList.add(Thread.currentThread().getName());
                        latch.countDown();
                    }
                }
            });
        }
        latch.await();
        executor.shutdown();

        assertThat(startThreadList).isEqualTo(finishedThreadList);
    }
}
