package pl.lodz.hubertgaw.resource;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.AthleticsTrack;
import pl.lodz.hubertgaw.service.AthleticsTrackService;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/athletics_tracks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AthleticsTrackResource {

    private final AthleticsTrackService athleticsTrackService;
    private final Logger logger;

    public AthleticsTrackResource(AthleticsTrackService athleticsTrackService, Logger logger) {
        this.athleticsTrackService = athleticsTrackService;
        this.logger = logger;

        logger.info("Constructor AthleticsTrackResource called");
    }

    @GET
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Get all Athletics Tracks",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.ARRAY, implementation = AthleticsTrack.class)))
            }
    )
    @PermitAll
    public Response get() {
        logger.info("Method get() called");

        Response response = Response.ok(athleticsTrackService.findAll()).build();

        logger.info("Built response: {}", response);

        return response;
    }

    @GET
    @Path("/{athleticsTrackId}")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Get Athletics_Track by id",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = AthleticsTrack.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No AthleticsTrack found for id provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    @PermitAll
    public Response getById(@PathParam("athleticsTrackId") Integer athleticsTrackId) {
        logger.info("Method getById() called with argument: {}", athleticsTrackId);

        Response response = Response.ok(athleticsTrackService.findById(athleticsTrackId)).build();

        logger.info("Built response: {}", response);

        return response;
    }

    @POST
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "201",
                            description = "AthleticsTrack Created",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = AthleticsTrack.class))),
                    @APIResponse(
                            responseCode = "400",
                            description = "AthleticsTrack already exists for athleticsTrackId",
                            content = @Content(mediaType = "application/json")),
            }
    )
    @RolesAllowed("ADMIN")
    public Response post(@Valid AthleticsTrack athleticsTrack) {
        logger.info("Method post() called with argument: {}", athleticsTrack);

        final AthleticsTrack saved = athleticsTrackService.save(athleticsTrack);
        Response response = Response.status(Response.Status.CREATED).entity(saved).build();

        logger.info("Built response: {}", response);

        return response;
    }

    @PUT
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "AthleticsTrack updated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = AthleticsTrack.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No AthleticsTrack found for Id provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    @RolesAllowed("ADMIN")
    public Response put(@Valid AthleticsTrack athleticsTrack) {
        logger.info("Method put() called with argument: {}", athleticsTrack);

        final AthleticsTrack saved = athleticsTrackService.update(athleticsTrack);
        Response response = Response.ok(saved).build();

        logger.info("Built response: {}", response);

        return response;
    }

    @PUT
    @Path("/{athleticsTrackId}/rent_equipment/{rentEquipmentId}")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "AthleticsTrack updated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = AthleticsTrack.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No AthleticsTrack found for athleticsTrackId provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    @RolesAllowed("ADMIN")
    public Response putEquipmentToObject(@PathParam("athleticsTrackId") Integer athleticsTrackId,
                                         @PathParam("rentEquipmentId") Integer rentEquipmentId) {
        logger.info("Method putEquipmentToObject() called with arguments: {}, {}", athleticsTrackId, rentEquipmentId);

        final AthleticsTrack saved = athleticsTrackService.putEquipmentToObject(athleticsTrackId, rentEquipmentId);
        Response response = Response.ok(saved).build();

        logger.info("Built response: {}", response);

        return response;
    }

}

