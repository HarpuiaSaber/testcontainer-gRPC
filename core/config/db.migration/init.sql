create table student
(
    id   bigserial
        constraint student_pk
            primary key,
    name varchar,
    age  integer not null,
    major_id integer not null,
    mentor_id bigint not null
);

alter table student
    owner to test;