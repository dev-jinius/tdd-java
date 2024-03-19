package io.hhplus.tdd.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TddCustomException extends RuntimeException {
    private final String code;
    private final String message;
}
