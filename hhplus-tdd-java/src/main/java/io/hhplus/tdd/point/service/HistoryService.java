package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.dto.PointHistoryDto;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.hhplus.tdd.point.TransactionType.USE;

/**
 * 포인트 충전/이용 내역 조회 서비스
 */
@Service
@RequiredArgsConstructor
public class HistoryService {
    private static final Logger log = LoggerFactory.getLogger(PointService.class);

    private final PointHistoryTable pointHistoryTable;  // 포인트 내역 테이블

    /**
     * 포인트 충전 및 사용 내역 조회
     * @param userId
     * @return
     */
    public List<PointHistoryDto> selectPointHistory(long userId) {
        List<PointHistory> dbList = selectPointAllHistoryList(userId);
        log.info("포인트 내역 : {}", dbList.toString());
        return dbList.stream().map(PointHistory::toDto).collect(Collectors.toList());
    }

    /**
     * 포인트 모든 내역 DB 조회
     * @param userId
     * @return List<PointHistory>
     */
    public List<PointHistory> selectPointAllHistoryList(long userId) {
        List<PointHistory> allHistoryList = pointHistoryTable.selectAllByUserId(userId);
        return allHistoryList.stream().filter(chargeHistory -> chargeHistory.userId() == userId).toList();
    }
}
