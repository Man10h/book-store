--liquibase formatted sql
--changeset manh:11301

CREATE TABLE order_entity (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,

                              total_price DOUBLE,

                              status VARCHAR(255),

                              user_id BIGINT,

                              CONSTRAINT fk_order_user
                                  FOREIGN KEY (user_id)
                                      REFERENCES user(id)
);

CREATE TABLE payment (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,

                         amount DOUBLE,

                         status VARCHAR(255),

                         provider VARCHAR(255),

                         transaction_id VARCHAR(255),

                         payment_url VARCHAR(1000),

                         txn_ref VARCHAR(255),

                         created_at DATETIME,

                         paid_at DATETIME,

                         order_id BIGINT UNIQUE,

                         CONSTRAINT fk_payment_order
                             FOREIGN KEY (order_id)
                                 REFERENCES order_entity(id)
);

CREATE TABLE order_item (
                            id BIGINT PRIMARY KEY AUTO_INCREMENT,

                            quantity BIGINT,

                            price DOUBLE,

                            book_id BIGINT,

                            order_id BIGINT,

                            CONSTRAINT fk_order_item_book
                                FOREIGN KEY (book_id)
                                    REFERENCES book(id),

                            CONSTRAINT fk_order_item_order
                                FOREIGN KEY (order_id)
                                    REFERENCES order_entity(id)
);

CREATE INDEX idx_order_user
    ON order_entity(user_id);

CREATE INDEX idx_order_status
    ON order_entity(status);

CREATE INDEX idx_order_item_book
    ON order_item(book_id);

CREATE INDEX idx_order_item_order
    ON order_item(order_id);

CREATE INDEX idx_payment_order
    ON payment(order_id);

CREATE INDEX idx_payment_status
    ON payment(status);

CREATE INDEX idx_payment_txn_ref
    ON payment(txn_ref);

CREATE INDEX idx_payment_transaction_id
    ON payment(transaction_id);