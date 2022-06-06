package pl.lodz.hubertgaw.service;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import pl.lodz.hubertgaw.dto.AuthRequest;
import pl.lodz.hubertgaw.dto.AuthResponse;
import pl.lodz.hubertgaw.dto.User;
import pl.lodz.hubertgaw.security.PasswordEncoder;
import pl.lodz.hubertgaw.security.TokenUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;

@ApplicationScoped
public class AuthenticationService {

    @Inject
    PasswordEncoder passwordEncoder;
    @Inject
    UserService userService;
    @Inject
    TokenUtils tokenUtils;
    @Inject
    Logger logger;

    @ConfigProperty(name = "pl.lodz.hubertgaw.sports_facility_management.jwt.duration")
    Long duration;
    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;


    public AuthResponse login (AuthRequest request) {
        logger.info("Method login() called with argument: {}", request);

        User u = userService.findByEmail(request.getEmail());

        logger.info("Found by email user:{}", u);

        if (u != null && u.getPassword().equals(passwordEncoder.encode(request.getPassword()))) {
            logger.info("Login successfully");
            return new AuthResponse(tokenUtils.generateToken(u, duration, issuer));
        } else {
            logger.warn("Login unsuccessfully");
            return new AuthResponse("UNAUTHORIZED");
        }
    }
}
