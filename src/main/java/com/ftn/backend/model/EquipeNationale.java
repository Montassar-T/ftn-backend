package com.ftn.backend.model;

import com.ftn.backend.enums.CategorieEnum;
import com.ftn.backend.enums.DisciplineEnum;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "equipes_nationales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipeNationale extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private DisciplineEnum discipline;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private CategorieEnum categorie;

    @Column
    private Integer annee;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "equipe_nationale_athletes",
            joinColumns = @JoinColumn(name = "equipe_id"),
            inverseJoinColumns = @JoinColumn(name = "athlete_id"))
    @Builder.Default
    private List<Athlete> membres = new ArrayList<>();
}
