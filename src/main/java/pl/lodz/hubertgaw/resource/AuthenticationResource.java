package pl.lodz.hubertgaw.resource;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
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
    @Inject
    Logger logger;

    @POST
    @Path("/login") @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response login(AuthRequest authRequest) {
        logger.info("Method login() called with argument: {}", authRequest);

        AuthResponse response;
        try {
            response = authenticationService.login(authRequest);
        } catch (Exception e) {
            Response builtResponse = Response.status(Response.Status.UNAUTHORIZED).build();

            logger.info("Built response: {}", builtResponse);

            return builtResponse;
        }
        if (!response.getToken().equals("UNAUTHORIZED")) {
            Response builtResponse = Response.ok(response).build();

            logger.info("Built response: {}", builtResponse);

            return builtResponse;
        } else {
            Response builtResponse = Response.status(Response.Status.UNAUTHORIZED).build();

            logger.info("Built response: {}", builtResponse);

            return builtResponse;
        }
    }

}