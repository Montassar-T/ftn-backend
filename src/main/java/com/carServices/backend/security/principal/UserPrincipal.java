package com.carServices.backend.security.principal;

import com.carServices.backend.enums.Permission;
import com.carServices.backend.enums.Role;
import com.carServices.backend.model.User;
import com.carServices.backend.model.UserStatus;
import com.carServices.backend.security.authorization.RolePermissions;
import java.util.*;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final String firstName;
    private final String lastName;
    private final UserStatus status;
    private final Role role;

    public UserPrincipal(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.status = user.getStatus();
        this.role = user.getRole();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Set<Permission> permissions = RolePermissions.getPermissions(role);

        List<GrantedAuthority> authorities = new ArrayList<>();

        // ROLE
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));

        // PERMISSIONS
        permissions.forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.name())));

        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isEnabled() {
        return status == UserStatus.ACTIVE;
    }
}
