package pl.lodz.hubertgaw.resource;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.BeachVolleyballCourt;
import pl.lodz.hubertgaw.service.BeachVolleyballCourtService;

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
    public Response get() {
        return Response.ok(beachVolleyballCourtService.findAll()).build();
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
                            description = "No Beach Volleyball Court found for id provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    public Response getById(@PathParam("sportObjectId") Integer sportObjectId) {
        return Response.ok(beachVolleyballCourtService.findById(sportObjectId)).build();
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
    public Response post(@Valid BeachVolleyballCourt beachVolleyballCourt) {
        logger.info("post");
        final BeachVolleyballCourt saved = beachVolleyballCourtService.save(beachVolleyballCourt);
        return Response.status(Response.Status.CREATED).entity(saved).build();
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
                            description = "No Beach Volleyball Court found for athleticsTrackId provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    public Response put(@Valid BeachVolleyballCourt beachVolleyballCourt) {
        final BeachVolleyballCourt saved = beachVolleyballCourtService.update(beachVolleyballCourt);
        return Response.ok(saved).build();
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
    public Response putEquipmentToObject(@PathParam("sportObjectId") Integer sportObjectId,
                                         @PathParam("rentEquipmentId") Integer rentEquipmentId) {
        final BeachVolleyballCourt saved = beachVolleyballCourtService.putEquipmentToObject(sportObjectId, rentEquipmentId);
        return Response.ok(saved).build();
    }

}
