package pl.lodz.hubertgaw.resource;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.RentEquipment;
import pl.lodz.hubertgaw.service.RentEquipmentService;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/rent_equipments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RentEquipmentResource {

    private final RentEquipmentService rentEquipmentService;
    private final Logger logger;

    public RentEquipmentResource(RentEquipmentService rentEquipmentService, Logger logger) {
        this.rentEquipmentService = rentEquipmentService;
        this.logger = logger;

        logger.info("Constructor RentEquipmentResource called");
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
    @PermitAll
    public Response get() {
        logger.info("Method get() called");

        Response response = Response.ok(rentEquipmentService.findAll()).build();

        logger.info("Built response: {}", response);

        return response;
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
    @PermitAll
    public Response getById(@PathParam("rentEquipmentId") Integer rentEquipmentId) {
        logger.info("Method getById() called with argument: {}", rentEquipmentId);

//        Optional<RentEquipment> optional =
        Response response = Response.ok(rentEquipmentService.findById(rentEquipmentId)).build();

        logger.info("Built response: {}", response);

        return response;
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
    @RolesAllowed("ADMIN")
    public Response post(@Valid RentEquipment rentEquipment) {
        logger.info("Method post() called with argument: {}", rentEquipment);

        final RentEquipment saved = rentEquipmentService.save(rentEquipment);
        Response response = Response.status(Response.Status.CREATED).entity(saved).build();

        logger.info("Built response: {}", response);

        return response;
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
    @RolesAllowed("ADMIN")
    public Response put(@Valid RentEquipment rentEquipment) {
        logger.info("Method put() called with argument: {}", rentEquipment);

        final RentEquipment saved = rentEquipmentService.update(rentEquipment);
        Response response = Response.ok(saved).build();

        logger.info("Built response: {}", response);

        return response;
    }

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
    @RolesAllowed("ADMIN")
    public Response deleteRentEquipment(@PathParam("rentEquipmentId") Integer rentEquipmentId) {
        logger.info("Method deleteRentEquipment() called with argument: {}", rentEquipmentId);

        rentEquipmentService.deleteRentEquipmentById(rentEquipmentId);
        Response response = Response.noContent().build();

        logger.info("Built response: {}", response);

        return response;

    }

}
