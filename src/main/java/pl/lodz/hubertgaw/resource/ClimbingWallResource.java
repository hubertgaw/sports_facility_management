package pl.lodz.hubertgaw.resource;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.ClimbingWall;
import pl.lodz.hubertgaw.service.ClimbingWallService;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;


@Path("/api/climbing_walls")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClimbingWallResource {

    private final ClimbingWallService climbingWallService;
    private final Logger logger;

    public ClimbingWallResource(ClimbingWallService ClimbingWallService, Logger logger) {
        this.climbingWallService = ClimbingWallService;
        this.logger = logger;
    }

    @GET
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Get all ClimbingWalls",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.ARRAY, implementation = ClimbingWall.class)))
            }
    )
    public Response get() {
        return Response.ok(climbingWallService.findAll()).build();
    }

    @GET
    @Path("/{sportObjectId}")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Get ClimbingWall by id",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = ClimbingWall.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No ClimbingWall found for id provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    public Response getById(@PathParam("sportObjectId") Integer sportObjectId) {
        return Response.ok(climbingWallService.findById(sportObjectId)).build();
    }

    @POST
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "201",
                            description = "ClimbingWall Created",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = ClimbingWall.class))),
                    @APIResponse(
                            responseCode = "400",
                            description = "ClimbingWall already exists for Id",
                            content = @Content(mediaType = "application/json")),
            }
    )
    public Response post(@Valid ClimbingWall ClimbingWall) {
        logger.info("post");
        final ClimbingWall saved = climbingWallService.save(ClimbingWall);
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }

    @PUT
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "ClimbingWall updated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = ClimbingWall.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No ClimbingWall found for athleticsTrackId provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    public Response put(@Valid ClimbingWall ClimbingWall) {
        final ClimbingWall saved = climbingWallService.update(ClimbingWall);
        return Response.ok(saved).build();
    }

    @PUT
    @Path("/{sportObjectId}/rent_equipment/{rentEquipmentId}")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "ClimbingWall updated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = ClimbingWall.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No ClimbingWall found for Id provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    public Response putEquipmentToObject(@PathParam("sportObjectId") Integer sportObjectId,
                                         @PathParam("rentEquipmentId") Integer rentEquipmentId) {
        final ClimbingWall saved = climbingWallService.putEquipmentToObject(sportObjectId, rentEquipmentId);
        return Response.ok(saved).build();
    }

}
