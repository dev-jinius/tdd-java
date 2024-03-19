package io.hhplus.tdd;

import io.hhplus.tdd.exception.TddCustomException;
import io.hhplus.tdd.point.PointController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
class ApiControllerAdvice extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ApiControllerAdvice.class);
    /**
     * 기본 예외 처리
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity.status(500).body(new ErrorResponse("500", "에러가 발생했습니다."));
    }

    /**
     * 커스텀 예외 처리
     * @param e
     * @return
     */
    @ExceptionHandler(value = TddCustomException.class)
    public ResponseEntity<ErrorResponse> handlePointServiceException(TddCustomException e) {
        return ResponseEntity.status(400).body(new ErrorResponse(e.getCode(), e.getMessage()));
    }
}
