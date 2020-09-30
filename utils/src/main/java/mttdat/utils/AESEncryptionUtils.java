package mttdat.utils;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.nio.charset.StandardCharsets;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class AESEncryptionUtils {

    private final String INSTANCE_CIPHER = "AES/GCM/NoPadding";
    private final String ANDROID_KEY_STORE_PROVIDER = "AndroidKeyStore";
    private byte[] iv;

    public SecretKey generateSecretKey(String alias){
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            return null;
        }

        try {
            final KeyGenerator keyGenerator = KeyGenerator
                    .getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE_PROVIDER);

            final KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(
                        alias, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build();

            keyGenerator.init(keyGenParameterSpec);

            return keyGenerator.generateKey();

        } catch (Exception e) {
            return null;
        }
    }

    public String encrypt(SecretKey secretKey, String textToEncrypt){
        try {
            Cipher cipher = Cipher.getInstance(INSTANCE_CIPHER);

            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            iv = cipher.getIV();    // This is used to decrypt.

            byte[] encryptedData = cipher.doFinal(textToEncrypt.getBytes(StandardCharsets.UTF_8));

            return new String(encryptedData, StandardCharsets.ISO_8859_1);
        } catch (Exception e) {
            return null;
        }
    }

    public byte[] encryptV2(SecretKey secretKey, String textToEncrypt){
        try {
            Cipher cipher = Cipher.getInstance(INSTANCE_CIPHER);

            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            iv = cipher.getIV();    // This is used to decrypt.

            return cipher.doFinal(textToEncrypt.getBytes(StandardCharsets.UTF_8));

        } catch (Exception e) {
            return null;
        }
    }

    public String decrypt(String alias, String encryptedString){
        try {
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEY_STORE_PROVIDER);
            keyStore.load(null);

            KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore
                    .getEntry(alias, null);

            SecretKey secretKey = secretKeyEntry.getSecretKey();

            Cipher cipher = Cipher.getInstance(INSTANCE_CIPHER);
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);  // Length 128 is the maximum.
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

            byte[] decodedData = cipher.doFinal(encryptedString.getBytes(StandardCharsets.ISO_8859_1));
            return new String(decodedData, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    public byte[] decryptV2(String alias, byte[] encryptedString){
        try {
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEY_STORE_PROVIDER);
            keyStore.load(null);

            KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore
                    .getEntry(alias, null);

            SecretKey secretKey = secretKeyEntry.getSecretKey();

            Cipher cipher = Cipher.getInstance(INSTANCE_CIPHER);
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);  // Length 128 is the maximum.
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

            return cipher.doFinal(encryptedString);
        } catch (Exception e) {
            return null;
        }
    }
}
