package pl.lodz.hubertgaw.resource;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import pl.lodz.hubertgaw.dto.AuthRequest;
import pl.lodz.hubertgaw.dto.AuthResponse;
import pl.lodz.hubertgaw.dto.User;
import pl.lodz.hubertgaw.security.PasswordEncoder;
import pl.lodz.hubertgaw.security.TokenUtils;
import pl.lodz.hubertgaw.service.AuthenticationService;
import pl.lodz.hubertgaw.service.UserService;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/user")
public class AuthenticationResource {

    @Inject
    AuthenticationService authenticationService;

    @POST
    @Path("/login") @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response login(AuthRequest authRequest) {
        AuthResponse response;
        try {
            response = authenticationService.login(authRequest);
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        if (!response.getToken().equals("UNAUTHORIZED")) {
            return Response.ok(response).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

}