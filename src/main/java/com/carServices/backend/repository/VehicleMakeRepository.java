package com.carServices.backend.repository;

import com.carServices.backend.model.VehicleMake;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VehicleMakeRepository extends JpaRepository<VehicleMake, Long>, JpaSpecificationExecutor<VehicleMake> {
    Optional<VehicleMake> findByIdAndDeletedAtIsNull(Long id);
}
