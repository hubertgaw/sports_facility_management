package pl.lodz.hubertgaw.service.exception;

import pl.lodz.hubertgaw.service.exception.core.*;

public class BookingException extends BaseException {

    public BookingException (BaseException e) {
        super(e, e.getErrorCode());
    }

    public static BookingException bookingNotFoundException() {
        return new BookingException(NotFoundException.notFound("Booking for given id not found!"));
    }

    public static BookingException bookingEmptyIdException() {
        return new BookingException(
                EmptyPropertyException.emptyId("Id for updating booking cannot be null"));
    }

    public static BookingException rentEquipmentForSportObjectNotFoundException() {
        return new BookingException(
                NotFoundException.notFound("Rent equipment with specified name not found in booked sport object"));
    }

    public static BookingException wrongDatePastException() {
        return new BookingException(WrongFormatException.wrongFormat("Date cannot be a past value"));
    }

    public static BookingException wrongDateTimeException() {
        return new BookingException(
                WrongFormatException.wrongFormat("Time in the date must be a full hour or half past hour"));
    }

    public static BookingException duplicateBookingTimeException() {
        return new BookingException(
                DuplicateEntryException.duplicateBooking("There is already booking for this object in given time"));
    }

    public static BookingException invalidNumberOfPlacesForObject() {
        return new BookingException(
                WrongFormatException.invalidField("Field number of places cannot be applicable to provided object"));
    }

    public static BookingException invalidHalfRentedForObject() {
        return new BookingException(
                WrongFormatException.invalidField("Field half rented cannot be applicable to provided object"));
    }

    public static BookingException numberOfPlacesHalfRentExcludeException() {
        return new BookingException(
                WrongFormatException.invalidField("Number of people cannot be provided along with half rent"));
    }
}
