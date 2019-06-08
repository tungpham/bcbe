DROP TABLE IF EXISTS projectfile;
DROP TABLE IF EXISTS contractorfile;
DROP TABLE IF EXISTS project_template;
DROP TABLE IF EXISTS project_specialty;
DROP TABLE IF EXISTS project_invite;
DROP TABLE IF EXISTS contractor_specialty;
DROP TABLE IF EXISTS proposal_option;
DROP TABLE IF EXISTS message;
DROP TABLE IF EXISTS proposal;
DROP TABLE IF EXISTS project;
DROP TABLE IF EXISTS contractor;
DROP TABLE IF EXISTS address;
DROP TABLE IF EXISTS option;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS template;
DROP TABLE IF EXISTS specialty;

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

CREATE TABLE contractor
(
    id uuid primary key,
    email varchar(50) not null,
    status varchar(10),
    status_reason varchar(300),
    address_id uuid references address,
    created_at timestamp not null,
    updated_at timestamp not null,
    updated_by varchar(50)
);

CREATE TABLE contractorfile (
    id uuid primary key,
    name varchar(100),
    con_id uuid references contractor,
    created_at timestamp not null,
    updated_at timestamp not null,
    updated_by varchar(50)
);

CREATE TABLE project (
    id uuid primary key,
    title varchar(100),
    description varchar(200),
    budget numeric(10,2),
    status varchar(10),
    gen_id uuid references contractor,
    created_at timestamp not null,
    updated_at timestamp not null,
    updated_by varchar(50)
);

CREATE TABLE projectfile (
    id uuid primary key,
    name varchar(100),
    proj_id uuid references project,
    created_at timestamp not null,
    updated_at timestamp not null,
    updated_by varchar(50)
);

CREATE TABLE proposal (
    id uuid primary key,
    description varchar(100),
    budget numeric(10,2),
    status varchar(10),
    duration numeric(10,2), 
    project_id uuid references project,
    sub_id uuid references contractor,
    unique(project_id, sub_id),
    created_at timestamp not null,
    updated_at timestamp not null,
    updated_by varchar(50)
);

CREATE TABLE proposalfile (
    id uuid primary key,
    name varchar(100),
    prop_id uuid references proposal,
    created_at timestamp not null,
    updated_at timestamp not null,
    updated_by varchar(50)
);

CREATE TABLE template (
    id uuid primary key ,
    description varchar(100),
    name varchar(100),
    unique(name),
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
    unique(name),
    created_at timestamp not null,
    updated_at timestamp not null,
    updated_by varchar(50)
);

CREATE TABLE option (
    id uuid primary key,
    name varchar(100),
    description varchar(200),
    value varchar(1000),
    budget numeric(10, 2),
    duration numeric(10,2),
    cat_id uuid references category not null,
    unique(name),
    created_at timestamp not null,
    updated_at timestamp not null,
    updated_by varchar(50)
);

CREATE TABLE project_template (
    id uuid primary key,
    proj_id uuid references project,
    tem_id uuid references template,
    unique(proj_id, tem_id),
    created_at timestamp not null,
    updated_at timestamp not null,
    updated_by varchar(50)
);

CREATE TABLE specialty (
    id uuid primary key,
    name varchar(50),
    description varchar(200),
    value varchar(50),
    unique(name),
    created_at timestamp not null,
    updated_at timestamp not null,
    updated_by varchar(50)
);

CREATE TABLE project_specialty (
    id uuid primary key,
    proj_id uuid references project,
    spec_id uuid references specialty,
    unique(proj_id, spec_id),
    created_at timestamp not null,
    updated_at timestamp not null,
    updated_by varchar(50)
);

CREATE TABLE contractor_specialty (
    id uuid primary key,
    con_id uuid references contractor,
    spec_id uuid references specialty,
    unique(con_id, spec_id),
    created_at timestamp not null,
    updated_at timestamp not null,
    updated_by varchar(50)
);

create table proposal_option (
    id uuid primary key,  
    prop_id uuid references proposal,
    cat_id uuid references category,
    name varchar(100),
    description varchar(200),
    value varchar(1000),
    budget numeric(10, 2),
    duration numeric(10,2),
    unique(name),
    created_at timestamp not null,
    updated_at timestamp not null,
    updated_by varchar(50)
);

CREATE TABLE project_invite (
    id uuid primary key,
    proj_id uuid references project,
    sub_id uuid references contractor,
    unique(proj_id, sub_id),
    created_at timestamp not null,
    updated_at timestamp not null,
    updated_by varchar(50)
);

create table message (
    id uuid primary key,  
    prop_id uuid references proposal,
    from_id uuid references contractor,
    to_id uuid references contractor,
    content varchar(500),
    status varchar(10),
    created_at timestamp not null,
    updated_at timestamp not null,
    updated_by varchar(50)
);

CREATE TABLE proposalmsgfile (
    id uuid primary key,
    name varchar(100),
    msg_id uuid references message,
    created_at timestamp not null,
    updated_at timestamp not null,
    updated_by varchar(50)
);
