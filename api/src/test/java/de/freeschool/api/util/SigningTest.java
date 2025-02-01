package de.freeschool.api.util;

import de.freeschool.api.ApiTestBase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SigningTest extends ApiTestBase {

    @Test
    void newKeyPair() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchProviderException {
        String salt = RandomNumberGenerator.generateRandomString(16);
        Signing.EncryptedKeyPair keyPair1 = Signing.newKeyPair("abc", salt);
        Signing.EncryptedKeyPair keyPair2 = Signing.newKeyPair("abc", salt);

        Assertions.assertThat(keyPair1.getPublicKey()).isNotEqualTo(keyPair2.getPublicKey());
        Assertions.assertThat(keyPair1.getEncryptedPrivateKey()).isNotEqualTo(keyPair2.getEncryptedPrivateKey());

        byte[] publicKey1Bytes = Base64.getDecoder().decode(keyPair1.getPublicKey());
        Assertions.assertThat(publicKey1Bytes).hasSize(88);

        byte[] encryptedPrivateKey1Bytes = Base64.getDecoder().decode(keyPair1.getEncryptedPrivateKey());
        Assertions.assertThat(encryptedPrivateKey1Bytes).hasSize(208);
    }

    @Test
    void signData() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchProviderException {
        String salt = "the salt";
        Signing.EncryptedKeyPair keyPair = Signing.newKeyPair("xyz", salt);

        String data = "my data";
        Signing signer = new Signing(keyPair);
        Signing.SignatureData signature = signer.signData(data, "xyz", salt);
        Assertions.assertThat(signature.getSignatureB64()).hasSize(96);

        assertThrows(RuntimeException.class, () -> signer.signData(data, "bad", salt));
    }

    @Test
    void verifySignature() throws GeneralSecurityException {
        String salt = "the salt";
        Signing.EncryptedKeyPair keyPair = Signing.newKeyPair("xyz", salt);

        String data = "my data";
        Signing signer = new Signing(keyPair);
        Signing.SignatureData signature = signer.signData(data, "xyz", salt);

        Signing.PublicKeyOnly verifyingKey = new Signing.PublicKeyOnly(keyPair.publicKey);

        Assertions.assertThat(Signing.verifySignature(data, signature, verifyingKey)).isTrue();

        Signing.EncryptedKeyPair otherKeyPair = Signing.newKeyPair("xyz", salt);
        Signing.PublicKeyOnly wrongVerifyingKey = new Signing.PublicKeyOnly(otherKeyPair.publicKey);

        Assertions.assertThat(Signing.verifySignature(data, signature, wrongVerifyingKey)).isFalse();
    }
}
