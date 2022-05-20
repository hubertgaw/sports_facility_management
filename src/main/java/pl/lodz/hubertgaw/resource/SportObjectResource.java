package pl.lodz.hubertgaw.resource;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.SportObject;
import pl.lodz.hubertgaw.service.BookingService;
import pl.lodz.hubertgaw.service.SportObjectService;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/sport_objects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SportObjectResource {

    private final SportObjectService sportObjectService;
    private final BookingService bookingService;
    private final Logger logger;

    public SportObjectResource(SportObjectService sportObjectService, BookingService bookingService, Logger logger) {
        this.sportObjectService = sportObjectService;
        this.bookingService = bookingService;
        this.logger = logger;

        logger.info("Constructor SportObjectResource called");
    }

    @GET
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Get all SportObjects",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.ARRAY, implementation = SportObject.class)))
            }
    )
    @PermitAll
    public Response get() {
        logger.info("Method get() called");

        Response response = Response.ok(sportObjectService.findAll()).build();

        logger.info("Built response: {}", response);

        return response;
    }

    @GET
    @Path("/{sportObjectId}")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Get SportObject by id",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = SportObject.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No SportObject found for id provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    @PermitAll
    public Response getById(@PathParam("sportObjectId") Integer sportObjectId) {
        logger.info("Method getById() called with argument: {}", sportObjectId);

        Response response = Response.ok(sportObjectService.findById(sportObjectId)).build();

        logger.info("Built response: {}", response);

        return response;
    }


    @PUT
    @Path("/{sportObjectId}/rent_equipment/{rentEquipmentId}")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "SportObject updated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = SportObject.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No SportObject found for Id provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    @RolesAllowed("ADMIN")
    public Response putEquipmentToObject(@PathParam("sportObjectId") Integer sportObjectId,
                                         @PathParam("rentEquipmentId") Integer rentEquipmentId) {
        logger.info("Method putEquipmentToObject() called with arguments: {}, {}", sportObjectId, rentEquipmentId);

        final SportObject saved = sportObjectService.putEquipmentToObject(sportObjectId, rentEquipmentId);
        Response response = Response.ok(saved).build();

        logger.info("Built response: {}", response);

        return response;
    }


    @DELETE
    @Path("{sportObjectId}")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "204",
                            description = "SportObject by id deleted",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = SportObject.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No SportObject found for id provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    @RolesAllowed("ADMIN")
    public Response deleteSportObject(@PathParam("sportObjectId") Integer sportObjectId) {
        logger.info("Method deleteSportObject called with argument: {}", sportObjectId);

        sportObjectService.deleteSportObjectById(sportObjectId);
        Response response = Response.noContent().build();

        logger.info("Built response: {}", response);

        return response;

    }

    @GET
    @Path("/{sportObjectId}/bookings")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Get Bookings from SportObject by id",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = SportObject.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No SportObject found for id provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    @PermitAll
    public Response getBookingsFromSportObject(@PathParam("sportObjectId") Integer sportObjectId) {
        logger.info("Method getBookingsFromSportObject() called with argument: {}", sportObjectId);

        Response response = Response.ok(sportObjectService.findBookingsForSportObject(sportObjectId)).build();

        logger.info("Built response: {}", response);

        return response;
    }

}
