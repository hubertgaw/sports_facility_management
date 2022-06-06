package pl.lodz.hubertgaw.resource;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.SportsHall;
import pl.lodz.hubertgaw.service.SportsHallService;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/sports_halls")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SportsHallResource {

    private final SportsHallService sportsHallService;
    private final Logger logger;

    public SportsHallResource(SportsHallService SportsHallService, Logger logger) {
        this.sportsHallService = SportsHallService;
        this.logger = logger;

        logger.info("Constructor SportsHallResource called");
    }

    @GET
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Get all SportsHalls",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.ARRAY, implementation = SportsHall.class)))
            }
    )
    @PermitAll
    public Response get() {
        logger.info("Method get() called");

        Response response = Response.ok(sportsHallService.findAll()).build();

        logger.info("Built response: {}", response);

        return response;
    }

    @GET
    @Path("/{sportObjectId}")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Get SportsHall by id",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = SportsHall.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No SportsHall found for id provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    @PermitAll
    public Response getById(@PathParam("sportObjectId") Integer sportObjectId) {
        logger.info("Method getById() called with argument: {}", sportObjectId);

        Response response = Response.ok(sportsHallService.findById(sportObjectId)).build();

        logger.info("Built response: {}", response);

        return response;
    }

    @POST
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "201",
                            description = "SportsHall Created",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = SportsHall.class))),
                    @APIResponse(
                            responseCode = "400",
                            description = "SportsHall already exists for Id",
                            content = @Content(mediaType = "application/json")),
            }
    )
    @RolesAllowed("ADMIN")
    public Response post(@Valid SportsHall sportsHall) {
        logger.info("Method post() called with argument: {}", sportsHall);

        final SportsHall saved = sportsHallService.save(sportsHall);
        Response response = Response.status(Response.Status.CREATED).entity(saved).build();

        logger.info("Built response: {}", response);

        return response;
    }

    @PUT
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "SportsHall updated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = SportsHall.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No SportsHall found for athleticsTrackId provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    @RolesAllowed("ADMIN")
    public Response put(@Valid SportsHall sportsHall) {
        logger.info("Method put() called with argument: {}", sportsHall);

        final SportsHall saved = sportsHallService.update(sportsHall);
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
                            description = "SportsHall updated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = SportsHall.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No SportsHall found for Id provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    @RolesAllowed("ADMIN")
    public Response putEquipmentToObject(@PathParam("sportObjectId") Integer sportObjectId,
                                         @PathParam("rentEquipmentId") Integer rentEquipmentId) {
        logger.info("Method putEquipmentToObject() called with arguments: {}, {}", sportObjectId, rentEquipmentId);

        final SportsHall saved = sportsHallService.putEquipmentToObject(sportObjectId, rentEquipmentId);
        Response response = Response.ok(saved).build();

        logger.info("Built response: {}", response);

        return response;
    }

}
