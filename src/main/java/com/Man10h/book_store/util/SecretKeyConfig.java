package com.Man10h.book_store.util;

import com.nimbusds.jose.jwk.OctetSequenceKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;

@Configuration
public class SecretKeyConfig {

    @Value("${jwt.secret}")
    private String secret;

    @Bean
    public SecretKey getSecretKey() {
        byte[] decodedKey = Base64.getDecoder().decode(secret);
        return new OctetSequenceKey.Builder(decodedKey).build().toSecretKey();
    }
}
