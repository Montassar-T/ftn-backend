package com.ftn.backend.repository;

import com.ftn.backend.model.ClubStaff;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubStaffRepository extends JpaRepository<ClubStaff, Long> {

    List<ClubStaff> findByClub_IdAndDeletedAtIsNull(Long clubId);

    List<ClubStaff> findByDeletedAtIsNull();

    Optional<ClubStaff> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByClub_IdAndUser_IdAndDeletedAtIsNull(Long clubId, Long userId);
}
