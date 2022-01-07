package pl.lodz.hubertgaw.service.exception.mapper;

import pl.lodz.hubertgaw.service.exception.SportObjectException;
import pl.lodz.hubertgaw.service.exception.core.BaseException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class SportObjectExceptionHandler implements ExceptionMapper<BaseException> {

    @Override
    public Response toResponse(BaseException e) {
        return MapperUtils.convertExceptionToResponse(e);
    }

//    @Override
//    public Response toResponse(Throwable throwable) {
//        return null;
//    }
}
