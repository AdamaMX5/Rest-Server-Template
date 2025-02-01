package de.freeschool.api.util;

import de.freeschool.api.ApiTestBase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PasswordEncryptionTest extends ApiTestBase {

    @Test
    void encryptDecrypt() {
        String data = "this is my data";
        String password = "my secret password!!";
        String salt = RandomNumberGenerator.generateRandomString(16);
        String encrypted = PasswordEncryption.encrypt(data, password, salt);
        String decrypted = PasswordEncryption.decrypt(encrypted, password, salt);
        Assertions.assertThat(data).isEqualTo(decrypted);
        Assertions.assertThat(data).isNotEqualTo(encrypted);
    }

    @Test
    void encryptDecryptBadPassword() {
        String data = "this is my data";
        String password = "my secret password!!";
        String salt = "123";
        String encrypted = PasswordEncryption.encrypt(data, password, salt);
        assertThrows(PasswordEncryption.BadPasswordException.class,
                () -> PasswordEncryption.decrypt(encrypted, "wrong password", salt)
        );
    }

    @Test
    void ensureStrongPassword() throws PasswordEncryption.WeakPasswordException {
        PasswordEncryption.ensureStrongPassword("abcdABCD1.");

        List<String> passwords = Arrays.asList("", "123", "abcdefghijklmnop", "abcdefgABCDEFG", "...,,,...,,,.,.,.,",
                "asdf ASDF 5.353"
        );

        for (String password : passwords) {
            assertThrows(PasswordEncryption.WeakPasswordException.class,
                    () -> PasswordEncryption.ensureStrongPassword(password)
            );
        }
    }
}