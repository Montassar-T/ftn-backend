package com.ftn.backend.repository;

import com.ftn.backend.model.Sujet;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SujetRepository extends JpaRepository<Sujet, Long>, JpaSpecificationExecutor<Sujet> {

    Optional<Sujet> findByIdAndDeletedAtIsNull(Long id);

    List<Sujet> findByForum_IdAndDeletedAtIsNull(Long forumId);

    List<Sujet> findByTitreContainingIgnoreCaseAndDeletedAtIsNull(String titre);
}
