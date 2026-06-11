package com.ftn.backend.model;

import com.ftn.backend.enums.RecordTypeEnum;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;

@Entity
@Table(name = "records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Record extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Upstream fields ---

    @Column(nullable = false, length = 50)
    private String type;

    @Column(name = "swim_style", nullable = false, length = 50)
    private String swimStyle;

    @Column(nullable = false, length = 100)
    private String distance;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "athlete_id", nullable = false)
    private Athlete athlete;

    @Column(nullable = false)
    private Integer time;

    @Column(nullable = false)
    private LocalDate date;

    // --- HEAD fields ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id")
    private Competition competition;

    @Column(name = "temps_ms")
    private Long tempsMs;

    @Column(name = "temps_display", length = 20)
    private String tempsDisplay;

    @Column(name = "record_date")
    private LocalDate recordDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "record_type", length = 20)
    private RecordTypeEnum recordType;
}
