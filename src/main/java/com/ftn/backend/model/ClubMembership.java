package com.ftn.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;

@Entity
@Table(name = "club_memberships")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubMembership extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @Column(length = 50)
    private String role;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_current")
    @Builder.Default
    private Boolean isCurrent = true;
}
