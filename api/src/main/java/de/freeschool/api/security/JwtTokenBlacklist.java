package de.freeschool.api.security;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class JwtTokenBlacklist {

    @Getter
    private static class TokenInvalidator {
        private final long invalidatedWhen;

        public TokenInvalidator(long secondsUntilInvalid) {
            invalidatedWhen = System.currentTimeMillis() + 1000 * secondsUntilInvalid;
        }

        public boolean isInvalid() {
            return System.currentTimeMillis() >= invalidatedWhen;
        }

    }

    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;
    private long lastGarbageCollection = System.currentTimeMillis();

    private final long garbageCollectionInterval = 600;
    private final Map<String, TokenInvalidator> tokenBlacklist = new HashMap<>();

    public void addToBlacklist(String token, int secondsUntilInvalid) {
        tokenBlacklist.put(token, new TokenInvalidator(secondsUntilInvalid));
        collectGarbage();
    }

    private void collectGarbage() {
        if (System.currentTimeMillis() - lastGarbageCollection > 1000 * garbageCollectionInterval) {
            Set<String> toRemove = new HashSet<>();
            tokenBlacklist.forEach((token, tokenInvalidator) -> {
                if (System.currentTimeMillis() - tokenInvalidator.getInvalidatedWhen() > jwtExpirationInMs) {
                    toRemove.add(token);
                }
            });
            toRemove.forEach(tokenBlacklist::remove);
            lastGarbageCollection = System.currentTimeMillis();
        }
    }

    public boolean isBlacklisted(String token) {
        TokenInvalidator tokenInvalidator = tokenBlacklist.get(token);
        return tokenInvalidator != null && tokenInvalidator.isInvalid();
    }
}