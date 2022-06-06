package pl.lodz.hubertgaw.service.exception;

import pl.lodz.hubertgaw.service.exception.core.BaseException;
import pl.lodz.hubertgaw.service.exception.core.EmptyPropertyException;
import pl.lodz.hubertgaw.service.exception.core.NotFoundException;

public class FullSizePitchException extends BaseException {

    public FullSizePitchException(BaseException e) {
        super(e, e.getErrorCode());
    }

    public static FullSizePitchException fullSizePitchNotFoundException() {
        return new FullSizePitchException(
                NotFoundException.notFound(
                        "Full size pitch for given id not found. Try to search in all sport objects or change the id."));
    }

    public static FullSizePitchException fullSizePitchEmptyIdException() {
        return new FullSizePitchException(
                EmptyPropertyException.emptyProperty("Id for updating full size pitch cannot be null"));
    }
}
