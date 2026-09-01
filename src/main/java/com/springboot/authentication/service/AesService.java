package com.springboot.authentication.service;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

@Service
public class AesService {

  private static final String ALGORITHM = "AES/ECB/PKCS5Padding";

  public byte[] encrypt(byte[] data, String secretKey) throws Exception {
    validateKey(secretKey);

    SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes("UTF-8"), "AES");
    Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.ENCRYPT_MODE, keySpec);

    return cipher.doFinal(data);
  }

  public byte[] decrypt(byte[] encryptedData, String secretKey) throws Exception {
    validateKey(secretKey);

    SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes("UTF-8"), "AES");
    Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.DECRYPT_MODE, keySpec);

    return cipher.doFinal(encryptedData);
  }

  private void validateKey(String secretKey) {
    if (secretKey == null || secretKey.length() != 16) {
      throw new IllegalArgumentException("Key harus tepat 16 karakter untuk AES-128");
    }
  }
}