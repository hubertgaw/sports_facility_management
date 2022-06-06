package pl.lodz.hubertgaw.resource;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.DartRoom;
import pl.lodz.hubertgaw.service.DartRoomService;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/dart_rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DartRoomResource {

    private final pl.lodz.hubertgaw.service.DartRoomService dartRoomService;
    private final Logger logger;

    public DartRoomResource(DartRoomService DartRoomService, Logger logger) {
        this.dartRoomService = DartRoomService;
        this.logger = logger;

        logger.info("Constructor DartRoomResource called");
    }

    @GET
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Get all DartRooms",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.ARRAY, implementation = DartRoom.class)))
            }
    )
    @PermitAll
    public Response get() {
        logger.info("Method get() called");

        Response response = Response.ok(dartRoomService.findAll()).build();

        logger.info("Built response: {}", response);

        return response;
    }

    @GET
    @Path("/{sportObjectId}")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Get DartRoom by id",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = DartRoom.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No DartRoom found for id provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    @PermitAll
    public Response getById(@PathParam("sportObjectId") Integer sportObjectId) {
        logger.info("Method getById() called with argument: {}", sportObjectId);

        Response response = Response.ok(dartRoomService.findById(sportObjectId)).build();

        logger.info("Built response: {}", response);

        return response;
    }

    @POST
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "201",
                            description = "DartRoom Created",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = DartRoom.class))),
                    @APIResponse(
                            responseCode = "400",
                            description = "DartRoom already exists for Id",
                            content = @Content(mediaType = "application/json")),
            }
    )
    @RolesAllowed("ADMIN")
    public Response post(@Valid DartRoom dartRoom) {
        logger.info("Method post() called with argument: {}", dartRoom);

        final DartRoom saved = dartRoomService.save(dartRoom);
        Response response = Response.status(Response.Status.CREATED).entity(saved).build();

        logger.info("Built response: {}", response);

        return response;
    }

    @PUT
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "DartRoom updated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = DartRoom.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No DartRoom found for athleticsTrackId provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    @RolesAllowed("ADMIN")
    public Response put(@Valid DartRoom dartRoom) {
        logger.info("Method put() called with argument: {}", dartRoom);

        final DartRoom saved = dartRoomService.update(dartRoom);
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
                            description = "DartRoom updated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = DartRoom.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No DartRoom found for Id provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    @RolesAllowed("ADMIN")
    public Response putEquipmentToObject(@PathParam("sportObjectId") Integer sportObjectId,
                                         @PathParam("rentEquipmentId") Integer rentEquipmentId) {
        logger.info("Method putEquipmentToObject() called with arguments: {}, {}", sportObjectId, rentEquipmentId);

        final DartRoom saved = dartRoomService.putEquipmentToObject(sportObjectId, rentEquipmentId);
        Response response = Response.ok(saved).build();

        logger.info("Built response: {}", response);

        return response;
    }

}
