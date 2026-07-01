package com.ftn.backend.repository;

import com.ftn.backend.model.Pool;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PoolRepository extends JpaRepository<Pool, Long>, JpaSpecificationExecutor<Pool> {

    Optional<Pool> findByIdAndDeletedAtIsNull(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Pool p WHERE p.id = :id AND p.deletedAt IS NULL")
    Optional<Pool> findByIdAndDeletedAtIsNullForUpdate(@Param("id") Long id);

    List<Pool> findAllByDeletedAtIsNull();

    List<Pool> findByActifTrueAndDeletedAtIsNull();

    long countByDeletedAtIsNull();
}
