package pl.lodz.hubertgaw.service.exception.core;


public class NotFoundException extends BaseException {

    public NotFoundException(String message) {
        super(message, 404);
    }

    public static NotFoundException notFound(String message) {
        return new NotFoundException(message);
    }
}
