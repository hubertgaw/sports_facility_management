package pl.lodz.hubertgaw.service.exception;

import pl.lodz.hubertgaw.service.exception.core.BaseException;
import pl.lodz.hubertgaw.service.exception.core.EmptyPropertyException;
import pl.lodz.hubertgaw.service.exception.core.NotFoundException;

public class SportsHallException extends BaseException {

    public SportsHallException(BaseException e) {
        super(e, e.getErrorCode());
    }

    public static SportsHallException sportsHallNotFoundException() {
        return new SportsHallException(
                NotFoundException.notFound(
                        "Sports hall for given id not found. Try to search in all sport objects or change the id."));
    }

    public static SportsHallException sportsHallEmptyIdException() {
        return new SportsHallException(
                EmptyPropertyException.emptyProperty("Id for updating sports hall cannot be null"));
    }
}
