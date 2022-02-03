package pl.lodz.hubertgaw.resource;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.CustomObject;
import pl.lodz.hubertgaw.service.CustomObjectService;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/custom_objects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomObjectResource {

    private final CustomObjectService customObjectService;
    private final Logger logger;

    public CustomObjectResource(CustomObjectService CustomObjectService, Logger logger) {
        this.customObjectService = CustomObjectService;
        this.logger = logger;
    }

    @GET
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Get all CustomObjects",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.ARRAY, implementation = CustomObject.class)))
            }
    )
    public Response get() {
        return Response.ok(customObjectService.findAll()).build();
    }

    @GET
    @Path("/{sportObjectId}")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Get CustomObject by id",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = CustomObject.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No CustomObject found for id provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    public Response getById(@PathParam("sportObjectId") Integer sportObjectId) {
        return Response.ok(customObjectService.findById(sportObjectId)).build();
    }

    @GET
    @Path("/type/{customObjectType}")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Get CustomObjects by type",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = CustomObject.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No CustomObject found for type provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    public Response getByType(@PathParam("customObjectType") String customObjectType) {
        return Response.ok(customObjectService.findByType(customObjectType)).build();
    }


    @POST
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "201",
                            description = "CustomObject Created",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = CustomObject.class))),
                    @APIResponse(
                            responseCode = "400",
                            description = "CustomObject already exists for Id",
                            content = @Content(mediaType = "application/json")),
            }
    )
    public Response post(@Valid CustomObject CustomObject) {
        logger.info("post");
        final CustomObject saved = customObjectService.save(CustomObject);
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }

    @PUT
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "CustomObject updated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = CustomObject.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No CustomObject found for athleticsTrackId provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    public Response put(@Valid CustomObject CustomObject) {
        final CustomObject saved = customObjectService.update(CustomObject);
        return Response.ok(saved).build();
    }

    @PUT
    @Path("/{sportObjectId}/rent_equipment/{rentEquipmentId}")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "CustomObject updated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = CustomObject.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No CustomObject found for Id provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    public Response putEquipmentToObject(@PathParam("sportObjectId") Integer sportObjectId,
                                         @PathParam("rentEquipmentId") Integer rentEquipmentId) {
        final CustomObject saved = customObjectService.putEquipmentToObject(sportObjectId, rentEquipmentId);
        return Response.ok(saved).build();
    }


}
