create table users
(
    id               uuid                        not null
        primary key,
    create_date_time timestamp(6) with time zone not null,
    login            varchar(255)                not null,
    name             varchar(255)                not null,
    password         varchar(255)                not null
);

alter table users
    owner to postgres;

create table category
(
    id               uuid                        not null
        primary key,
    category_name    varchar(255)                not null,
    create_date_time timestamp(6) with time zone not null,
    creator_id       uuid                        not null,
    update_date_time timestamp(6) with time zone,
    parent_id        uuid
        constraint fk2y94svpmqttx80mshyny85wqr
            references category
);

alter table category
    owner to postgres;

create table theme
(
    id               uuid                        not null
        primary key,
    category_id      uuid                        not null,
    create_date_time timestamp(6) with time zone not null,
    creator_id       uuid                        not null,
    theme_name       varchar(255)                not null,
    update_date_time timestamp(6) with time zone
);

alter table theme
    owner to postgres;

create table message
(
    id               uuid                        not null
        primary key,
    create_date_time timestamp(6) with time zone not null,
    creator_id       uuid                        not null,
    text             varchar(255)                not null,
    theme_id         uuid                        not null,
    update_date_time timestamp(6) with time zone
);

alter table message
    owner to postgres;

