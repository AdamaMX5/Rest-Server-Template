package de.freeschool.api.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import de.freeschool.api.models.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JwtGenerator {
    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    @Autowired
    private JwtTokenBlacklist tokenBlacklist;

    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return jwtCreate(userDetails.getUsername());
    }

    public String generateToken(UserEntity user) {
        return jwtCreate(user.getEmailOrUsername());
    }

    private String jwtCreate(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return JWT.create()
                .withIssuer(username)
                .withSubject("my_subject")
                .withExpiresAt(expiryDate)
                .withClaim("username", username)
                .sign(Algorithm.HMAC512(jwtSecret));
    }

    public boolean validateToken(String token) {

        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC512(jwtSecret)).build();

            DecodedJWT jwt = verifier.verify(token);

            Date expiresAt = jwt.getExpiresAt();
            if (expiresAt.before(new Date())) {
                return false;
            }
            // is token blacklisted?
            if (tokenBlacklist.isBlacklisted(token)) {
                return false;
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }


    public String getUsernameFromToken(String token) {
        DecodedJWT jwt = JWT.decode(token);
        Map<String, Claim> claims = jwt.getClaims();
        return claims.get("username").asString();
    }
}
