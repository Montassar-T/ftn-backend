package com.ftn.backend.repository;

import com.ftn.backend.model.Club;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ClubRepository extends JpaRepository<Club, Long>, JpaSpecificationExecutor<Club> {

    Optional<Club> findByIdAndDeletedAtIsNull(Long id);

    List<Club> findAllByDeletedAtIsNull();

    List<Club> findByActifTrueAndDeletedAtIsNull();

    long countByDeletedAtIsNull();
}
