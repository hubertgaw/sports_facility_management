package pl.lodz.hubertgaw.service.exception.core;

public class WrongFormatException extends BaseException {
    public WrongFormatException(String message) {
        super(message, 400);
    }

    public static WrongFormatException wrongFormat(String message) {
        return new WrongFormatException(message);
    }
}
