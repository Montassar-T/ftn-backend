package com.ftn.backend.repository;

import com.ftn.backend.model.CompetitionStaff;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompetitionStaffRepository extends JpaRepository<CompetitionStaff, Long> {

    List<CompetitionStaff> findByCompetition_IdAndDeletedAtIsNull(Long competitionId);

    List<CompetitionStaff> findByUser_IdAndDeletedAtIsNull(Long userId);

    Optional<CompetitionStaff> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByCompetition_IdAndUser_IdAndDeletedAtIsNull(Long competitionId, Long userId);

    List<CompetitionStaff> findByUser_IdInAndDeletedAtIsNull(List<Long> userIds);

    List<CompetitionStaff> findByDeletedAtIsNull();

    long countByDeletedAtIsNull();
}
