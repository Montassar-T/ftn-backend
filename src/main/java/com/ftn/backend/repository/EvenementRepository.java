package com.ftn.backend.repository;

import com.ftn.backend.enums.EvenementStatus;
import com.ftn.backend.enums.EvenementType;
import com.ftn.backend.model.Evenement;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EvenementRepository extends JpaRepository<Evenement, Long>, JpaSpecificationExecutor<Evenement> {

    Optional<Evenement> findByIdAndDeletedAtIsNull(Long id);

    List<Evenement> findByTypeAndDeletedAtIsNull(EvenementType type);

    List<Evenement> findByStatusAndDeletedAtIsNull(EvenementStatus status);
}
