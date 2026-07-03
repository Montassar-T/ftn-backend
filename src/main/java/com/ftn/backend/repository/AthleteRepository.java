package com.ftn.backend.repository;

import com.ftn.backend.model.Athlete;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AthleteRepository extends JpaRepository<Athlete, Long>, JpaSpecificationExecutor<Athlete> {

    Optional<Athlete> findByIdAndDeletedAtIsNull(Long id);

    Optional<Athlete> findByUser_IdAndDeletedAtIsNull(Long userId);

    List<Athlete> findByClub_IdAndDeletedAtIsNull(Long clubId);

    long countByDeletedAtIsNull();
}
