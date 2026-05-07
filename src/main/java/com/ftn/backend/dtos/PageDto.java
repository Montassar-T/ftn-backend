package com.ftn.backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageDto<T> {
    @JsonProperty("total_count")
    private long total;

    @JsonProperty("data")
    private List<T> data;
}
