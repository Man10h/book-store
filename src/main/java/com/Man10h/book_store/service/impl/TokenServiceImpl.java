package com.Man10h.book_store.service.impl;

import com.Man10h.book_store.exception.exception.ErrorException;
import com.Man10h.book_store.model.entity.UserEntity;
import com.Man10h.book_store.repository.UserRepository;
import com.Man10h.book_store.service.TokenService;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final SecretKey secretKey;
    private final UserRepository userRepository;

    @Override
    public String generateToken(UserEntity userEntity) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        JWTClaimsSet claim = new JWTClaimsSet.Builder()
                .subject(userEntity.getUsername())
                .expirationTime(new Date(new Date().getTime() + 60 * 60 * 1000))
                .claim("roles", Collections.singletonList("ROLE_" + userEntity.getRoleEntity().getName()))
                .build();
        SignedJWT signedJWT = new SignedJWT(header, claim);
        try{
            JWSSigner signer = new MACSigner(secretKey);
            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (Exception e) {
            throw new ErrorException(e.getMessage());
        }
    }

    @Override
    public boolean validateToken(String token) {
        try{
            JWSVerifier verifier = new MACVerifier(secretKey);
            SignedJWT signedJWT = SignedJWT.parse(token);
            boolean x = signedJWT.verify(verifier) && new Date().before(signedJWT.getJWTClaimsSet().getExpirationTime());
            return signedJWT.verify(verifier) && new Date().before(signedJWT.getJWTClaimsSet().getExpirationTime());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getUsername(String token) {
        try{
            if(validateToken(token)){
                SignedJWT signedJWT = SignedJWT.parse(token);
                return signedJWT.getJWTClaimsSet().getSubject();
            }
            throw new ErrorException("Invalid token");

        } catch (Exception e) {
            throw new ErrorException(e.getMessage());
        }
    }


}
