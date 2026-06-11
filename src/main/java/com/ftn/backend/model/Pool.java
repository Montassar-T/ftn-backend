package com.ftn.backend.model;

import com.ftn.backend.enums.PoolTypeEnum;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pools")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pool extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String nom;

    @Column(length = 255)
    private String ville;

    @Column(length = 500)
    private String adresse;

    @Column
    private Integer longueur;

    @Column(name = "nb_couloirs")
    private Integer nbCouloirs;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private PoolTypeEnum type;

    @Column(nullable = false)
    @Builder.Default
    private Boolean actif = true;
}
