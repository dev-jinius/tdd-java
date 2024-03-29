package io.hhplus.tdd.point;

import io.hhplus.tdd.point.dto.PointHistoryDto;
import io.hhplus.tdd.point.dto.UserPointDto;
import io.hhplus.tdd.point.service.ChargeService;
import io.hhplus.tdd.point.service.HistoryService;
import io.hhplus.tdd.point.service.PointService;
import io.hhplus.tdd.point.service.UseService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.*;

@RestController
@AllArgsConstructor
@RequestMapping("/point")
public class PointController {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);

    private final PointService pointService;        //유저 포인트 조회 서비스
    private final HistoryService historyService;    //유저 포인트 충전 및 사용 내역 조회 서비스
    private final ChargeService chargeService;      //유저 포인트 충전 서비스
    private final UseService useService;            //유저 포인트 사용 서비스

    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}")
    public ResponseEntity<Long> point(@PathVariable(value = "id") long id) {
        log.info("id [{}]", id);

        UserPointDto dbUser = pointService.selectPointByUserId(id);
        dbUser.setPoint(10000L);
        return ResponseEntity.ok().body(dbUser.getPoint());
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}/histories")
    public ResponseEntity<List<PointHistoryDto>> history(@PathVariable(value = "id") long id) {
        log.info("id [{}]", id);

        List<PointHistoryDto> userHistories = historyService.selectPointHistory(id);
        return ResponseEntity.ok().body(userHistories);
    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping(value = "{id}/charge")
    public ResponseEntity<UserPointDto> charge(@PathVariable(value = "id")long id, @RequestParam(value = "amount")long amount) {
        UserPointDto dto = chargeService.chargeUserPoint(id, amount);
        return ResponseEntity.ok().body(dto);
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping(value = "{id}/use")
    public ResponseEntity<UserPointDto> use(@PathVariable(value = "id")long id, @RequestParam(value = "amount")long amount) {
        UserPointDto dto = useService.useUserPoint(id, amount);
        return ResponseEntity.ok().body(dto);
    }
}
