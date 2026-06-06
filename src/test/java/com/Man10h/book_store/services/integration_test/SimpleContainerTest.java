package com.Man10h.book_store.services.integration_test;


import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;

public class SimpleContainerTest {

    @Test
    void test() {
        MySQLContainer<?> mysql =
                new MySQLContainer<>("mysql:8.0");

        mysql.start();

        System.out.println(mysql.getJdbcUrl());

        mysql.stop();
    }
}
