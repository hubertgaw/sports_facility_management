package pl.lodz.hubertgaw.service.exception.core;

public class ForbiddenException extends BaseException {

    public ForbiddenException(String message) {
        super(message, 403);
    }

    public static ForbiddenException forbidden(String message) {
        return new ForbiddenException(message);
    }

}
