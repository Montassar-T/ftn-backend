package com.carServices.backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SingleResultDto<T> {
    @JsonProperty("data")
    private T data;
}
