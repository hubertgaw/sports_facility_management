package pl.lodz.hubertgaw.resource;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.AthleticsTrack;
import pl.lodz.hubertgaw.service.AthleticsTrackService;

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
    public Response get() {
        return Response.ok(athleticsTrackService.findAll()).build();
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
    public Response getById(@PathParam("athleticsTrackId") Integer athleticsTrackId) {
//        Optional<AthleticsTrack> optional = athleticsTrackService.findById(athleticsTrackId);
        return Response.ok(athleticsTrackService.findById(athleticsTrackId)).build();
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
    public Response post(@Valid AthleticsTrack athleticsTrack) {
        logger.info("post");
        final AthleticsTrack saved = athleticsTrackService.save(athleticsTrack);
        return Response.status(Response.Status.CREATED).entity(saved).build();
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
                            description = "No AthleticsTrack found for athleticsTrackId provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    public Response put(@Valid AthleticsTrack athleticsTrack) {
        final AthleticsTrack saved = athleticsTrackService.update(athleticsTrack);
        return Response.ok(saved).build();
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
    public Response putEquipmentToObject(@PathParam("athleticsTrackId") Integer athleticsTrackId,
                                         @PathParam("rentEquipmentId") Integer rentEquipmentId) {
        final AthleticsTrack saved = athleticsTrackService.putEquipmentToObject(athleticsTrackId, rentEquipmentId);
        return Response.ok(saved).build();
    }

}

