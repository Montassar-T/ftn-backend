package com.ftn.backend.model;

import com.ftn.backend.enums.CategorieEnum;
import com.ftn.backend.enums.DisciplineEnum;
import com.ftn.backend.enums.SexeEnum;
import com.ftn.backend.enums.StyleEnum;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "epreuves")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Epreuve extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    @Column(nullable = false)
    private String nom;

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
    private Integer distance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private StyleEnum style;

    @Column(name = "date_heure", nullable = false)
    private LocalDateTime dateHeure;
}
