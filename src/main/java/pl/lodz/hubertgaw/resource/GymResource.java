package pl.lodz.hubertgaw.resource;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.Gym;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/gyms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GymResource {

    private final pl.lodz.hubertgaw.service.GymService gymService;
    private final Logger logger;

    public GymResource(pl.lodz.hubertgaw.service.GymService GymService, Logger logger) {
        this.gymService = GymService;
        this.logger = logger;
    }

    @GET
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Get all Gyms",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.ARRAY, implementation = Gym.class)))
            }
    )
    @PermitAll
    public Response get() {
        return Response.ok(gymService.findAll()).build();
    }

    @GET
    @Path("/{sportObjectId}")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Get Gym by id",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = Gym.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No Gym found for id provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    @PermitAll
    public Response getById(@PathParam("sportObjectId") Integer sportObjectId) {
        return Response.ok(gymService.findById(sportObjectId)).build();
    }

    @POST
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "201",
                            description = "Gym Created",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = Gym.class))),
                    @APIResponse(
                            responseCode = "400",
                            description = "Gym already exists for Id",
                            content = @Content(mediaType = "application/json")),
            }
    )
    @RolesAllowed("ADMIN")
    public Response post(@Valid Gym Gym) {
        logger.info("post");
        final Gym saved = gymService.save(Gym);
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }

    @PUT
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Gym updated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = Gym.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No Gym found for Id provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    @RolesAllowed("ADMIN")
    public Response put(@Valid Gym Gym) {
        final Gym saved = gymService.update(Gym);
        return Response.ok(saved).build();
    }

    @PUT
    @Path("/{sportObjectId}/rent_equipment/{rentEquipmentId}")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Gym updated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = Gym.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No Gym found for Id provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    @RolesAllowed("ADMIN")
    public Response putEquipmentToObject(@PathParam("sportObjectId") Integer sportObjectId,
                                         @PathParam("rentEquipmentId") Integer rentEquipmentId) {
        final Gym saved = gymService.putEquipmentToObject(sportObjectId, rentEquipmentId);
        return Response.ok(saved).build();
    }


}
