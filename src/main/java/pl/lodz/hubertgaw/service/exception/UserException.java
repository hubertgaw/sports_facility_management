package pl.lodz.hubertgaw.service.exception;

import pl.lodz.hubertgaw.service.exception.core.*;

public class UserException extends BaseException {

    protected UserException(BaseException e) {
        super(e, e.getErrorCode());
    }

    public static UserException userNotFoundException() {
        return new UserException(NotFoundException.notFound("User for given id not found!"));
    }

    public static UserException userEmptyIdException() {
        return new UserException(
                EmptyPropertyException.emptyId("Id for updating user cannot be null"));
    }

    public static UserException userForEmailNotFoundException() {
        return new UserException(NotFoundException.notFound("No user found for specified email"));
    }

    public static UserException userDuplicateEmailException() {
        return new UserException(
                DuplicateEntryException.duplicateName("User with given email already exists"));
    }

    public static UserException userForRoleNotFoundException() {
        return new UserException(NotFoundException.notFound("No user found for specified role"));
    }
}
