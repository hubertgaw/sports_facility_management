package pl.lodz.hubertgaw.service.exception;

import pl.lodz.hubertgaw.service.exception.core.BaseException;
import pl.lodz.hubertgaw.service.exception.core.EmptyPropertyException;
import pl.lodz.hubertgaw.service.exception.core.NotFoundException;

public class AthleticsTrackException extends BaseException {

    public AthleticsTrackException(BaseException e) {
        super(e, e.getErrorCode());
    }

    public static AthleticsTrackException athleticsTrackNotFoundException() {
        return new AthleticsTrackException(
                NotFoundException.notFound(
                        "Athletics track for given id not found. Try to search in all sport objects or change the id."));
    }

    public static AthleticsTrackException athleticsTrackEmptyIdException() {
        return new AthleticsTrackException(
                EmptyPropertyException.emptyId("Id for updating athletics track cannot be null"));
    }
}
