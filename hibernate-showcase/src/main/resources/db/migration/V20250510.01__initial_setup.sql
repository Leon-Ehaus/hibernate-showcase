CREATE SCHEMA IF NOT EXISTS db;

CREATE TABLE IF NOT EXISTS db.users
(
    id   uuid primary key default gen_random_uuid(),
    name text
);

CREATE TABLE IF NOT EXISTS db.posts
(
    id        uuid primary key default gen_random_uuid(),
    author_id uuid references db.users (id) not null,
    title     text
);