package pl.lodz.hubertgaw.service.exception;

import pl.lodz.hubertgaw.service.exception.core.BaseException;
import pl.lodz.hubertgaw.service.exception.core.EmptyPropertyException;
import pl.lodz.hubertgaw.service.exception.core.NotFoundException;

public class DartRoomException extends BaseException {

    public DartRoomException(BaseException e) {
        super(e, e.getErrorCode());
    }

    public static DartRoomException dartRoomNotFoundException() {
        return new DartRoomException(
                NotFoundException.notFound(
                        "Dart room for given id not found. Try to search in all sport objects or change the id."));
    }

    public static DartRoomException dartRoomEmptyIdException() {
        return new DartRoomException(
                EmptyPropertyException.emptyProperty("Id for updating dart room cannot be null"));
    }
}
