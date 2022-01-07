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
import java.util.Optional;
import javax.ws.rs.core.Response;

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
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = SportObject.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No SportObject found for id provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    public Response getById(@PathParam("sportObjectId") Integer sportObjectId) {
        return Response.ok(sportObjectService.findById(sportObjectId)).build();
//        return !optional.isEmpty() ? Response.ok(optional.get()).build() : Response.status(Response.Status.NOT_FOUND).build();
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
        //jeśli już koszytsam z "ręcznego" return to mozna ustawić zwracany language/header poprzez wywołanie po ok().
        return Response.ok(saved).build();
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
    public Response deleteSportObject(@PathParam("sportObjectId") Integer sportObjectId) {
        sportObjectService.deleteSportObjectById(sportObjectId);
//        return Response.noContent().build();
//        bez tego return byłoby to samo - jax-rs automatycznie ogarnia podstawowe responsy
        return Response.noContent().build();

    }
}
