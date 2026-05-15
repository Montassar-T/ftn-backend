package com.ftn.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "sujets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sujet extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "forum_id", nullable = false)
    private Forum forum;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "auteur_id")
    private User auteur;

    @Column(nullable = false, length = 500)
    private String titre;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(nullable = false)
    @Builder.Default
    private Boolean epingle = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ferme = false;

    @Column(name = "nb_vues", nullable = false)
    @Builder.Default
    private Integer nbVues = 0;

    @Column(name = "nb_reponses", nullable = false)
    @Builder.Default
    private Integer nbReponses = 0;
}
