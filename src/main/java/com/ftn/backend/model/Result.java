package com.ftn.backend.model;

import com.ftn.backend.enums.ResultStatutEnum;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Result extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "athlete_id")
    private Athlete athlete;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id")
    private Competition competition;

    @Column(length = 255)
    private String epreuve;

    @Column(length = 50)
    private String temps;

    @Column
    private Integer rang;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    @Builder.Default
    private ResultStatutEnum statut = ResultStatutEnum.EN_ATTENTE;
}
