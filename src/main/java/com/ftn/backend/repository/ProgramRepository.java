package com.ftn.backend.repository;

import com.ftn.backend.model.Program;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProgramRepository extends JpaRepository<Program, Long>, JpaSpecificationExecutor<Program> {

    Optional<Program> findByIdAndDeletedAtIsNull(Long id);

    List<Program> findAllByDeletedAtIsNull();

    List<Program> findByActifTrueAndDeletedAtIsNull();

    long countByDeletedAtIsNull();
}
