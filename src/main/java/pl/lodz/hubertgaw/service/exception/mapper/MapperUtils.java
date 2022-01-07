package pl.lodz.hubertgaw.service.exception.mapper;

import pl.lodz.hubertgaw.service.exception.core.BaseException;

import javax.ws.rs.core.Response;

public class MapperUtils {

    public static Response convertExceptionToResponse(BaseException exception) {
        if (exception.getErrorCode() == 404) {
            return Response.status(Response.Status.NOT_FOUND).entity(exception.getMessage()).build();
        }
        else {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }
}
