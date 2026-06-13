package com.ftn.backend.repository;

import com.ftn.backend.model.Record;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RecordRepository extends JpaRepository<Record, Long>, JpaSpecificationExecutor<Record> {
    Optional<Record> findByIdAndDeletedAtIsNull(Long id);

    List<Record> findByAthlete_IdAndDeletedAtIsNull(Long athleteId);

    List<Record> findAllByDeletedAtIsNull();
}
