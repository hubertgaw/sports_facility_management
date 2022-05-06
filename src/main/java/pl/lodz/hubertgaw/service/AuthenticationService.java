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

@ApplicationScoped
public class AuthenticationService {

    @Inject
    PasswordEncoder passwordEncoder;
    @Inject
    UserService userService;
    @Inject
    TokenUtils tokenUtils;

    @ConfigProperty(name = "pl.lodz.hubertgaw.sports_facility_management.jwt.duration")
    Long duration;
    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;


    public AuthResponse login (AuthRequest request) {
        User u = userService.findByEmail(request.getEmail());
        if (u != null && u.getPassword().equals(passwordEncoder.encode(request.getPassword()))) {
            return new AuthResponse(tokenUtils.generateToken(u, duration, issuer));
        } else {
            return new AuthResponse("UNAUTHORIZED");
        }
    }
}
