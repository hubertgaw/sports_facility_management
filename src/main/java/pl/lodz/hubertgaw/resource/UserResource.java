package pl.lodz.hubertgaw.resource;

import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.User;
import pl.lodz.hubertgaw.service.UserService;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UserService userService;
    private final Logger logger;

    public UserResource(UserService userService, Logger logger) {
        this.userService = userService;
        this.logger = logger;
    }

    @GET
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Get all Users",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.ARRAY, implementation = User.class)))
            }
    )
    @RolesAllowed("ADMIN")
    public Response get() {
        return Response.ok(userService.findAll()).build();
    }

    @GET
    @Path("/id/{userId}")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Get User by id",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = User.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No User found for id provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    @RolesAllowed("ADMIN")
    public Response getById(@PathParam("userId") Integer userId) {
        return Response.ok(userService.findById(userId)).build();
    }

    //TODO: chyba najlepiej zrobić endpoint: /user, w którym to zwróci ZALOGOWANEMU userowi informacje o nim.

    @GET
    @Path("/email/{email}")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Get User by email",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = User.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No User found for email provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    @RolesAllowed("ADMIN")
    public Response getByEmail(@PathParam("email") String email) {
        return Response.ok(userService.findByEmail(email)).build();
    }

    @GET
    @Path("/role/{role}")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Get User by role",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = User.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No User found for role provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    @RolesAllowed("ADMIN")
    public Response getByRole(@PathParam("role") String role) {
        return Response.ok(userService.findByRole(role)).build();
    }


    @POST
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "201",
                            description = "User Created",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = User.class))),
                    @APIResponse(
                            responseCode = "400",
                            description = "User already exists for userId",
                            content = @Content(mediaType = "application/json")),
            }
    )
    @PermitAll
    public Response post(@Valid User user) {
        logger.info("post");
        final User saved = userService.save(user);
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }

    @PUT
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "User updated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = User.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No User found for userId provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    @RolesAllowed("ADMIN")
    public Response put(@Valid User user) {
        final User saved = userService.update(user);
        return Response.ok(saved).build();
    }

    //TODO: analogicznie jw. dla update, czyli osobny endpoint i user updatuje siebie.

    @DELETE
    @Path("{userId}")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "204",
                            description = "User by id deleted",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = User.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No User found for id provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    @RolesAllowed("ADMIN")
    public Response deleteUser(@PathParam("userId") Integer userId) {
        userService.deleteUserById(userId);
        return Response.noContent().build();

    }

}
