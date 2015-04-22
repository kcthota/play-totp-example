# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table BackupCodes (
  user_id                   varchar(40) not null,
  code1                     integer,
  code2                     integer,
  code3                     integer,
  code4                     integer,
  code5                     integer,
  constraint pk_BackupCodes primary key (user_id))
;

create table User (
  id                        varchar(40) not null,
  email                     varchar(255),
  password                  varchar(255),
  salt                      varchar(255),
  totp_enabled              boolean,
  totp_key                  varchar(255),
  constraint uq_User_email unique (email),
  constraint pk_User primary key (id))
;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists BackupCodes;

drop table if exists User;

SET REFERENTIAL_INTEGRITY TRUE;

