--liquibase formatted sql
--changeset manh:12131

INSERT INTO role(id, name) VALUES (1, 'USER'), (2, 'ADMIN');