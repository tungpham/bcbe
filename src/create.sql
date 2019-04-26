DROP TABLE address;
CREATE TABLE address (
    id uuid primary key,
    name varchar(100),
    phone varchar(12),
    street varchar(100),
    city varchar(100)
);

DROP TABLE gencontractor;
CREATE TABLE gencontractor (
    id uuid primary key,
    address_id uuid references address
);

DROP TABLE subcontractor;
CREATE TABLE subcontractor (
    id uuid primary key,
    address_id uuid references address
);

DROP TABLE project;
CREATE TABLE project (
    id uuid primary key,
    title varchar(100),
    description varchar(200),
    budget numeric(10,2),
    gen_id uuid references gencontractor
);

DROP TABLE proposal;
CREATE TABLE propsal (
    id uuid primary key,
    description varchar(100),
    project_id uuid references project,
    sub_id uuid references subcontractor
);

