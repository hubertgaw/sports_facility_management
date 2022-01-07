package pl.lodz.hubertgaw.service.exception;

import pl.lodz.hubertgaw.service.exception.core.BaseException;
import pl.lodz.hubertgaw.service.exception.core.EmptyPropertyException;
import pl.lodz.hubertgaw.service.exception.core.NotFoundException;

public class GymException extends BaseException {

    public GymException(BaseException e) {
        super(e, e.getErrorCode());
    }

    public static GymException gymNotFoundException() {
        return new GymException(
                NotFoundException.notFound(
                        "Gym for given id not found. Try to search in all sport objects or change the id."));
    }

    public static GymException gymEmptyIdException() {
        return new GymException(
                EmptyPropertyException.emptyId("Id for updating gym cannot be null"));
    }
}
