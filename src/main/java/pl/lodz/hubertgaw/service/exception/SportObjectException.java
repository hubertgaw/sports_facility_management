package pl.lodz.hubertgaw.service.exception;

import lombok.Getter;
import pl.lodz.hubertgaw.service.exception.core.BaseException;
import pl.lodz.hubertgaw.service.exception.core.DuplicateEntryException;
import pl.lodz.hubertgaw.service.exception.core.EmptyPropertyException;
import pl.lodz.hubertgaw.service.exception.core.NotFoundException;

@Getter
public class SportObjectException extends BaseException {

    public SportObjectException(BaseException e) {
        super(e, e.getErrorCode());
    }

    public static SportObjectException sportObjectNotFoundException() {
        return new SportObjectException(NotFoundException.notFound("Sport object for given id not found"));
    }

    public static SportObjectException sportObjectEmptyIdException() {
        return new SportObjectException(EmptyPropertyException.emptyId("Id for updating sport object cannot be null"));
    }

    public static SportObjectException sportObjectDuplicateNameException() {
        return new SportObjectException(DuplicateEntryException.duplicateName("Sport object with given name already exists"));
    }
}
