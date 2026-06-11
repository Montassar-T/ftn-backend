package com.ftn.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "competitions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Competition extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 50)
    private String type;

    @Column(name = "start_date", nullable = true)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = true)
    private LocalDate endDate;

    @Column(name = "registration_deadline")
    private LocalDateTime registrationDeadline;

    @Column(length = 50)
    private String code;

    @Column(name = "pool_id")
    private Long poolId;

    @Column(length = 10)
    private String lane;

    @Column(name = "age_categories", length = 200)
    private String ageCategories;

    @Column(name = "source_url", columnDefinition = "TEXT")
    private String sourceUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String status = "PLANIFIEE";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evenement_id")
    private Evenement evenement;
}
