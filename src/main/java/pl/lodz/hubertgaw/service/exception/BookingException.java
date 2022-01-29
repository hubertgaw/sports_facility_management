package pl.lodz.hubertgaw.service.exception;

import pl.lodz.hubertgaw.service.exception.core.BaseException;
import pl.lodz.hubertgaw.service.exception.core.EmptyPropertyException;
import pl.lodz.hubertgaw.service.exception.core.NotFoundException;
import pl.lodz.hubertgaw.service.exception.core.WrongFormatException;

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
}
