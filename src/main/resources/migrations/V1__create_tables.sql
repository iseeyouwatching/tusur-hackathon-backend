create table public._user
(
    id            uuid not null
        primary key,
    avatar_number integer,
    first_name    varchar(255),
    last_name     varchar(255),
    middle_name   varchar(255),
    phone         varchar(255)
);

alter table public._user
    owner to postgres;

create table public.blacklist_token
(
    id    uuid not null
        primary key,
    token varchar(255)
);

alter table public.blacklist_token
    owner to postgres;

create table public.project
(
    id                        uuid not null
        primary key,
    created_at                timestamp,
    description               varchar(255),
    name                      varchar(255),
    schedule                  varchar(255),
    votes_per_period          integer,
    votes_refresh_period_days integer
);

alter table public.project
    owner to postgres;

create table public.project_user
(
    id                uuid not null
        primary key,
    is_active_project boolean,
    is_admin          boolean,
    number_of_votes   integer,
    project_id        uuid not null
        constraint fk4ug72llnm0n7yafwntgdswl3y
            references public.project,
    user_id           uuid not null
        constraint fk59o2u5jcij6lcrb9qvi0ede8u
            references public._user
);

alter table public.project_user
    owner to postgres;

create table public.proposal
(
    id              uuid not null
        primary key,
    created_at      timestamp,
    jira_link       varchar(255),
    proposal_status integer,
    text            varchar(255),
    votes_against   integer,
    votes_for       integer,
    project_id      uuid
        constraint fktebwryejikcsw6h29slrt5otk
            references public.project,
    user_id         uuid
        constraint fk3qn4a2nagsqytt3st8hyknd1u
            references public._user
);

alter table public.proposal
    owner to postgres;

create table public.comment
(
    id                uuid not null
        primary key,
    created_at        timestamp,
    text              varchar(255),
    parent_comment_id uuid
        constraint fkhvh0e2ybgg16bpu229a5teje7
            references public.comment,
    proposal_id       uuid not null
        constraint fkgd5c792vmenisiw5os6nflkp
            references public.proposal,
    user_id           uuid
        constraint fkoo5phijy22unidgkw0sipcs74
            references public._user
);

alter table public.comment
    owner to postgres;

create table public.draft
(
    id          uuid not null
        primary key,
    content     varchar(255),
    draft_type  integer,
    jira_link   varchar(255),
    saved_at    timestamp,
    comment_id  uuid
        constraint fkna5qhcpxq3imv1ba3xsl0rhif
            references public.comment,
    project_id  uuid
        constraint fk7ht9tpbhomv62gkm9e58vs7uv
            references public.project,
    proposal_id uuid
        constraint fk4bwafw64o10bavvrt0gwx8xj4
            references public.proposal,
    user_id     uuid
        constraint fkaaxaby5ktyjpb1fjlfmhoxiv9
            references public._user
);

alter table public.draft
    owner to postgres;

create table public.user_project
(
    project_id uuid not null
        constraint fkocfkr6u2yh3w1qmybs8vxuv1c
            references public.project,
    user_id    uuid not null
        constraint fk7ee95yjmll0yj5d6h36ahy7bp
            references public._user
);

alter table public.user_project
    owner to postgres;

create table public.user_vote
(
    id                    uuid not null
        primary key,
    can_be_voice_canceled boolean,
    is_upvote             boolean,
    proposal_id           uuid
        constraint fk1skpevsu6jidm37n4whovppi3
            references public.proposal,
    user_id               uuid
        constraint fkqb2vcd6x71979kyow0u6kkufp
            references public._user
);

alter table public.user_vote
    owner to postgres;

