package pl.lodz.hubertgaw.security;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.enterprise.context.RequestScoped;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

@RequestScoped
public class PasswordEncoder {

    @ConfigProperty(name = "pl.lodz.hubertgaw.sports_facility_management.password.secret")  private String secret;
    @ConfigProperty(name = "pl.lodz.hubertgaw.sports_facility_management.password.iteration")  private Integer iteration;
    @ConfigProperty(name = "pl.lodz.hubertgaw.sports_facility_management.password.keylength")  private Integer keylength;

    /**
     * More info (https://www.owasp.org/index.php/Hashing_Java) 404 :(
     * @param cs password
     * @return encoded password
     */
    public String encode(CharSequence cs) {
        try {
            byte[] result = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
                    .generateSecret(new PBEKeySpec(cs.toString().toCharArray(), secret.getBytes(), iteration, keylength))
                    .getEncoded();
            return Base64.getEncoder().encodeToString(result);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new RuntimeException(ex);
        }
    }
}