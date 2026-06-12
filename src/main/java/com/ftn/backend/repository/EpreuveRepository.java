package com.ftn.backend.repository;

import com.ftn.backend.model.Epreuve;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EpreuveRepository extends JpaRepository<Epreuve, Long>, JpaSpecificationExecutor<Epreuve> {

    Optional<Epreuve> findByIdAndDeletedAtIsNull(Long id);

    List<Epreuve> findByCompetition_IdAndDeletedAtIsNull(Long competitionId);
}
