package pl.lodz.hubertgaw.service.exception;

import pl.lodz.hubertgaw.service.exception.core.BaseException;
import pl.lodz.hubertgaw.service.exception.core.EmptyPropertyException;
import pl.lodz.hubertgaw.service.exception.core.NotFoundException;

public class SportSwimmingPoolException extends BaseException {

    public SportSwimmingPoolException(BaseException e) {
        super(e, e.getErrorCode());
    }

    public static SportSwimmingPoolException sportSwimmingPoolNotFoundException() {
        return new SportSwimmingPoolException(
                NotFoundException.notFound(
                        "Sport swimming pool for given id not found. Try to search in all sport objects or change the id."));
    }

    public static SportSwimmingPoolException sportSwimmingPoolEmptyIdException() {
        return new SportSwimmingPoolException(
                EmptyPropertyException.emptyProperty("Id for updating sport swimming pool cannot be null"));
    }
}
