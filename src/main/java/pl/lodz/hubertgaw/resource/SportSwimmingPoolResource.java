package pl.lodz.hubertgaw.resource;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.SportSwimmingPool;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Path("/api/sport_swimming_pools")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SportSwimmingPoolResource {

    private final pl.lodz.hubertgaw.service.SportSwimmingPoolService SportSwimmingPoolService;
    private final Logger logger;

    public SportSwimmingPoolResource(pl.lodz.hubertgaw.service.SportSwimmingPoolService SportSwimmingPoolService, Logger logger) {
        this.SportSwimmingPoolService = SportSwimmingPoolService;
        this.logger = logger;
    }

    @GET
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Get all SportSwimmingPools",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.ARRAY, implementation = SportSwimmingPool.class)))
            }
    )
    public Response get() {
        return Response.ok(SportSwimmingPoolService.findAll()).build();
    }

    @GET
    @Path("/{sportObjectId}")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Get SportSwimmingPool by id",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = SportSwimmingPool.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No SportSwimmingPool found for id provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    public Response getById(@PathParam("sportObjectId") Integer sportObjectId) {
        Optional<SportSwimmingPool> optional = SportSwimmingPoolService.findById(sportObjectId);
        return !optional.isEmpty() ? Response.ok(optional.get()).build() : Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "201",
                            description = "SportSwimmingPool Created",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = SportSwimmingPool.class))),
                    @APIResponse(
                            responseCode = "400",
                            description = "SportSwimmingPool already exists for Id",
                            content = @Content(mediaType = "application/json")),
            }
    )
    public Response post(@Valid SportSwimmingPool SportSwimmingPool) {
        logger.info("post");
        final SportSwimmingPool saved = SportSwimmingPoolService.save(SportSwimmingPool);
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }

    @PUT
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "SportSwimmingPool updated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = SportSwimmingPool.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No SportSwimmingPool found for athleticsTrackId provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    public Response put(@Valid SportSwimmingPool SportSwimmingPool) {
        final SportSwimmingPool saved = SportSwimmingPoolService.update(SportSwimmingPool);
        return Response.ok(saved).build();
    }

    @PUT
    @Path("/{sportObjectId}/rent_equipment/{rentEquipmentId}")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "SportSwimmingPool updated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = SportSwimmingPool.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No SportSwimmingPool found for Id provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    public Response putEquipmentToObject(@PathParam("sportObjectId") Integer sportObjectId,
                                         @PathParam("rentEquipmentId") Integer rentEquipmentId) {
        final SportSwimmingPool saved = SportSwimmingPoolService.putEquipmentToObject(sportObjectId, rentEquipmentId);
        return Response.ok(saved).build();
    }


}