package com.ftn.backend.repository;

import com.ftn.backend.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByIdAndDeletedAtIsNull(Long id);

    Optional<User> findByEmailAndDeletedAtIsNull(String email);

    List<User> findAllByDeletedAtIsNull();

    boolean existsByEmail(String email);
}
