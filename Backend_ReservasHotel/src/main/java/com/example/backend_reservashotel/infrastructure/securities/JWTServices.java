package com.example.backend_reservashotel.infrastructure.securities;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

// Genera y lee los tokens JWT (firmados con HMAC-SHA usando jwt.secret)
@Service
public class JWTServices {
    public static final String CLAIM_ROL = "rol";

    private final SecretKey claveSecreta;
    private final long expiracion;

    public JWTServices(@Value("${jwt.secret}") String claveSecreta,
                       @Value("${jwt.expiration}") long expiracion) {
        // La clave en Base64 debe tener al menos 256 bits (32 bytes)
        this.claveSecreta = Keys.hmacShaKeyFor(Decoders.BASE64.decode(claveSecreta));
        this.expiracion = expiracion;
    }

    public String generar(String username, String rol) {
        Date ahora = new Date();
        return Jwts.builder()
                .subject(username)
                .claim(CLAIM_ROL, rol)
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + expiracion))
                .signWith(claveSecreta)
                .compact();
    }

    // Lanza JwtException si el token está alterado, mal formado o expirado
    public Claims leer(String token) {
        return Jwts.parser()
                .verifyWith(claveSecreta)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getExpiracion() {
        return expiracion;
    }
}
