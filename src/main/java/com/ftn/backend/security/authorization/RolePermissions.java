package com.ftn.backend.security.authorization;

import com.ftn.backend.enums.Permission;
import com.ftn.backend.enums.Role;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RolePermissions {

    public static Set<Permission> getPermissions(Role role) {
        return switch (role) {
            case ADMIN -> adminPermissions();
            case ATHLETE, COACH -> athletePermissions();
            case USER -> Collections.emptySet();
        };
    }

    private static Set<Permission> athletePermissions() {
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.SUJET_CREATE);
        permissions.add(Permission.SUJET_UPDATE_OWN);
        permissions.add(Permission.SUJET_DELETE_OWN);
        permissions.add(Permission.REPONSE_CREATE);
        permissions.add(Permission.REPONSE_UPDATE_OWN);
        permissions.add(Permission.REPONSE_DELETE_OWN);
        permissions.add(Permission.REPONSE_LIKE);
        permissions.add(Permission.REPONSE_SIGNALER);
        return Collections.unmodifiableSet(permissions);
    }

    private static Set<Permission> adminPermissions() {
        Set<Permission> permissions = new HashSet<>(athletePermissions());
        permissions.add(Permission.SUJET_EPINGLER);
        permissions.add(Permission.SUJET_FERMER);
        permissions.add(Permission.SUJET_DELETE_ANY);
        permissions.add(Permission.SUJET_UPDATE_ANY);
        permissions.add(Permission.REPONSE_DELETE_ANY);
        permissions.add(Permission.FORUM_CREATE);
        permissions.add(Permission.FORUM_UPDATE);
        permissions.add(Permission.FORUM_DELETE);
        permissions.add(Permission.RESERVATION_APPROVE);
        return Collections.unmodifiableSet(permissions);
    }
}
