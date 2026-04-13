package com.carServices.backend.repository;

import com.carServices.backend.model.Mechanic;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MechanicRepository extends JpaRepository<Mechanic, Long>, JpaSpecificationExecutor<Mechanic> {
    Optional<Mechanic> findByIdAndDeletedAtIsNull(Long id);
}
