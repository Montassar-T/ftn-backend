package com.ftn.backend.repository;

import com.ftn.backend.model.NationsRanking;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface NationsRankingRepository
        extends JpaRepository<NationsRanking, Long>, JpaSpecificationExecutor<NationsRanking> {
    Optional<NationsRanking> findByIdAndDeletedAtIsNull(Long id);

    List<NationsRanking> findBySeasonAndDeletedAtIsNull(String season);

    List<NationsRanking> findByAthlete_IdAndDeletedAtIsNull(Long athleteId);
}
