package com.ftn.backend.dtos.reservation;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignLanesDto {

    @NotEmpty(message = "You must assign at least one lane")
    private List<Integer> lanes;
}