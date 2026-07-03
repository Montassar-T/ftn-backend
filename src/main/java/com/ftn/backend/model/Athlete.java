package com.ftn.backend.model;

import com.ftn.backend.enums.CategorieEnum;
import com.ftn.backend.enums.SexeEnum;

import com.ftn.backend.enums.AthleteCategory;
import com.ftn.backend.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import lombok.*;

@Entity
@Table(name = "athletes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Athlete extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 150)
    private String nom;

    @Column(length = 150)
    private String prenom;

    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private Club club;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Column(length = 100)
    private String nationalite;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private CategorieEnum categorie;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private SexeEnum sexe;

    @Column(length = 255)
    private String email;

    @Column(length = 50)
    private String telephone;
}
