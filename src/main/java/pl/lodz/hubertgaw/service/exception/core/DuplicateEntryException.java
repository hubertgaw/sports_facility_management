package pl.lodz.hubertgaw.service.exception.core;

public class DuplicateEntryException extends BaseException {

    public DuplicateEntryException(String message) {
        super(message, 403);
    }

    public static DuplicateEntryException duplicateName(String message) {
        return new DuplicateEntryException(message);
    }

    public static DuplicateEntryException duplicateBooking (String message) {
        return new DuplicateEntryException(message);
    }
}
