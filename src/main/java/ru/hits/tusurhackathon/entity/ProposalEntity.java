package ru.hits.tusurhackathon.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import ru.hits.tusurhackathon.enumeration.ProposalStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "proposal")
public class ProposalEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    private String text;

    @Column(name = "jira_link")
    private String jiraLink;

    @Column(name = "votes_for")
    private Integer votesFor = 0; // Количество голосов ЗА предложение

    @Column(name = "votes_against")
    private Integer votesAgainst = 0; // Количество голосов ПРОТИВ предложение

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "proposal_status")
    private ProposalStatus proposalStatus;

    @OneToMany(mappedBy = "proposal")
    private List<CommentEntity> comments;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private ProjectEntity project;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
