package com.ftn.backend.repository;

import com.ftn.backend.model.ForumReaction;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForumReactionRepository extends JpaRepository<ForumReaction, Long> {
    Optional<ForumReaction> findByIdAndDeletedAtIsNull(Long id);

    List<ForumReaction> findByPost_IdAndDeletedAtIsNull(Long postId);

    List<ForumReaction> findByThread_IdAndDeletedAtIsNull(Long threadId);
}
