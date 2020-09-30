package mttdat.utils;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

public class RSAEncryptionUtils {

    private final String ANDROID_KEY_STORE_PROVIDER = "AndroidKeyStore";
    private final String INSTANCE_CIPHER = "RSA";

    public PublicKey generateKeys(String alias){
        KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEY_STORE_PROVIDER);

            KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(
                    alias, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setKeySize(2048)
                    .build();

            kpg.initialize(keyGenParameterSpec);

            KeyPair keyPair = kpg.genKeyPair();

            PrivateKey privateKey = keyPair.getPrivate();


            return keyPair.getPublic();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] encrypt(PublicKey publicKey, final String textToEncrypt){

        try {
            Cipher cipher = Cipher.getInstance(INSTANCE_CIPHER);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            // org.apache.commons.codec.binary.Hex.encodeHex(encryptedBytes)));
            return cipher.doFinal(textToEncrypt.getBytes());    // = encryptedBytes
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String decrypt(String alias, final byte[] encryptedBytes) {

        try {
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEY_STORE_PROVIDER);
            keyStore.load(null);

            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore
                    .getEntry(alias, null);


            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());

            return new String(cipher.doFinal(encryptedBytes), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
