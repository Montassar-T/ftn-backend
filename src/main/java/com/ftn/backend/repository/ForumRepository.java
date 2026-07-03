package com.ftn.backend.repository;

import com.ftn.backend.model.Forum;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ForumRepository extends JpaRepository<Forum, Long>, JpaSpecificationExecutor<Forum> {

    Optional<Forum> findByIdAndDeletedAtIsNull(Long id);

    List<Forum> findAllByDeletedAtIsNull();
}
