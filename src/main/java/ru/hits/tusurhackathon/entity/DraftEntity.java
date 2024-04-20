package ru.hits.tusurhackathon.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import ru.hits.tusurhackathon.enumeration.DraftType;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "draft")
public class DraftEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "draft_type")
    private DraftType draftType;

    @ManyToOne
    @JoinColumn(name = "proposal_id")
    private ProposalEntity proposal;

    @Column(name = "saved_at")
    private LocalDateTime savedAt;

}
