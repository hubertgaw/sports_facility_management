package pl.lodz.hubertgaw.service.exception.core;

public class EmptyPropertyException extends BaseException{

    public EmptyPropertyException(String message) {
        super(message, 400);
    }

    public static EmptyPropertyException emptyId(String message) {
        return new EmptyPropertyException(message);
    }
}