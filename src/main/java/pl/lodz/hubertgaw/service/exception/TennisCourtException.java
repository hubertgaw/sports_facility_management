package pl.lodz.hubertgaw.service.exception;

import pl.lodz.hubertgaw.service.exception.core.BaseException;
import pl.lodz.hubertgaw.service.exception.core.EmptyPropertyException;
import pl.lodz.hubertgaw.service.exception.core.NotFoundException;

public class TennisCourtException extends BaseException {

    public TennisCourtException(BaseException e) {
        super(e, e.getErrorCode());
    }

    public static TennisCourtException tennisCourtNotFoundException() {
        return new TennisCourtException(
                NotFoundException.notFound(
                        "Tennis court for given id not found. Try to search in all sport objects or change the id."));
    }

    public static TennisCourtException tennisCourtEmptyIdException() {
        return new TennisCourtException(
                EmptyPropertyException.emptyProperty("Id for updating tennis court cannot be null"));
    }
}
