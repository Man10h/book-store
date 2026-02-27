--liquibase formatted sql
--changeset manh:1213

CREATE TABLE role (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255),
    password VARCHAR(255),
    email VARCHAR(255),
    enabled BOOLEAN,
    verification_code VARCHAR(255),
    verification_code_expiration DATETIME,
    role_id BIGINT,
    CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES role(id)
);


CREATE TABLE book (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255),
    author VARCHAR(255),
    type VARCHAR(255),
    description TEXT,
    price DOUBLE
);


CREATE TABLE image (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    url VARCHAR(500),
    book_id BIGINT,
    CONSTRAINT fk_image_book FOREIGN KEY (book_id) REFERENCES book(id)
);


CREATE TABLE cart (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT UNIQUE,
    CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    quantity BIGINT,
    status VARCHAR(255),
    book_id BIGINT,
    cart_id BIGINT,
    CONSTRAINT fk_item_book FOREIGN KEY (book_id) REFERENCES book(id),
    CONSTRAINT fk_item_cart FOREIGN KEY (cart_id) REFERENCES cart(id)
);

CREATE INDEX idx_user_username ON user(username);
CREATE INDEX idx_user_email ON user(email);
CREATE INDEX idx_user_role ON user(role_id);
CREATE INDEX idx_image_book ON image(book_id);
CREATE INDEX idx_cart_user ON cart(user_id);
CREATE INDEX idx_item_book ON item(book_id);
CREATE INDEX idx_item_cart ON item(cart_id);