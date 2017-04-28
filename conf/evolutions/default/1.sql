# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table user (
  id                            bigint not null,
  username                      varchar(255),
  password                      varchar(255),
  salt                          varchar(255),
  session_id                    varchar(255),
  google_secret_key             varchar(255),
  google_validation_code        integer,
  google_scratch_codes          clob,
  yubi_key_nonce                varchar(255),
  yubi_key_identity             varchar(255),
  constraint pk_user primary key (id)
);
create sequence user_seq;


# --- !Downs

drop table if exists user;
drop sequence if exists user_seq;

