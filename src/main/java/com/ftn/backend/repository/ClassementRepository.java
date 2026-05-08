package com.ftn.backend.repository;

import com.ftn.backend.enums.CategorieEnum;
import com.ftn.backend.enums.DisciplineEnum;
import com.ftn.backend.enums.SexeEnum;
import com.ftn.backend.model.Classement;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ClassementRepository extends JpaRepository<Classement, Long>, JpaSpecificationExecutor<Classement> {

    Optional<Classement> findByIdAndDeletedAtIsNull(Long id);

    Optional<Classement> findByAthlete_IdAndDisciplineAndCategorieAndSexeAndAnneeAndDeletedAtIsNull(
            Long athleteId, DisciplineEnum discipline, CategorieEnum categorie, SexeEnum sexe, Integer annee);

    List<Classement> findByAthlete_IdAndDeletedAtIsNull(Long athleteId);

    List<Classement> findByDisciplineAndCategorieAndSexeAndAnneeAndDeletedAtIsNullOrderByRangAsc(
            DisciplineEnum discipline, CategorieEnum categorie, SexeEnum sexe, Integer annee);
}
