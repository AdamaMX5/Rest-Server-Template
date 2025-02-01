package de.freeschool.api.security;

import de.freeschool.api.ApiTestBase;
import de.freeschool.api.GeneralTestSetup;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@GeneralTestSetup
class JwtTokenBlacklistTest extends ApiTestBase {

    @Autowired
    JwtTokenBlacklist jwtTokenBlacklist;

    @Test
    void tokenBlacklistLifecycle() throws InterruptedException {
        // check empty
        Assertions.assertThat(jwtTokenBlacklist.isBlacklisted("Blubb")).isFalse();

        jwtTokenBlacklist.addToBlacklist("token1", 1);

        // added to blacklist, but still valid
        Thread.sleep(100);
        Assertions.assertThat(jwtTokenBlacklist.isBlacklisted("token1")).isFalse();

        // invalid after 1 s
        Thread.sleep(1000);
        Assertions.assertThat(jwtTokenBlacklist.isBlacklisted("token1")).isTrue();

        // new token, invalid immediately
        jwtTokenBlacklist.addToBlacklist("token2", 0);
        Assertions.assertThat(jwtTokenBlacklist.isBlacklisted("token2")).isTrue();
    }
}