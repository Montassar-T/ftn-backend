package com.ftn.backend.repository;

import com.ftn.backend.model.Resultat;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ResultatRepository extends JpaRepository<Resultat, Long>, JpaSpecificationExecutor<Resultat> {

    Optional<Resultat> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByEpreuve_IdAndAthlete_IdAndDeletedAtIsNull(Long epreuveId, Long athleteId);

    List<Resultat> findByAthlete_IdAndDeletedAtIsNull(Long athleteId);

    List<Resultat> findByEpreuve_IdAndDeletedAtIsNull(Long epreuveId);

    List<Resultat> findByEpreuve_Competition_IdAndDeletedAtIsNull(Long competitionId);

    long countByDeletedAtIsNull();
}
