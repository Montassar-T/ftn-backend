package com.ftn.backend.repository;

import com.ftn.backend.model.ClubMembership;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubMembershipRepository extends JpaRepository<ClubMembership, Long> {
    Optional<ClubMembership> findByIdAndDeletedAtIsNull(Long id);

    List<ClubMembership> findByClub_IdAndDeletedAtIsNull(Long clubId);

    List<ClubMembership> findByUser_IdAndDeletedAtIsNull(Long userId);
}
