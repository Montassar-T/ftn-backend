package com.ftn.backend.repository;

import com.ftn.backend.enums.CompetitionStatutEnum;
import com.ftn.backend.model.Competition;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CompetitionRepository extends JpaRepository<Competition, Long>, JpaSpecificationExecutor<Competition> {

    Optional<Competition> findByIdAndDeletedAtIsNull(Long id);

    List<Competition> findAllByDeletedAtIsNull();

    List<Competition> findByStatutAndDeletedAtIsNull(CompetitionStatutEnum statut);

    List<Competition> findByPool_IdAndDeletedAtIsNull(Long poolId);

    long countByDeletedAtIsNull();

    long countByStatutAndDeletedAtIsNull(CompetitionStatutEnum statut);

    List<Competition> findTop5ByDeletedAtIsNullOrderByCreatedAtDesc();
}
