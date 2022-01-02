package pl.lodz.hubertgaw.resource;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.AthleticsTrack;
import pl.lodz.hubertgaw.dto.RentEquipment;
import pl.lodz.hubertgaw.dto.SportObject;
import pl.lodz.hubertgaw.service.AthleticsTrackService;
import pl.lodz.hubertgaw.service.RentEquipmentService;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Path("/api/rent_equipments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RentEquipmentResource {

    private final RentEquipmentService rentEquipmentService;
    private final Logger logger;

    public RentEquipmentResource(RentEquipmentService rentEquipmentService, Logger logger) {
        this.rentEquipmentService = rentEquipmentService;
        this.logger = logger;
    }

    @GET
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Get all Rent Equipments",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.ARRAY, implementation = RentEquipment.class)))
            }
    )
    public Response get() {
        return Response.ok(rentEquipmentService.findAll()).build();
    }

    @GET
    @Path("/{rentEquipmentId}")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Get Rent Equipment by id",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = RentEquipment.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No Rent Equipment found for id provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    public Response getById(@PathParam("rentEquipmentId") Integer rentEquipmentId) {
        Optional<RentEquipment> optional = rentEquipmentService.findById(rentEquipmentId);
        return !optional.isEmpty() ? Response.ok(optional.get()).build() : Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "201",
                            description = "Rent Equipment Created",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = RentEquipment.class))),
                    @APIResponse(
                            responseCode = "400",
                            description = "Rent Equipment already exists for rentEquipmentId",
                            content = @Content(mediaType = "application/json")),
            }
    )
    public Response post(@Valid RentEquipment rentEquipment) {
        logger.info("post");
        final RentEquipment saved = rentEquipmentService.save(rentEquipment);
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }

    @PUT
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Rent Equipment updated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = RentEquipment.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No Rent Equipment found for rentEquipmentId provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    public Response put(@Valid RentEquipment rentEquipment) {
        final RentEquipment saved = rentEquipmentService.update(rentEquipment);
        return Response.ok(saved).build();
    }

//    @PUT
//    @Path("/{rentEquipmentId}/sport_object/{sportObjectId}")
//    @APIResponses(
//            value = {
//                    @APIResponse(
//                            responseCode = "200",
//                            description = "AthleticsTrack updated",
//                            content = @Content(mediaType = "application/json",
//                                    schema = @Schema(type = SchemaType.OBJECT, implementation = AthleticsTrack.class))),
//                    @APIResponse(
//                            responseCode = "404",
//                            description = "No AthleticsTrack found for athleticsTrackId provided",
//                            content = @Content(mediaType = "application/json")),
//            }
//    )
//    public Response putEquipmentToObject(@PathParam("rentEquipmentId") Integer rentEquipmentId,
//                                         @PathParam("sportObjectId") Integer sportObjectId) {
//        final AthleticsTrack saved = rentEquipmentService.update(athleticsTrack);
//        return Response.ok(saved).build();
//    }

    @DELETE
    @Path("{rentEquipmentId}")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "204",
                            description = "RentEquipment by id deleted",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = RentEquipment.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No RentEquipment found for id provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    public Response deleteRentEquipment(@PathParam("rentEquipmentId") Integer rentEquipmentId) {
        rentEquipmentService.deleteRentEquipmentById(rentEquipmentId);
//        return Response.noContent().build();
        return Response.noContent().build();

    }

}
