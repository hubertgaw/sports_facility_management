package pl.lodz.hubertgaw.service.exception;

import pl.lodz.hubertgaw.service.exception.core.BaseException;
import pl.lodz.hubertgaw.service.exception.core.EmptyPropertyException;
import pl.lodz.hubertgaw.service.exception.core.NotFoundException;

public class SmallPitchException extends BaseException {

    public SmallPitchException(BaseException e) {
        super(e, e.getErrorCode());
    }

    public static SmallPitchException smallPitchNotFoundException() {
        return new SmallPitchException(
                NotFoundException.notFound(
                        "Small pitch for given id not found. Try to search in all sport objects or change the id."));
    }

    public static SmallPitchException smallPitchEmptyIdException() {
        return new SmallPitchException(
                EmptyPropertyException.emptyId("Id for updating small pitch cannot be null"));
    }

}
