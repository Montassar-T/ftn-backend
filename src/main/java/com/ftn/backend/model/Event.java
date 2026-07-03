package com.ftn.backend.model;

import com.ftn.backend.enums.CategorieEnum;
import com.ftn.backend.enums.EventStatusEnum;
import com.ftn.backend.enums.RoundTypeEnum;
import com.ftn.backend.enums.SexeEnum;
import com.ftn.backend.enums.SwimStyleEnum;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    @Enumerated(EnumType.STRING)
    @Column(name = "swim_style", length = 20)
    private SwimStyleEnum swimStyle;

    private Integer distance;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private SexeEnum gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "age_category", length = 50)
    private CategorieEnum ageCategory;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private RoundTypeEnum round;

    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private EventStatusEnum status = EventStatusEnum.SCHEDULED;
}
