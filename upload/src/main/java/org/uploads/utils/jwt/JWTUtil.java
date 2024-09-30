package org.uploads.utils.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

import java.util.function.Function;

@Component
public class JWTUtil {

    private final PublicKey publicKey;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String authServiceUrl = "http://localhost:8081/login/public_key"; // URL of the Auth Service

    public JWTUtil() throws Exception {
        String publicKeyString = restTemplate.getForObject(authServiceUrl, String.class);
        System.out.println(publicKeyString);
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        this.publicKey = keyFactory.generatePublic(keySpec);
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token).getBody();
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }


    public Boolean validateToken(String token) {
        return (!isTokenExpired(token));
    }

    private Claims extractClaims(String token) {
        return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token).getBody();
    }
}

