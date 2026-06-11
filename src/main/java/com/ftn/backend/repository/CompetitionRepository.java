package com.ftn.backend.repository;

import com.ftn.backend.enums.CompetitionStatutEnum;
import com.ftn.backend.model.Competition;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CompetitionRepository extends JpaRepository<Competition, Long>, JpaSpecificationExecutor<Competition> {

    Optional<Competition> findByIdAndDeletedAtIsNull(Long id);

    Optional<Competition> findByEvenement_IdAndDeletedAtIsNull(Long evenementId);

    List<Competition> findAllByDeletedAtIsNull();

    List<Competition> findByStatutAndDeletedAtIsNull(CompetitionStatutEnum statut);

    List<Competition> findByStatusAndDeletedAtIsNull(String status);

    List<Competition> findByPool_IdAndDeletedAtIsNull(Long poolId);

    long countByDeletedAtIsNull();

    long countByStatutAndDeletedAtIsNull(CompetitionStatutEnum statut);

    List<Competition> findTop5ByDeletedAtIsNullOrderByCreatedAtDesc();

    /**
     * Finds competitions whose date range overlaps [startDate, endDate].
     * Overlap condition: existingStart <= newEnd AND existingEnd >= newStart
     */
    @Query("SELECT c FROM Competition c WHERE c.deletedAt IS NULL " + "AND (:excludeId IS NULL OR c.id <> :excludeId) "
            + "AND c.startDate IS NOT NULL AND c.endDate IS NOT NULL "
            + "AND c.startDate <= :endDate AND c.endDate >= :startDate "
            + "ORDER BY c.startDate ASC")
    List<Competition> findConflicting(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("excludeId") Long excludeId);
}
