package com.ftn.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "reponses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reponse extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sujet_id", nullable = false)
    private Sujet sujet;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "auteur_id")
    private User auteur;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "nb_likes", nullable = false)
    @Builder.Default
    private Integer nbLikes = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean signale = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_reponse_id")
    private Reponse parentReponse;

    @Column(name = "image_url", length = 500)
    private String imageUrl;
}
