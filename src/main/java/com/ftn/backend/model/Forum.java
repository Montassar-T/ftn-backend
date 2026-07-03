package com.ftn.backend.model;

import com.ftn.backend.enums.CategorieForumEnum;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "forums")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Forum extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private CategorieForumEnum categorie;

    @Column(name = "nb_sujets", nullable = false)
    @Builder.Default
    private Integer nbSujets = 0;

    @Column(length = 255)
    private String slug;

    @Column(name = "order_index")
    @Builder.Default
    private Integer orderIndex = 0;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
}
