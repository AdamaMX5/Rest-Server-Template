package de.freeschool.api.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Helper class that provides data signing and signature verification
 * - Generate unique key pairs for asymmetrical signing of data
 * - Private key is password protected
 * - Handles conversion between bytes (internal) and base64 encoded strings (external)
 */
public class Signing {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PublicKeyOnly {
        protected String publicKey;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class EncryptedKeyPair extends PublicKeyOnly {
        private String encryptedPrivateKey;

        public EncryptedKeyPair(String publicKey, String encryptedPrivateKey) {
            this.publicKey = publicKey;
            this.encryptedPrivateKey = encryptedPrivateKey;
        }
    }

    @Data
    public static class SignatureData {
        private String signatureB64;

        SignatureData(byte[] signature) {
            signatureB64 = Base64.getEncoder().encodeToString(signature);
        }

        byte[] getBytes() {
            return Base64.getDecoder().decode(signatureB64);
        }
    }

    final private EncryptedKeyPair encryptedKeyPair;

    public static EncryptedKeyPair newKeyPair(String encryptionPassword,
                                              String salt) throws NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, NoSuchProviderException {

        Security.insertProviderAt(new BouncyCastleProvider(), 1);
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", "BC");
        ECGenParameterSpec spec = new ECGenParameterSpec("secp256k1");
        keyPairGenerator.initialize(spec);
        KeyPair pair = keyPairGenerator.generateKeyPair();
        return new EncryptedKeyPair(keyToString(pair.getPublic()),
                PasswordEncryption.encrypt(keyToString(pair.getPrivate()), encryptionPassword, salt)
        );

    }

    private static String keyToString(Key key) {
        byte[] keyBytes = key.getEncoded();
        return Base64.getEncoder().encodeToString(keyBytes);
    }

    private static PublicKey stringToPublicKey(String publicKeyString) throws GeneralSecurityException {
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyString);
        KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", "BC");
        return keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));
    }

    private static PrivateKey stringToPrivateKey(String privateKeyString) throws GeneralSecurityException {
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyString);
        KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", "BC");
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
    }

    Signing(EncryptedKeyPair keyPair) {
        encryptedKeyPair = keyPair;
    }

    public SignatureData signData(String data, String password,
                                  String salt) throws PasswordEncryption.BadPasswordException {
        try {
            Signature signature = Signature.getInstance("SHA256withECDSA");
            signature.initSign(stringToPrivateKey(
                    PasswordEncryption.decrypt(encryptedKeyPair.encryptedPrivateKey, password, salt)));
            signature.update(data.getBytes(StandardCharsets.UTF_8));
            return new SignatureData(signature.sign());
        } catch (SignatureException | InvalidKeyException e) {
            throw new PasswordEncryption.BadPasswordException(e);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean verifySignature(String data, SignatureData signature,
                                          PublicKeyOnly verifyingKey) throws GeneralSecurityException {
        Signature sig = Signature.getInstance("SHA256withECDSA");
        sig.initVerify(stringToPublicKey(verifyingKey.publicKey));
        sig.update(data.getBytes(StandardCharsets.UTF_8));
        return sig.verify(signature.getBytes());
    }
}
