package com.ftn.backend.model;

import com.ftn.backend.enums.PosteStaffEnum;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "competition_staff", uniqueConstraints = @UniqueConstraint(columnNames = {"competition_id", "user_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompetitionStaff extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PosteStaffEnum poste;
}
