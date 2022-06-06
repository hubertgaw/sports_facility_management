package pl.lodz.hubertgaw.resource;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.TennisCourt;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
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

        logger.info("Constructor TennisCourtResource called");
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
    @PermitAll
    public Response get() {
        logger.info("Method get() called");

        Response response = Response.ok(tennisCourtService.findAll()).build();

        logger.info("Built response: {}", response);

        return response;
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
    @PermitAll
    public Response getById(@PathParam("sportObjectId") Integer sportObjectId) {
        logger.info("Method getById() called with argument: {}", sportObjectId);

        Response response = Response.ok(tennisCourtService.findById(sportObjectId)).build();

        logger.info("Built response: {}", response);

        return response;
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
    @RolesAllowed("ADMIN")
    public Response post(@Valid TennisCourt tennisCourt) {
        logger.info("Method post() called with argument: {}", tennisCourt);

        final TennisCourt saved = tennisCourtService.save(tennisCourt);
        Response response = Response.status(Response.Status.CREATED).entity(saved).build();

        logger.info("Built response: {}", response);

        return response;
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
    @RolesAllowed("ADMIN")
    public Response put(@Valid TennisCourt tennisCourt) {
        logger.info("Method put() called with argument: {}", tennisCourt);

        final TennisCourt saved = tennisCourtService.update(tennisCourt);
        Response response = Response.ok(saved).build();

        logger.info("Built response: {}", response);

        return response;
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
    @RolesAllowed("ADMIN")
    public Response putEquipmentToObject(@PathParam("sportObjectId") Integer sportObjectId,
                                         @PathParam("rentEquipmentId") Integer rentEquipmentId) {
        logger.info("Method putEquipmentToObject() called with arguments: {}, {}", sportObjectId, rentEquipmentId);

        final TennisCourt saved = tennisCourtService.putEquipmentToObject(sportObjectId, rentEquipmentId);
        Response response = Response.ok(saved).build();

        logger.info("Built response: {}", response);

        return response;
    }

}
