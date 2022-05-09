package pl.lodz.hubertgaw.service.exception;

import pl.lodz.hubertgaw.service.exception.core.BaseException;
import pl.lodz.hubertgaw.service.exception.core.EmptyPropertyException;
import pl.lodz.hubertgaw.service.exception.core.NotFoundException;
import pl.lodz.hubertgaw.service.exception.core.WrongFormatException;

public class CustomObjectException extends BaseException {

    public CustomObjectException(BaseException e) {
        super(e, e.getErrorCode());
    }

    public static CustomObjectException customObjectNotFoundException() {
        return new CustomObjectException(
                NotFoundException.notFound(
                        "Custom object for given id not found. Try to search in all sport objects or change the id."));
    }

    public static CustomObjectException customObjectEmptyIdException() {
        return new CustomObjectException(
                EmptyPropertyException.emptyProperty("Id for updating custom object cannot be null"));
    }

    public static CustomObjectException customObjectTypeNotFoundException() {
        return new CustomObjectException(
                NotFoundException.notFound("No custom object was found for provided type"));
    }

    public static CustomObjectException customObjectWrongTypeFormatException() {
        return new CustomObjectException(
                WrongFormatException.wrongFormat("Type can contains only letters and digits (no space allowed)"));
    }
}
