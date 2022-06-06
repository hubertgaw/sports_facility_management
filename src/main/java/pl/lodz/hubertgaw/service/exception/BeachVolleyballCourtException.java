package pl.lodz.hubertgaw.service.exception;

import pl.lodz.hubertgaw.service.exception.core.BaseException;
import pl.lodz.hubertgaw.service.exception.core.EmptyPropertyException;
import pl.lodz.hubertgaw.service.exception.core.NotFoundException;

public class BeachVolleyballCourtException extends BaseException {

    public BeachVolleyballCourtException(BaseException e) {
        super(e, e.getErrorCode());
    }

    public static BeachVolleyballCourtException beachVolleyballCourtNotFoundException() {
        return new BeachVolleyballCourtException(
                NotFoundException.notFound(
                        "Beach volleyball court for given id not found. Try to search in all sport objects or change the id."));
    }

    public static BeachVolleyballCourtException beachVolleyballCourtEmptyIdException() {
        return new BeachVolleyballCourtException(
                EmptyPropertyException.emptyProperty("Id for updating beach volleyball court cannot be null"));

    }
}
