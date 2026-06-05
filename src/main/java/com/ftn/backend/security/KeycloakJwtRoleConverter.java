package com.ftn.backend.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class KeycloakJwtRoleConverter
        implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {

        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

        if (realmAccess == null) {
            return List.of();
        }

        Object rolesObj = realmAccess.get("roles");

        if (!(rolesObj instanceof List<?> roles)) {
            return List.of();
        }

        return roles.stream()
                .map(Object::toString)
                .filter(role ->
                        role.equals("ROLE_ADMIN")
                                || role.equals("ROLE_COACH")
                                || role.equals("ROLE_ATHLETE"))
                .map(role -> (GrantedAuthority) new SimpleGrantedAuthority(role))
                .toList();
    }
}