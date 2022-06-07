package pl.lodz.hubertgaw.resource;

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
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UserService userService;
    private final Logger logger;

    public UserResource(UserService userService, Logger logger) {
        this.userService = userService;
        this.logger = logger;

        logger.info("Constructor UserResource called");
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
        logger.info("Method get() called");

        Response response = Response.ok(userService.findAll()).build();

        logger.info("Built response: {}", response);

        return response;
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
        logger.info("Method getById() called with argument: {}", userId);

        Response response = Response.ok(userService.findById(userId)).build();

        logger.info("Built response: {}", response);

        return response;
    }


    @GET
    @Path("/info")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Get information about logged user",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = User.class)))
            }
    )
    @RolesAllowed("USER")
    public Response getLoggedUser(@Context SecurityContext userContext) {
        logger.info("Method getLoggedUser() called with argument: {}", userContext);

        Response response = Response.ok(userService.findByEmail(userContext.getUserPrincipal().getName())).build();

        logger.info("Built response: {}", response);

        return response;
    }


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
        logger.info("Method getByEmail() called with argument: {}", email);

        Response response = Response.ok(userService.findByEmail(email)).build();

        logger.info("Built response: {}", response);

        return response;
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
        logger.info("Method getByRole() called with argument: {}", role);

        Response response = Response.ok(userService.findByRole(role)).build();

        logger.info("Built response: {}", response);

        return response;
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
        logger.info("Method post() called with argument: {}", user);

        final User saved = userService.save(user);
        Response response = Response.status(Response.Status.CREATED).entity(saved).build();

        logger.info("Built response: {}", response);

        return response;
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
    public Response putUserByAdmin(@Valid User user) {
        logger.info("Method putUserByAdmin() called with argument: {}", user);

        final User saved = userService.update(user);
        Response response = Response.ok(saved).build();

        logger.info("Built response: {}", response);

        return response;
    }

    @PUT
    @Path("/info")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Update logged user's information",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = User.class)))
            }
    )
    @RolesAllowed("USER")
    public Response putUserByUser(@Context SecurityContext userContext, @Valid User user) {
        logger.info("Method putUserByUser() called with argument: {}, {}", userContext, user);

        int updatedUserId = userService.findByEmail((userContext.getUserPrincipal().getName())).getId();
        user.setId(updatedUserId);
        final User saved = userService.update(user);
        Response response = Response.ok(saved).build();

        logger.info("Built response: {}", response);

        return response;
    }

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
        logger.info("Method deleteUser() called with arguments: {}", userId);

        userService.deleteUserById(userId);
        Response response = Response.noContent().build();

        logger.info("Built response: {}", response);

        return response;

    }

}
