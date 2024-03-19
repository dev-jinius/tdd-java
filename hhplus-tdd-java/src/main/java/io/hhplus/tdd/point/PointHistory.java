package io.hhplus.tdd.point;

import io.hhplus.tdd.point.dto.PointHistoryDto;

public record PointHistory(
        long id,
        long userId,
        long amount,
        TransactionType type,
        long updateMillis
) {
    public PointHistoryDto toDto() {
        return PointHistoryDto.builder()
                .id(this.id)
                .id(this.userId)
                .id(this.amount)
                .id(this.type.ordinal())
                .id(this.updateMillis)
                .build();
    }
}
