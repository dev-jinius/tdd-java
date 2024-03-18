package io.hhplus.tdd.point.dto;

import io.hhplus.tdd.point.UserPoint;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPointDto {

    private long id;
    private long point;
    private long updateMillis;

    //entity to dto
    public UserPointDto toDto(UserPoint userPoint) {
        return UserPointDto.builder()
                .id(userPoint.id())
                .point(userPoint.point())
                .updateMillis(userPoint.updateMillis())
                .build();
    }

}
