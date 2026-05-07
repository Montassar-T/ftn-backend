package com.ftn.backend.repository;

import com.ftn.backend.model.Inscription;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InscriptionRepository
        extends JpaRepository<Inscription, Long>, JpaSpecificationExecutor<Inscription> {

    Optional<Inscription> findByIdAndDeletedAtIsNull(Long id);

    List<Inscription> findByAthlete_IdAndDeletedAtIsNull(Long athleteId);

    List<Inscription> findByEpreuveIdAndDeletedAtIsNull(Long epreuveId);
}
