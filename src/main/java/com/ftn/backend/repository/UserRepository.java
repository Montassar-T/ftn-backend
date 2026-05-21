package com.ftn.backend.repository;

import com.ftn.backend.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByKeycloakIdAndDeletedAtIsNull(String KeyCloakId);

    Optional<User> findByEmailAndDeletedAtIsNull(String email);

    Optional<User> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByKeycloakIdAndDeletedAtIsNull(String KeyCloakId);
}
