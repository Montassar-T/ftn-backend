package com.ftn.backend.model;

import com.ftn.backend.enums.ForumReactionTypeEnum;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "forum_reactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForumReaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thread_id")
    private Sujet thread;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Reponse post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private ForumReactionTypeEnum type;
}
