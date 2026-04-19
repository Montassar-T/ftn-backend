package com.carServices.backend.repository;

import com.carServices.backend.model.Repair;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RepairRepository extends JpaRepository<Repair, Long>, JpaSpecificationExecutor<Repair> {

    Optional<Repair> findByIdAndDeletedAtIsNull(Long id);
}
