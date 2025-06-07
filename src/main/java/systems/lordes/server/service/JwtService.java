package systems.lordes.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import systems.lordes.server.config.FakeRestaurantProperties;
import systems.lordes.server.gen.api.User;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    private final SecretKey secretKey;
    private final Long jwtExpiration;
    private final ObjectMapper objectMapper;

    @Autowired
    public JwtService(
            FakeRestaurantProperties fakeRestaurantProperties,
            ObjectMapper objectMapper
    ) {
        this.secretKey = Keys.hmacShaKeyFor(fakeRestaurantProperties.getSecretJwtKey().getBytes(StandardCharsets.UTF_8));
        this.jwtExpiration = fakeRestaurantProperties.getJwtExpiration();
        this.objectMapper = objectMapper;
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plus(Duration.ofMillis(jwtExpiration));

        Map<String, Object> extraClaims = objectMapper.convertValue(user, Map.class);

        return Jwts.builder()
                .claims(extraClaims)
                .subject(user.getEmail())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(secretKey)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
