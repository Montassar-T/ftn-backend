package com.ftn.backend.model;

import com.ftn.backend.enums.CategorieActuEnum;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "actualites")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Actualite extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "auteur_id")
    private User auteur;

    @Column(nullable = false, length = 500)
    private String titre;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private CategorieActuEnum categorie;

    @Column(name = "date_publication")
    private LocalDateTime datePublication;

    @Column(nullable = false)
    @Builder.Default
    private Boolean publie = false;

    @Column(name = "image_url", length = 500)
    private String imageUrl;
}
