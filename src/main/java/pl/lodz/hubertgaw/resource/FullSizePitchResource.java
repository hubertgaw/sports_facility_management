package pl.lodz.hubertgaw.resource;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.FullSizePitch;
import pl.lodz.hubertgaw.service.FullSizePitchService;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Path("/api/full_size_pitches")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FullSizePitchResource {

    private final pl.lodz.hubertgaw.service.FullSizePitchService FullSizePitchService;
    private final Logger logger;

    public FullSizePitchResource(pl.lodz.hubertgaw.service.FullSizePitchService FullSizePitchService, Logger logger) {
        this.FullSizePitchService = FullSizePitchService;
        this.logger = logger;
    }

    @GET
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Get all FullSizePitchs",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.ARRAY, implementation = FullSizePitch.class)))
            }
    )
    public Response get() {
        return Response.ok(FullSizePitchService.findAll()).build();
    }

    @GET
    @Path("/{sportObjectId}")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Get FullSizePitch by id",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = FullSizePitch.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No FullSizePitch found for id provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    public Response getById(@PathParam("sportObjectId") Integer sportObjectId) {
        Optional<FullSizePitch> optional = FullSizePitchService.findById(sportObjectId);
        return !optional.isEmpty() ? Response.ok(optional.get()).build() : Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "201",
                            description = "FullSizePitch Created",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = FullSizePitch.class))),
                    @APIResponse(
                            responseCode = "400",
                            description = "FullSizePitch already exists for Id",
                            content = @Content(mediaType = "application/json")),
            }
    )
    public Response post(@Valid FullSizePitch FullSizePitch) {
        logger.info("post");
        final FullSizePitch saved = FullSizePitchService.save(FullSizePitch);
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }

    @PUT
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "FullSizePitch updated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = FullSizePitch.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No FullSizePitch found for athleticsTrackId provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    public Response put(@Valid FullSizePitch fullSizePitch) {
        final FullSizePitch saved = FullSizePitchService.update(fullSizePitch);
        return Response.ok(saved).build();
    }

    @PUT
    @Path("/{sportObjectId}/rent_equipment/{rentEquipmentId}")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "FullSizePitch updated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = FullSizePitch.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No FullSizePitch found for Id provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    public Response putEquipmentToObject(@PathParam("sportObjectId") Integer sportObjectId,
                                         @PathParam("rentEquipmentId") Integer rentEquipmentId) {
        final FullSizePitch saved = FullSizePitchService.putEquipmentToObject(sportObjectId, rentEquipmentId);
        return Response.ok(saved).build();
    }

}