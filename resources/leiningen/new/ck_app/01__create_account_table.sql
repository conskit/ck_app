create table account (
  account_id int primary key auto_increment not null,
  firstname  varchar(200)                   not null,
  lastname   varchar(200)                   not null,
  email      varchar(200)                   not null
);

insert into account (firstname, lastname, email) values ('Conskit', 'Framework', 'conskit@framework.com');