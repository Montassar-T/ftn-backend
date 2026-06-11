package com.ftn.backend.repository;

import com.ftn.backend.model.Classement;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ClassementRepository extends JpaRepository<Classement, Long>, JpaSpecificationExecutor<Classement> {

    Optional<Classement> findByIdAndDeletedAtIsNull(Long id);

    List<Classement> findByAthlete_IdAndDeletedAtIsNull(Long athleteId);

    List<Classement> findByEpreuve_IdAndSeasonAndDeletedAtIsNullOrderByRankAsc(Long epreuveId, String season);

    Optional<Classement> findByAthlete_IdAndEpreuve_IdAndSeasonAndDeletedAtIsNull(
            Long athleteId, Long epreuveId, String season);
}
