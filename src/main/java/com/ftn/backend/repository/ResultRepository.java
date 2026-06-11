package com.ftn.backend.repository;

import com.ftn.backend.model.Result;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ResultRepository extends JpaRepository<Result, Long>, JpaSpecificationExecutor<Result> {

    Optional<Result> findByIdAndDeletedAtIsNull(Long id);

    List<Result> findByAthlete_IdAndDeletedAtIsNull(Long athleteId);

    List<Result> findByCompetition_IdAndDeletedAtIsNull(Long competitionId);

    long countByDeletedAtIsNull();
}
