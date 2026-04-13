package com.carServices.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class JpaQueryDto {
    private String joinColumnName;
    private String columnName;
    private Class<?> entityClazz;
    private String nextJoin;
}
