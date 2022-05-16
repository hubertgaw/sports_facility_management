package pl.lodz.hubertgaw.service.exception;

import pl.lodz.hubertgaw.service.exception.core.BaseException;
import pl.lodz.hubertgaw.service.exception.core.NotFoundException;

public class RoleException extends BaseException{

    protected RoleException(BaseException e) {
        super(e, e.getErrorCode());
    }

    public static RoleException roleNotFoundException() {
        return new RoleException(NotFoundException.notFound("Role not found."));
    }

}
