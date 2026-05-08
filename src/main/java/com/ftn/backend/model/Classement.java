package com.ftn.backend.model;

import com.ftn.backend.enums.CategorieEnum;
import com.ftn.backend.enums.DisciplineEnum;
import com.ftn.backend.enums.SexeEnum;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "classements",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_classement_scope",
                    columnNames = {"athlete_id", "discipline", "categorie", "sexe", "annee"})
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Classement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "athlete_id", nullable = false)
    private Athlete athlete;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private DisciplineEnum discipline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private CategorieEnum categorie;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private SexeEnum sexe;

    @Column(nullable = false)
    private Integer annee;

    @Column(name = "points_total", nullable = false)
    @Builder.Default
    private Integer pointsTotal = 0;

    @Column(nullable = false)
    private Integer rang;
}
