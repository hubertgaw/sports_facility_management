package pl.lodz.hubertgaw.service.exception;

import pl.lodz.hubertgaw.service.exception.core.*;

public class BookingException extends BaseException {

    public BookingException (BaseException e) {
        super(e, e.getErrorCode());
    }

    public static BookingException bookingNotFoundException() {
        return new BookingException(NotFoundException.notFound("Booking for given id not found!"));
    }

    public static BookingException bookingForUserNotFoundException() {
        return new BookingException(ForbiddenException.forbidden("Booking for given user not found!"));
    }

    public static BookingException bookingEmptyIdException() {
        return new BookingException(
                EmptyPropertyException.emptyProperty("Id for updating booking cannot be null"));
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

    // the following exceptions cannot be done as bean validation, because in role: user these fields are not used and
    // in other roles they cannot be null.
    public static BookingException bookingEmptyEmailException() {
        return new BookingException(
                EmptyPropertyException.emptyProperty("Email for user in booking cannot be null"));
    }

    public static BookingException bookingWrongEmailFormatException() {
        return new BookingException(
                WrongFormatException.wrongFormat("Email must be in proper format"));
    }

    public static BookingException bookingEmptyFirstNameException() {
        return new BookingException(
                EmptyPropertyException.emptyProperty("First name for user in booking cannot be null"));
    }

    public static BookingException bookingEmptyLastNameException() {
        return new BookingException(
                EmptyPropertyException.emptyProperty("Last name for user in booking cannot be null"));
    }


}
