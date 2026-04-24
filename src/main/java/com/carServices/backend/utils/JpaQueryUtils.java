package com.carServices.backend.utils;

import com.carServices.backend.dtos.JpaQueryDto;
import com.carServices.backend.model.*;
import java.util.Map;

public class JpaQueryUtils {

    private static final Map<Class<?>, Map<String, JpaQueryDto>> correspondanceEntityMap = Map.ofEntries(
            Map.entry(
                    Vehicle.class,
                    Map.of(
                            "model_id", new JpaQueryDto("model", "id", VehicleModel.class, null),
                            "client_id", new JpaQueryDto("client", "id", Client.class, null))),
            Map.entry(
                    ActivityLog.class,
                    Map.of(
                            "user_id", new JpaQueryDto("user", "id", User.class, null))));
    // FIXME THE MAP ABOCE IS WRONG
    // This class MUST remain immutable
    // spotless:off
    // @formatter:off

    // [         KEY      : [       KEY     : [                     VALUE1                     |              VALUE2
    //           |    VALUE3     |  VALUE4]]
    // [ Principal entity : [Param from URL : [ joinColumn between Principal and joined entity | column to filter in
    // joined entity | Joined entity | next join]]

    private JpaQueryUtils() {
        // Utility class
    }

    // @formatter:on
    // spotless:off
    public static JpaQueryDto getParameterMapping(Class<?> clazz, String param) {
        Map<String, JpaQueryDto> entityMap = correspondanceEntityMap.get(clazz);
        if (entityMap != null) {
            return entityMap.get(param);
        } else {
            return null;
        }
    }
}
