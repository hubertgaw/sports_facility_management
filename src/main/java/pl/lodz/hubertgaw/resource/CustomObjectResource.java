package pl.lodz.hubertgaw.resource;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.CustomObject;
import pl.lodz.hubertgaw.service.CustomObjectService;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
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

        logger.info("Constructor CustomObjectResource called");

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
    @PermitAll
    public Response get() {
        logger.info("Method get() called");

        Response response = Response.ok(customObjectService.findAll()).build();

        logger.info("Built response: {}", response);

        return response;
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
    @PermitAll
    public Response getById(@PathParam("sportObjectId") Integer sportObjectId) {
        logger.info("Method getById() called with argument: {}", sportObjectId);

        Response response = Response.ok(customObjectService.findById(sportObjectId)).build();

        logger.info("Built response: {}", response);

        return response;
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
    @PermitAll
    public Response getByType(@PathParam("customObjectType") String customObjectType) {
        logger.info("Method getByType() called with argument: {}", customObjectType);

        Response response = Response.ok(customObjectService.findByType(customObjectType)).build();

        logger.info("Built response: {}", response);

        return response;
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
    @RolesAllowed("ADMIN")
    public Response post(@Valid CustomObject customObject) {
        logger.info("Method post() called with argument: {}", customObject);

        final CustomObject saved = customObjectService.save(customObject);
        Response response = Response.status(Response.Status.CREATED).entity(saved).build();

        logger.info("Built response: {}", response);

        return response;
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
                            description = "No CustomObject found for Id provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    @RolesAllowed("ADMIN")
    public Response put(@Valid CustomObject customObject) {
        logger.info("Method put() called with argument: {}", customObject);

        final CustomObject saved = customObjectService.update(customObject);
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
                            description = "CustomObject updated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = CustomObject.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No CustomObject found for Id provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    @RolesAllowed("ADMIN")
    public Response putEquipmentToObject(@PathParam("sportObjectId") Integer sportObjectId,
                                         @PathParam("rentEquipmentId") Integer rentEquipmentId) {
        logger.info("Method putEquipmentToObject() called with arguments: {}, {}", sportObjectId, rentEquipmentId);

        final CustomObject saved = customObjectService.putEquipmentToObject(sportObjectId, rentEquipmentId);
        Response response = Response.ok(saved).build();

        logger.info("Built response: {}", response);

        return response;
    }


}
