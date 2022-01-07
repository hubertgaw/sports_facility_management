package pl.lodz.hubertgaw.service.exception;

import pl.lodz.hubertgaw.service.exception.core.BaseException;
import pl.lodz.hubertgaw.service.exception.core.EmptyPropertyException;
import pl.lodz.hubertgaw.service.exception.core.NotFoundException;

public class ClimbingWallException extends BaseException {

    public ClimbingWallException(BaseException e) {
        super(e, e.getErrorCode());
    }

    public static ClimbingWallException climbingWallNotFoundException() {
        return new ClimbingWallException(
                NotFoundException.notFound(
                        "Climbing wall for given id not found. Try to search in all sport objects or change the id."));
    }

    public static ClimbingWallException climbingWallEmptyIdException() {
        return new ClimbingWallException(
                EmptyPropertyException.emptyId("Id for updating climbing wall cannot be null"));
    }
}
