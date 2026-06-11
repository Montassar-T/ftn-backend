package com.ftn.backend.repository;

import com.ftn.backend.enums.CategorieForumEnum;
import com.ftn.backend.model.Sujet;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SujetRepository extends JpaRepository<Sujet, Long>, JpaSpecificationExecutor<Sujet> {

    Optional<Sujet> findByIdAndDeletedAtIsNull(Long id);

    List<Sujet> findByForum_IdAndDeletedAtIsNull(Long forumId);

    List<Sujet> findByTitreContainingIgnoreCaseAndDeletedAtIsNull(String titre);

    @Query(
            "SELECT s FROM Sujet s JOIN s.forum f WHERE f.categorie = :categorie AND s.deletedAt IS NULL AND f.deletedAt IS NULL ORDER BY s.epingle DESC, s.dateCreation DESC")
    List<Sujet> findByForumCategorieAndDeletedAtIsNull(@Param("categorie") CategorieForumEnum categorie);
}
