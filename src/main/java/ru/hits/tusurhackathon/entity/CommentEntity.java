package ru.hits.tusurhackathon.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "comment")
public class CommentEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    private String text;

    @ManyToOne
    @JoinColumn(name = "proposal_id", nullable = false)
    private ProposalEntity proposal;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
