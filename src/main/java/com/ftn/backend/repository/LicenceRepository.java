package com.ftn.backend.repository;

import com.ftn.backend.enums.StatutLicenceEnum;
import com.ftn.backend.model.Licence;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LicenceRepository
        extends JpaRepository<Licence, Long>, JpaSpecificationExecutor<Licence> {

    Optional<Licence> findByIdAndDeletedAtIsNull(Long id);

    List<Licence> findByAthlete_IdAndDeletedAtIsNull(Long athleteId);

    List<Licence> findByStatutAndDeletedAtIsNull(StatutLicenceEnum statut);

    List<Licence> findByClub_IdAndDeletedAtIsNull(Long clubId);
}
