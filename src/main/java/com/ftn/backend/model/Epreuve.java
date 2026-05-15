package com.ftn.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;
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

    @Column(name = "swim_style", nullable = false, length = 50)
    private String swimStyle;

    @Column(nullable = false, length = 100)
    private String distance;

    @Column(nullable = false, length = 10)
    private String gender;

    @Column(length = 50)
    private String round;

    @Column(name = "scheduled_date", nullable = false)
    private LocalDate scheduledDate;

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String status = "PLANIFIEE";
}
