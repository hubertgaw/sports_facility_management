package pl.lodz.hubertgaw.resource;

import io.quarkus.security.Authenticated;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import pl.lodz.hubertgaw.dto.AuthRequest;
import pl.lodz.hubertgaw.dto.AuthResponse;
import pl.lodz.hubertgaw.dto.User;
import pl.lodz.hubertgaw.security.PasswordEncoder;
import pl.lodz.hubertgaw.security.TokenUtils;
import pl.lodz.hubertgaw.service.UserService;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static java.util.Objects.requireNonNull;

@Path("/user")
public class AuthenticationResource {

    @Inject
    PasswordEncoder passwordEncoder;
    @Inject
    UserService userService;

    @ConfigProperty(name = "pl.lodz.hubertgaw.sports_facility_management.jwt.duration") public Long duration;
    @ConfigProperty(name = "mp.jwt.verify.issuer") public String issuer;

    @PermitAll
    @POST
    @Path("/login") @Produces(MediaType.APPLICATION_JSON)
    public Response login(AuthRequest authRequest) {
        User u = userService.findByEmail(authRequest.getEmail());
        if (u != null && u.getPassword().equals(passwordEncoder.encode(authRequest.getPassword()))) {
            try {
                return Response.ok(new AuthResponse(TokenUtils.generateToken(u.getEmail(), u.getRoles(), duration, issuer))).build();
            } catch (Exception e) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

}