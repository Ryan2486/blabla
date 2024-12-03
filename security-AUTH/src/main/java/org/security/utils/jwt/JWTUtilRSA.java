package org.security.utils.jwt;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JWTUtilRSA {
    private static final int EXPIRATION = 1000 * 60 * 60 * 10; // 10 hours
    private static final PrivateKey PRIVATE_KEY;
    private static final PublicKey PUBLIC_KEY;

    static {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PRIVATE_KEY = keyPair.getPrivate();
            PUBLIC_KEY = keyPair.getPublic();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error initializing RSA keys", e);
        }
    }

    public static String generateTokenWithKerberosTicket(String username, String ticket, Set<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("kerberosTicket", ticket);
        claims.put("roles", roles);
        System.out.println("token: " + createToken(claims, username));
        return createToken(claims, username);
    }

    private static String createToken(Map<String, Object> claims, String subject) {
        System.out.println(PRIVATE_KEY);
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SignatureAlgorithm.RS256, PRIVATE_KEY)
                .compact();
        System.out.println("Token: " + "helloword");
        return token;
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(PUBLIC_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token) {
        return (!isTokenExpired(token));
    }

    public static String getPublicKeyAsBase64() {
        return Base64.getEncoder().encodeToString(PUBLIC_KEY.getEncoded());
    }

    public static PublicKey getPublicKey() {
        return PUBLIC_KEY;
    }

}