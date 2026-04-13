package com.carServices.backend.repository;

import com.carServices.backend.model.VehicleBrand;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VehicleBrandRepository
        extends JpaRepository<VehicleBrand, Long>, JpaSpecificationExecutor<VehicleBrand> {
    Optional<VehicleBrand> findByIdAndDeletedAtIsNull(Long id);
}
