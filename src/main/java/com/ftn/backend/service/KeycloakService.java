package com.ftn.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ftn.backend.dtos.TokenResponseDto;
import com.ftn.backend.exception.auth.AuthenticationException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KeycloakService {

    private final Keycloak keycloak;
    private final HttpClient httpClient;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    public String createUser(String email,
                             String password,
                             String firstName,
                             String lastName,
                             String role) {

        RealmResource realmResource = keycloak.realm(realm);

        // 1. Fetch role first
        RoleRepresentation kcRole = realmResource
                .roles()
                .get(role)
                .toRepresentation();

        // 2. Build user
        UserRepresentation user = new UserRepresentation();
        user.setUsername(email);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(true);
        user.setEmailVerified(true);

        // 3. Create user
        try (Response response = realmResource.users().create(user)) {
            if (response.getStatus() != 201) {
                throw new RuntimeException("Failed to create Keycloak user: " + response.getStatus());
            }

            String userId = response.getLocation()
                    .getPath()
                    .replaceAll(".*/([^/]+)$", "$1");

            // 4. Set password
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setTemporary(false);
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(password);
            realmResource.users().get(userId).resetPassword(credential);

            // 5. Assign role
            realmResource.users()
                    .get(userId)
                    .roles()
                    .realmLevel()
                    .add(List.of(kcRole));

            return userId;
        }
    }

    public void changePassword(
            String email,
            String currentPassword,
            String newPassword
    ) {
        // Verify the current password
        login(email, currentPassword);

        RealmResource realmResource = keycloak.realm(realm);

        List<UserRepresentation> users = realmResource.users().searchByEmail(email, true);

        if (users.isEmpty()) {
            throw new AuthenticationException("User not found");
        }

        String userId = users.get(0).getId();

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setTemporary(false);
        credential.setValue(newPassword);

        realmResource.users()
                .get(userId)
                .resetPassword(credential);
    }

    public TokenResponseDto login(String email, String password) {
        return callTokenEndpoint(
                "grant_type=password" +
                        "&client_id=" + clientId +
                        "&client_secret=" + clientSecret +
                        "&username=" + email +
                        "&password=" + password
        );
    }

    public TokenResponseDto refresh(String refreshToken) {
        return callTokenEndpoint(
                "grant_type=refresh_token" +
                        "&client_id=" + clientId +
                        "&client_secret=" + clientSecret +
                        "&refresh_token=" + refreshToken
        );
    }

    public void logout(String refreshToken) {
        try {
            String body = "client_id=" + clientId +
                    "&client_secret=" + clientSecret +
                    "&refresh_token=" + refreshToken;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl + "/realms/" + realm + "/protocol/openid-connect/logout"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 204) {
                throw new AuthenticationException("Failed to logout");
            }

        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthenticationException("Logout failed");
        }
    }

    public void deleteUser(String keycloakId) {
        try (Response response = keycloak.realm(realm).users().delete(keycloakId)) {
            if (response.getStatus() != 204) {
                throw new RuntimeException("Failed to delete Keycloak user: " + response.getStatus());
            }
        }
    }

    private TokenResponseDto callTokenEndpoint(String body) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl + "/realms/" + realm + "/protocol/openid-connect/token"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new AuthenticationException("Invalid credentials or token");
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(response.body());

            return TokenResponseDto.builder()
                    .accessToken(json.get("access_token").asText())
                    .refreshToken(json.get("refresh_token").asText())
                    .expiresIn(json.get("expires_in").asLong())
                    .build();

        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthenticationException("Token request failed");
        }
    }
}