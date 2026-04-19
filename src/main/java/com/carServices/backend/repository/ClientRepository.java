package com.carServices.backend.repository;

import com.carServices.backend.model.Client;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClientRepository extends JpaRepository<Client, Long>, JpaSpecificationExecutor<Client> {
    Optional<Client> findByIdAndDeletedAtIsNull(Long id);

    @Query(
            """
SELECT c FROM Client c
WHERE c.deletedAt IS NULL
AND (
	LOWER(c.name) LIKE LOWER(CONCAT(:q, '%'))
	OR c.phoneNumber LIKE CONCAT(:q, '%')
	OR LOWER(c.representative) LIKE LOWER(CONCAT(:q, '%'))
)
""")
    List<Client> search(@Param("q") String q);
}
