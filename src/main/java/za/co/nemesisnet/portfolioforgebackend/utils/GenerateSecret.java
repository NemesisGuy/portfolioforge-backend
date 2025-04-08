package za.co.nemesisnet.portfolioforgebackend.utils;

import javax.crypto.SecretKey;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Base64; // Use java.util.Base64

public class GenerateSecret {
    public static void main(String[] args) {
        // Generate a key suitable for the HS512 algorithm
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        // Get the raw bytes of the key
        byte[] keyBytes = key.getEncoded();
        // Encode the raw bytes into a Base64 string
        String base64EncodedKey = Base64.getEncoder().encodeToString(keyBytes);

        System.out.println("Generated Base64 Encoded Secret Key:");
        System.out.println(base64EncodedKey);
    }
}