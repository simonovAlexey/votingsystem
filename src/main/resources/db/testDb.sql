DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS vote;
DROP TABLE IF EXISTS menu;
DROP TABLE IF EXISTS dish;
DROP TABLE IF EXISTS restaurant;
DROP TABLE IF EXISTS users;

create table users
(
    id         integer auto_increment primary key,
    name       varchar(255)            not null,
    email      varchar(255)            not null,
    enabled    bool      default true  not null,
    password   varchar(255)            not null,
    registered timestamp default now() not null,

    constraint uk_email unique (email)
);

create table user_roles
(
    user_id integer not null,
    role    varchar(255),

    constraint uk_user_roles unique (user_id, role),
    constraint fk_user_roles foreign key (user_id) references users (id) on delete cascade
);

create table restaurant
(
    id   integer auto_increment primary key,
    name varchar(255) not null,

    constraint uk_restaurant unique (name)
);

create table dish
(
    id            integer auto_increment primary key,
    name          varchar(255) not null,
    date          date         not null,
    price         integer      not null,
    restaurant_id integer      not null,

    constraint uk_dish unique (name, restaurant_id, date),
    constraint fk_dish foreign key (restaurant_id) references restaurant (id) on delete cascade
);

create table vote
(
    id            integer auto_increment primary key,
    date          date    not null,
    restaurant_id integer not null,
    user_id       integer not null,
    primary key (id),
    constraint uk_vote unique (user_id, date),
    constraint fk_vote_user foreign key (user_id) references users (id) on delete cascade,
    constraint fk_vote_restaurant foreign key (restaurant_id) references restaurant (id) on delete cascade
);

-- test data
INSERT INTO USERS (name, email, password)
VALUES ('Admin', 'admin@gmail.com', '{noop}admin'),
       ('User', 'user@yandex.ru', '{noop}password');

INSERT INTO USER_ROLES (role, user_id)
VALUES ('USER', 1),
       ('ADMIN', 1),
       ('USER', 2);

INSERT INTO RESTAURANT (name)
VALUES ('1чебуречная'),
       ('2рюмочная'),
       ('3блинная');

INSERT INTO DISH (restaurant_id, name, price, date)
VALUES (1, 'беляш', 1, CURRENT_DATE - 1),
       (1, 'булочка', 2, CURRENT_DATE - 1),
       (1, 'кефир', 3, CURRENT_DATE - 1),
       (1, 'чебурек', 4, CURRENT_DATE - 1),

       (2, 'самогон', 1, CURRENT_DATE - 1),
       (2, 'виски', 2, CURRENT_DATE - 1),
       (2, 'ром', 3, CURRENT_DATE - 1),
       (2, 'коньяк', 4, CURRENT_DATE - 1),

       (3, 'с творогом', 1, CURRENT_DATE - 1),
       (3, 'с яблоком', 2, CURRENT_DATE - 1),
       (3, 'с мясом', 3, CURRENT_DATE - 1),
       (3, 'с овощами', 4, CURRENT_DATE - 1),

       (1, 'беляш2', 5, CURRENT_DATE),
       (1, 'булочка2', 6, CURRENT_DATE),
       (1, 'кефир2', 7, CURRENT_DATE),
       (1, 'чебурек2', 8, CURRENT_DATE),

       (2, 'самогон2', 5, CURRENT_DATE),
       (2, 'виски2', 6, CURRENT_DATE),
       (2, 'ром2', 7, CURRENT_DATE),
       (2, 'коньяк2', 8, CURRENT_DATE),

       (3, 'с творогом2', 5, CURRENT_DATE),
       (3, 'с яблоком2', 6, CURRENT_DATE),
       (3, 'с мясом2', 7, CURRENT_DATE),
       (3, 'с овощами2', 8, CURRENT_DATE);

INSERT INTO VOTE (user_id, date, restaurant_id)
VALUES (1, CURRENT_DATE - 1, 2),
       (2, CURRENT_DATE - 1, 1),
       (2, CURRENT_DATE, 3);
