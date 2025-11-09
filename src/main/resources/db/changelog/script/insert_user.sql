--liquibase formatted sql
--changeset manh:10931

INSERT INTO user(username, password, enabled, role_id)
VALUES
    ('admin', '$2a$10$gGupYyjf96p/HiVOaZwozuSKqGfLfSu6DLr.NEqDZ7xqL7BzznbkS', 1, 2),
    ('user', '$2a$10$zh9pwh0N4rhjP9ppSWiRouEa/fXHQ0dEE6MAmbbn.F655L4IbKOIi', 1, 1)