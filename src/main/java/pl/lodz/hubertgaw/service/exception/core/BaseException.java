package pl.lodz.hubertgaw.service.exception.core;

import lombok.Getter;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@Getter
public class BaseException extends RuntimeException {

    private final int errorCode;

    protected BaseException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    protected BaseException(Throwable cause, int errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }
}
