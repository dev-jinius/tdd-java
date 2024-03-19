package io.hhplus.tdd.point.dto;

import io.hhplus.tdd.point.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PointHistoryDto {
    private long id;
    private long userId;
    private long amount;
    private TransactionType type;
    private long updateMilli;
}
