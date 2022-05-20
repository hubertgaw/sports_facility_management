package pl.lodz.hubertgaw.security;

import io.smallrye.jwt.build.Jwt;
import io.smallrye.jwt.build.JwtClaimsBuilder;
import org.eclipse.microprofile.jwt.Claims;
import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.User;
import pl.lodz.hubertgaw.repository.entity.RoleName;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.management.relation.Role;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

@Singleton
public class TokenUtils {

    @Inject
    Logger logger;

    public String generateToken(User user, Long duration, String issuer) {
        logger.info("Method generateToken() called with arguments: {}, {}, {}", user, duration, issuer);

        Set<String> rolesString = new HashSet<>();
        for (RoleName role : user.getRoles()) {
            rolesString.add(role.toString());

            logger.info("Role:{} added", role);
        }

        return Jwt.issuer(issuer)
                .subject(user.getEmail())
                .groups(rolesString)
                .claim("firstName", user.getFirstName())
                .claim("lastName", user.getLastName())
                .claim("phoneNumber", user.getPhoneNumber())
                .expiresAt(System.currentTimeMillis() + duration)
                .sign();
    }

}