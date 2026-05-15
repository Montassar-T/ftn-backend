package com.ftn.backend.repository;

import com.ftn.backend.model.Classement;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ClassementRepository extends JpaRepository<Classement, Long>, JpaSpecificationExecutor<Classement> {

    Optional<Classement> findByIdAndDeletedAtIsNull(Long id);

    List<Classement> findByAthlete_IdAndDeletedAtIsNull(Long athleteId);

    List<Classement> findBySwimStyleAndDistanceAndSeasonAndDeletedAtIsNullOrderByRankAsc(
            String swimStyle, String distance, String season);

    Optional<Classement> findByAthlete_IdAndSwimStyleAndDistanceAndSeasonAndDeletedAtIsNull(
            Long athleteId, String swimStyle, String distance, String season);
}
