package com.raf.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

@Value("${jwt.secret}")
private String secret;

@Value("${jwt.expiration}")
private Long expiration;

private SecretKey getSigningKey() {
return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
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

private Claims extractAllClaims(String token) {
return Jwts.parser()
.verifyWith(getSigningKey())
.build()
.parseSignedClaims(token)
.getPayload();
}

private Boolean isTokenExpired(String token) {
return extractExpiration(token).before(new Date());
}

public String generateToken(String username, Long userId, String userType) {
Map<String, Object> claims = new HashMap<>();
claims.put("userId", userId);
claims.put("userType", userType);
return createToken(claims, username);
}

private String createToken(Map<String, Object> claims, String subject) {
return Jwts.builder()
.claims(claims)
.subject(subject)
.issuedAt(new Date(System.currentTimeMillis()))
.expiration(new Date(System.currentTimeMillis() + expiration))
.signWith(getSigningKey())
.compact();
}

public Boolean validateToken(String token, String username) {
final String extractedUsername = extractUsername(token);
return (extractedUsername.equals(username) && !isTokenExpired(token));
}

public Long getUserIdFromToken(String token) {
try {
Claims claims = extractAllClaims(token);
Object userIdObj = claims.get("userId");

if (userIdObj == null) {
return null;
}


if (userIdObj instanceof Long) {
return (Long) userIdObj;
} else if (userIdObj instanceof Integer) {
return ((Integer) userIdObj).longValue();
} else if (userIdObj instanceof String) {

try {
return Long.parseLong((String) userIdObj);
} catch (NumberFormatException e) {

return null;
}
} else if (userIdObj instanceof Number) {
return ((Number) userIdObj).longValue();
}

return null;
} catch (Exception e) {

return null;
}
}

public String getUserTypeFromToken(String token) {
Claims claims = extractAllClaims(token);
return claims.get("userType", String.class);
}
}

