package pl.lodz.hubertgaw.resource;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.Booking;
import pl.lodz.hubertgaw.dto.RentEquipment;
import pl.lodz.hubertgaw.dto.User;
import pl.lodz.hubertgaw.repository.entity.UserEntity;
import pl.lodz.hubertgaw.service.BookingService;
import pl.lodz.hubertgaw.service.UserService;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.*;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

@Path("/api/bookings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookingResource {

    private final BookingService bookingService;
    private final UserService userService;
    private final Logger logger;

    public BookingResource(BookingService bookingService, UserService userService, Logger logger) {
        this.bookingService = bookingService;
        this.logger = logger;
        this.userService = userService;

        logger.info("Constructor BookingResource called");
    }

    @GET
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Get all bookings",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.ARRAY, implementation = Booking.class)))
            }
    )
    @RolesAllowed("ADMIN")
    public Response get() {
        logger.info("Method get() called");

        Response response = Response.ok(bookingService.findAll()).build();

        logger.info("Built response: {}", response);

        return response;
    }

    @GET
    @Path("/{bookingId}")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Get Booking by id",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = Booking.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No Booking found for id provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    @RolesAllowed("ADMIN")
    public Response getById(@PathParam("bookingId") Integer bookingId) {
        logger.info("Method getById() called with argument: {}", bookingId);

//        Optional<RentEquipment> optional =
        Response response = Response.ok(bookingService.findById(bookingId)).build();

        logger.info("Built response: {}", response);

        return response;
    }


    @GET
    @Path("/mine")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Get logged user's bookings",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = User.class)))
            }
    )
    @RolesAllowed("USER")
    public Response getLoggedUserBookings(@Context SecurityContext userContext) {
        Integer loggedUserId = userService.findByEmail(userContext.getUserPrincipal().getName()).getId();
        Response response = Response.ok(bookingService.findByUserId(loggedUserId)).build();

        logger.info("Built response: {}", response);

        return response;
    }

    @POST
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "201",
                            description = "Booking Created",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = Booking.class))),
                    @APIResponse(
                            responseCode = "400",
                            description = "Booking already exists for given id",
                            content = @Content(mediaType = "application/json")),
            }
    )
    @PermitAll
    public Response post(@Valid Booking booking, @Context SecurityContext userContext) {
        logger.info("Method post() called with arguments: {}, {}", booking, userContext);
//        Principal user = userContext.getUserPrincipal();
        final Booking saved = bookingService.save(booking, userContext);
        Response response = Response.status(Response.Status.CREATED).entity(saved).build();

        logger.info("Built response: {}", response);

        return response;
    }

    @PUT
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Booking updated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = Booking.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No Booking found for bookingId provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    @RolesAllowed({"ADMIN","USER"})
    public Response put(@Context SecurityContext userContext, @Valid Booking booking) {
        logger.info("Method put() called with arguments: {}, {}", userContext, booking);

        final Booking saved = bookingService.update(booking, userContext);
        Response response = Response.ok(saved).build();

        logger.info("Built response: {}", response);

        return response;
    }

    @DELETE
    @Path("{bookingId}")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "204",
                            description = "Booking by id deleted",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = SchemaType.OBJECT, implementation = RentEquipment.class))),
                    @APIResponse(
                            responseCode = "404",
                            description = "No Booking found for id provided",
                            content = @Content(mediaType = "application/json")),
            }
    )
    @RolesAllowed({"USER", "ADMIN"})
    public Response deleteBooking(@Context SecurityContext userContext, @PathParam("bookingId") Integer bookingId) {
        logger.info("Method deleteBooking() called with arguments: {}, {}", userContext, bookingId);

        bookingService.deleteBookingById(bookingId, userContext);
        Response response = Response.noContent().build();

        logger.info("Built response: {}", response);

        return response;

    }

}
