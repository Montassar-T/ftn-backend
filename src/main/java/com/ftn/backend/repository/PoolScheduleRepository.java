package com.ftn.backend.repository;

import com.ftn.backend.model.PoolSchedule;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PoolScheduleRepository extends JpaRepository<PoolSchedule, Long> {
    Optional<PoolSchedule> findByIdAndDeletedAtIsNull(Long id);

    List<PoolSchedule> findByPool_IdAndDeletedAtIsNull(Long poolId);
}
