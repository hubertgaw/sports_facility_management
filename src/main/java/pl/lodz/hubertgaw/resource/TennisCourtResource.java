package pl.lodz.hubertgaw.resource;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.TennisCourt;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/tennis_courts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TennisCourtResource {

    private final pl.lodz.hubertgaw.service.TennisCourtService tennisCourtService;
    private final Logger logger;

    public TennisCourtResource(pl.lodz.hubertgaw.service.TennisCourtService TennisCourtService, Logger logger) {
        this.tennisCourtService = TennisCourtService;
        this.logger = logger;
    }

    @GET
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Get all TennisCourts",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.ARRAY, implementation = TennisCourt.class)))
            }
    )
    public Response get() {
        return Response.ok(tennisCourtService.findAll()).build();
    }

    @GET
    @Path("/{sportObjectId}")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Get TennisCourt by id",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = TennisCourt.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No TennisCourt found for id provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    public Response getById(@PathParam("sportObjectId") Integer sportObjectId) {
        return Response.ok(tennisCourtService.findById(sportObjectId)).build();
    }

    @POST
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "201",
                            description = "TennisCourt Created",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = TennisCourt.class))),
                    @APIResponse(
                            responseCode = "400",
                            description = "TennisCourt already exists for Id",
                            content = @Content(mediaType = "application/json")),
            }
    )
    public Response post(@Valid TennisCourt TennisCourt) {
        logger.info("post");
        final TennisCourt saved = tennisCourtService.save(TennisCourt);
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }

    @PUT
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "TennisCourt updated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = TennisCourt.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No TennisCourt found for athleticsTrackId provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    public Response put(@Valid TennisCourt TennisCourt) {
        final TennisCourt saved = tennisCourtService.update(TennisCourt);
        return Response.ok(saved).build();
    }

    @PUT
    @Path("/{sportObjectId}/rent_equipment/{rentEquipmentId}")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "TennisCourt updated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = TennisCourt.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No TennisCourt found for Id provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    public Response putEquipmentToObject(@PathParam("sportObjectId") Integer sportObjectId,
                                         @PathParam("rentEquipmentId") Integer rentEquipmentId) {
        final TennisCourt saved = tennisCourtService.putEquipmentToObject(sportObjectId, rentEquipmentId);
        return Response.ok(saved).build();
    }

}
