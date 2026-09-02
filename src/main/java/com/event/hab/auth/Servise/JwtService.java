package com.event.hab.auth.Servise;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    @Value("${app.jwt.secret}")
    private String secret;
    @Value("${app.jwt.expiration-ms}")
    private Long expirationMs;

    private Key getKey(){
        return Keys.hmacShaKeyFor(secret.getBytes());
    }


    public String generateToken(UserDetails userDetails){
       String email =  userDetails.getUsername();
       String role = userDetails.getAuthorities().toString();
        // дописать role с ROLE_/// на ///
       Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
       return Jwts.builder()
               .setClaims(claims)
               .setSubject(email)
               .setIssuedAt(new Date())
               .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
               .signWith(getKey(), SignatureAlgorithm.HS256)
               .compact();
    }

    public String extractEmail(String token){
        return  Jwts.parser()
                .verifyWith((SecretKey)getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();

    }

    public String extractRole(String token){
        return  Jwts.parser()
                .verifyWith((SecretKey)getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);

    }

    public Date extractExpiration(String token){
        return  Jwts.parser()
                .verifyWith((SecretKey)getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }

    public boolean validateToken(String token, UserDetails userDetails){
        try {
            return extractEmail(token).equals(userDetails.getUsername()) &&
                    extractExpiration(token).before(new Date());
        }catch (IllegalArgumentException e){
            return false;
        }

    }

}
