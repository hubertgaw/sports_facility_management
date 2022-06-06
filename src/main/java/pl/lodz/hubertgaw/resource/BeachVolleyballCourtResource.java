package pl.lodz.hubertgaw.resource;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.BeachVolleyballCourt;
import pl.lodz.hubertgaw.service.BeachVolleyballCourtService;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/beach_volleyball_courts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BeachVolleyballCourtResource {

    private final BeachVolleyballCourtService beachVolleyballCourtService;
    private final Logger logger;

    public BeachVolleyballCourtResource(BeachVolleyballCourtService beachVolleyballCourtService, Logger logger) {
        this.beachVolleyballCourtService = beachVolleyballCourtService;
        this.logger = logger;

        logger.info("Constructor BeachVolleyballCourtResource called");
    }

    @GET
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Get all Beach Volleyball Courts",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.ARRAY, implementation = BeachVolleyballCourt.class)))
            }
    )
    @PermitAll
    public Response get() {
        logger.info("Method get() called");

        Response response = Response.ok(beachVolleyballCourtService.findAll()).build();

        logger.info("Built response: {}", response);

        return response;
    }

    @GET
    @Path("/{sportObjectId}")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Get Beach Volleyball Court by id",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = BeachVolleyballCourt.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No Beach Volleyball Court found for Id provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    @PermitAll
    public Response getById(@PathParam("sportObjectId") Integer sportObjectId) {
        logger.info("Method getById() called with argument: {}", sportObjectId);

        Response response = Response.ok(beachVolleyballCourtService.findById(sportObjectId)).build();

        logger.info("Built response: {}", response);

        return response;
    }

    @POST
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "201",
                            description = "Beach Volleyball Court Created",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = BeachVolleyballCourt.class))),
                    @APIResponse(
                            responseCode = "400",
                            description = "Beach Volleyball Court already exists for Id",
                            content = @Content(mediaType = "application/json")),
            }
    )
    @RolesAllowed("ADMIN")
    public Response post(@Valid BeachVolleyballCourt beachVolleyballCourt) {
        logger.info("Method post() called with argument: {}", beachVolleyballCourt);

        final BeachVolleyballCourt saved = beachVolleyballCourtService.save(beachVolleyballCourt);
        Response response = Response.status(Response.Status.CREATED).entity(saved).build();

        logger.info("Built response: {}", response);

        return response;
    }

    @PUT
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Beach Volleyball Court updated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = BeachVolleyballCourt.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No Beach Volleyball Court found for Id provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    @RolesAllowed("ADMIN")
    public Response put(@Valid BeachVolleyballCourt beachVolleyballCourt) {
        logger.info("Method put() called with argument: {}", beachVolleyballCourt);

        final BeachVolleyballCourt saved = beachVolleyballCourtService.update(beachVolleyballCourt);
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
                            description = "BeachVolleyballCourt updated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = BeachVolleyballCourt.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No BeachVolleyballCourt found for Id provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    @RolesAllowed("ADMIN")
    public Response putEquipmentToObject(@PathParam("sportObjectId") Integer sportObjectId,
                                         @PathParam("rentEquipmentId") Integer rentEquipmentId) {
        logger.info("Method putEquipmentToObject() called with arguments: {}, {}", sportObjectId, rentEquipmentId);

        final BeachVolleyballCourt saved = beachVolleyballCourtService.putEquipmentToObject(sportObjectId, rentEquipmentId);
        Response response = Response.ok(saved).build();

        logger.info("Built response: {}", response);

        return response;
    }

}
