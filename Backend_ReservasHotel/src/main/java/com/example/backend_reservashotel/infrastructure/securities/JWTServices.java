package com.example.backend_reservashotel.infrastructure.securities;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

import javax.crypto.SecretKey;

@Service
public class JWTServices {

    private final SecretKey claveSecreta;
    private final long expiration;

    public JWTServices(@Value("${jwt.secret}") String claveSecreta,
                       @Value("${jwt.expiration}") long expiration) {
        this.claveSecreta = Keys.hmacShaKeyFor(Decoders.BASE64.decode(claveSecreta));
        this.expiration = expiration;
    }

    public String generar(String username, String rol){
        Date actual = new Date();
        return Jwts.builder()
            .claim("rol", rol)
            .subject(username)
            .issuedAt(actual)
            .expiration(new Date(actual.getTime() + expiration))
            .signWith(claveSecreta)
            .compact();
    }
    public Claims leer(String token){
        return Jwts.parser()
            .verifyWith(claveSecreta)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
