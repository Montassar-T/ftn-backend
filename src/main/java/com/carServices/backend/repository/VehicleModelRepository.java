package com.carServices.backend.repository;

import com.carServices.backend.model.VehicleModel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VehicleModelRepository
        extends JpaRepository<VehicleModel, Long>, JpaSpecificationExecutor<VehicleModel> {
    Optional<VehicleModel> findByIdAndDeletedAtIsNull(Long id);
}
