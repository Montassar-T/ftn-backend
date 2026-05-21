package com.ftn.backend.service;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KeycloakService {

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    public String createUser(String email,
                             String password,
                             String firstName,
                             String lastName,
                             String role) {

        RealmResource realmResource = keycloak.realm(realm);

        // 1. Build Keycloak user
        UserRepresentation user = new UserRepresentation();
        user.setUsername(email);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(true);
        user.setEmailVerified(true);

        // 2. Create user
        Response response = realmResource.users().create(user);

        if (response.getStatus() != 201) {
            throw new RuntimeException("Failed to create Keycloak user: " + response.getStatus());
        }

        String userId = response.getLocation()
                .getPath()
                .replaceAll(".*/([^/]+)$", "$1");

        // 3. Set password
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);

        realmResource.users()
                .get(userId)
                .resetPassword(credential);

        // 4. Assign role (ROLE_ATHLETE / ROLE_COACH)
        RoleRepresentation kcRole = realmResource
                .roles()
                .get(role)
                .toRepresentation();

        realmResource.users()
                .get(userId)
                .roles()
                .realmLevel()
                .add(List.of(kcRole));

        return userId;
    }
}