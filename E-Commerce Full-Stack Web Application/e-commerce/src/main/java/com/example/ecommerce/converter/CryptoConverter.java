package com.example.ecommerce.converter;

import com.example.ecommerce.util.SpringContext;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.core.env.Environment;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

@Converter(autoApply = false)
public class CryptoConverter implements AttributeConverter<String,String> {

    //lazy-singleton key
    private static volatile SecretKey KEY;

    private static SecretKey key() {
        if (KEY == null) {
            synchronized (CryptoConverter.class) {
                if (KEY == null) {
                    Environment env = SpringContext.getBean(Environment.class);
                    String b64 = Objects.requireNonNull(
                            env.getProperty("crypto.key"),
                            "Missing property crypto.key in application.properties");
                    KEY = new SecretKeySpec(Base64.getDecoder().decode(b64), "AES");
                }
            }
        }
        return KEY;
    }


    @Override
    public String convertToDatabaseColumn(String plain) {
        if (plain == null) return null;
        try {
            byte[] iv = SecureRandom.getInstanceStrong().generateSeed(12);   //96‑bit
            Cipher c  = Cipher.getInstance("AES/GCM/NoPadding");
            c.init(Cipher.ENCRYPT_MODE, key(), new GCMParameterSpec(128, iv));
            byte[] cipher = c.doFinal(plain.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(
                    ByteBuffer.allocate(iv.length + cipher.length)
                            .put(iv).put(cipher).array());
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            byte[] blob = Base64.getDecoder().decode(dbData);
            ByteBuffer bb = ByteBuffer.wrap(blob);
            byte[] iv     = new byte[12]; bb.get(iv);
            byte[] cipher = new byte[bb.remaining()]; bb.get(cipher);

            Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
            c.init(Cipher.DECRYPT_MODE, key(), new GCMParameterSpec(128, iv));
            return new String(c.doFinal(cipher), StandardCharsets.UTF_8);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }
}
