package com.ftn.backend.utils;

import com.ftn.backend.dtos.JpaQueryDto;
import com.ftn.backend.model.*;
import java.util.Map;

public class JpaQueryUtils {
    // NOTE - WE WILL IMPLEMENTED -- BY MONTASSAR
    private static final Map<Class<?>, Map<String, JpaQueryDto>> correspondanceEntityMap = Map.ofEntries();

    private JpaQueryUtils() {
        // Utility class
    }

    public static JpaQueryDto getParameterMapping(Class<?> clazz, String param) {
        Map<String, JpaQueryDto> entityMap = correspondanceEntityMap.get(clazz);
        if (entityMap != null) {
            return entityMap.get(param);
        } else {
            return null;
        }
    }
}
