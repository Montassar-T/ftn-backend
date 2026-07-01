package com.ftn.backend.repository;

import com.ftn.backend.model.Inscription;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InscriptionRepository extends JpaRepository<Inscription, Long>, JpaSpecificationExecutor<Inscription> {

    Optional<Inscription> findByIdAndDeletedAtIsNull(Long id);

    List<Inscription> findByAthlete_IdAndDeletedAtIsNull(Long athleteId);

    List<Inscription> findByEpreuve_IdAndDeletedAtIsNullOrderByRegisteredAtAsc(Long epreuveId);

    List<Inscription> findByEpreuve_IdAndDeletedAtIsNull(Long epreuveId);

    List<Inscription> findByEpreuve_Competition_IdAndDeletedAtIsNullOrderByRegisteredAtAsc(Long competitionId);

    List<Inscription> findByEpreuve_Competition_IdAndDeletedAtIsNull(Long competitionId);

    boolean existsByAthlete_IdAndEpreuve_IdAndDeletedAtIsNull(Long athleteId, Long epreuveId);

    long countByEpreuve_IdAndStatusAndDeletedAtIsNull(Long epreuveId, String status);
}
