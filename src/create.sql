DROP TABLE IF EXISTS proposal;
DROP TABLE IF EXISTS project;
DROP TABLE IF EXISTS subcontractor;
DROP TABLE IF EXISTS gencontractor;
DROP TABLE IF EXISTS address;
DROP TABLE IF EXISTS option;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS template;

CREATE TABLE address (
    id uuid primary key,
    name varchar(100),
    phone varchar(12),
    street varchar(100),
    city varchar(100),
    created_at timestamp not null,
    updated_at timestamp not null,
    updated_by varchar(50)
);

CREATE TABLE gencontractor
(
    id    uuid primary key,
    email varchar(50),
    address_id uuid references address,
    created_at timestamp not null,
    updated_at timestamp not null,
    updated_by varchar(50)
);

CREATE TABLE subcontractor (
    id uuid primary key,
    email varchar(30),
    address_id uuid references address,
    created_at timestamp not null,
    updated_at timestamp not null,
    updated_by varchar(50)
);

CREATE TABLE project (
    id uuid primary key,
    title varchar(100),
    description varchar(200),
    budget numeric(10,2),
    gen_id uuid references gencontractor,
    created_at timestamp not null,
    updated_at timestamp not null,
    updated_by varchar(50)
);

CREATE TABLE proposal (
    id uuid primary key,
    description varchar(100),
    project_id uuid references project,
    sub_id uuid references subcontractor,
    created_at timestamp not null,
    updated_at timestamp not null,
    updated_by varchar(50)
);

CREATE TABLE template (
    id uuid primary key ,
    description varchar(100),
    name varchar(100),
    created_at timestamp not null,
    updated_at timestamp not null,
    updated_by varchar(50)
);

CREATE TABLE category (
    id uuid primary key ,
    description varchar(200),
    name varchar(100),
    type varchar(100),
    value varchar(100),
    tem_id uuid references template not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    updated_by varchar(50)
);

CREATE TABLE option (
    id uuid primary key,
    name varchar(100),
    description varchar(200),
    value varchar(1000),
    cat_id uuid references category not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    updated_by varchar(50)
);
