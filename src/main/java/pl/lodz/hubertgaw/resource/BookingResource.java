package pl.lodz.hubertgaw.resource;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.Booking;
import pl.lodz.hubertgaw.dto.RentEquipment;
import pl.lodz.hubertgaw.service.BookingService;

import javax.validation.Valid;
import javax.ws.rs.*;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/bookings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookingResource {

    private final BookingService bookingService;
    private final Logger logger;

    public BookingResource(BookingService bookingService, Logger logger) {
        this.bookingService = bookingService;
        this.logger = logger;
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
    public Response get() {
        return Response.ok(bookingService.findAll()).build();
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
    public Response getById(@PathParam("bookingId") Integer bookingId) {
//        Optional<RentEquipment> optional =
        return Response.ok(bookingService.findById(bookingId)).build();
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
    public Response post(@Valid Booking booking) {
        logger.info("post");
        final Booking saved = bookingService.save(booking);
        return Response.status(Response.Status.CREATED).entity(saved).build();
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
    public Response put(@Valid Booking booking) {
        final Booking saved = bookingService.update(booking);
        return Response.ok(saved).build();
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
    public Response deleteBooking(@PathParam("bookingId") Integer bookingId) {
        bookingService.deleteBookingById(bookingId);
        return Response.noContent().build();

    }
}
