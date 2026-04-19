package com.carServices.backend.security.authorization;

import com.carServices.backend.enums.*;
import java.util.HashSet;
import java.util.Set;

public class RolePermissions {

    public static final Set<Permission> EMPLOYEE_PERMISSIONS = Set.of(
            Permission.VEHICLE_CREATE,
            Permission.VEHICLE_UPDATE,
            Permission.VEHICLE_READ,
            Permission.VEHICLE_DELETE,
            Permission.CLIENT_CREATE,
            Permission.CLIENT_UPDATE,
            Permission.CLIENT_READ,
            Permission.CLIENT_DELETE,
            Permission.MECHANIC_CREATE,
            Permission.MECHANIC_UPDATE,
            Permission.MECHANIC_READ,
            Permission.MECHANIC_DELETE,
            Permission.REPAIR_CREATE,
            Permission.REPAIR_READ,
            Permission.REPAIR_UPDATE,
            Permission.REPAIR_DELETE);

    public static final Set<Permission> ADMIN_PERMISSIONS;

    static {
        Set<Permission> admin = new HashSet<>(EMPLOYEE_PERMISSIONS);

        admin.addAll(
                Set.of(Permission.USER_CREATE, Permission.USER_UPDATE, Permission.USER_READ, Permission.USER_DELETE));

        ADMIN_PERMISSIONS = Set.copyOf(admin);
    }

    public static Set<Permission> getPermissions(Role role) {
        return switch (role) {
            case ADMIN -> ADMIN_PERMISSIONS;
            case EMPLOYEE -> EMPLOYEE_PERMISSIONS;
        };
    }
}
