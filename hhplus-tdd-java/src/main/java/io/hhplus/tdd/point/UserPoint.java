package io.hhplus.tdd.point;

import io.hhplus.tdd.point.dto.UserPointDto;
import lombok.Builder;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    public UserPointDto toDto() {
        return UserPointDto.builder()
                .id(this.id)
                .point(this.point)
                .updateMillis(this.updateMillis)
                .build();
    }
}
