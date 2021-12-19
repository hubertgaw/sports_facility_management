package pl.lodz.hubertgaw.resource;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.SportObject;
import pl.lodz.hubertgaw.dto.SportsHall;
import pl.lodz.hubertgaw.service.SportObjectService;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Path("/api/sport_objects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SportObjectResource {

    private final SportObjectService sportObjectService;
    private final Logger logger;

    public SportObjectResource(SportObjectService sportObjectService, Logger logger) {
        this.sportObjectService = sportObjectService;
        this.logger = logger;
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
    public Response get() {
        return Response.ok(sportObjectService.findAll()).build();
    }

    @GET
    @Path("/{sportObjectId}")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Get SportObject by id",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = SportsHall.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No SportObject found for id provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    public Response getById(@PathParam("sportObjectId") Integer sportObjectId) {
        Optional<SportObject> optional = sportObjectService.findById(sportObjectId);
        return !optional.isEmpty() ? Response.ok(optional.get()).build() : Response.status(Response.Status.NOT_FOUND).build();
    }

//    @PUT
//    @APIResponses(
//            value = {
//                    @APIResponse(
//                            responseCode = "200",
//                            description = "SportObject updated",
//                            content = @Content(mediaType = "application/json",
//                                    schema = @Schema(type = SchemaType.OBJECT, implementation = SportObject.class))),
//                    @APIResponse(
//                            responseCode = "404",
//                            description = "No SportObject found for sportObjectId provided",
//                            content = @Content(mediaType = "application/json")),
//            }
//    )
//    public Response put(@Valid SportObject sportObject) {
//        final SportsHall saved = sportObjectService.update(sportObject);
//        return Response.ok(saved).build();
//    }

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
    public Response putEquipmentToObject(@PathParam("sportObjectId") Integer sportObjectId,
                                         @PathParam("rentEquipmentId") Integer rentEquipmentId) {
        final SportObject saved = sportObjectService.putEquipmentToObject(sportObjectId, rentEquipmentId);
        return Response.ok(saved).build();
    }

}
