package org.example.jobapplicationtracker.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.example.jobapplicationtracker.model.User;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789SECRETKEY";

    private Key getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }


    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    public String extractUsername(String token) {
        try {
            return extractAllClaims(token).getSubject();
        } catch (JwtException e) {
            return null;
        }
    }

    public String extractRole(String token) {

        try {
            return extractAllClaims(token)
                    .get("role", String.class);

        } catch (JwtException e) {

            return "USER";
        }
    }

    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
