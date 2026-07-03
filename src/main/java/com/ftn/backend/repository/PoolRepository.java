package com.ftn.backend.repository;

import com.ftn.backend.model.Pool;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PoolRepository extends JpaRepository<Pool, Long>, JpaSpecificationExecutor<Pool> {

    Optional<Pool> findByIdAndDeletedAtIsNull(Long id);

    List<Pool> findAllByDeletedAtIsNull();

    List<Pool> findByActifTrueAndDeletedAtIsNull();

    long countByDeletedAtIsNull();
}
