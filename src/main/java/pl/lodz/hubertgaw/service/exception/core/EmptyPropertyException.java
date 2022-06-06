package pl.lodz.hubertgaw.service.exception.core;

public class EmptyPropertyException extends BaseException{

    public EmptyPropertyException(String message) {
        super(message, 400);
    }

    public static EmptyPropertyException emptyProperty(String message) {
        return new EmptyPropertyException(message);
    }

    //empty name and other properties are handled by @NotNull and @NotEmpty annotations
}
