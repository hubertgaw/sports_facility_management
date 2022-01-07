package pl.lodz.hubertgaw.service.exception.mapper;

import pl.lodz.hubertgaw.service.exception.RentEquipmentException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RentEquipmentExceptionHandler implements ExceptionMapper<RentEquipmentException> {
    @Override
    public Response toResponse(RentEquipmentException e) {
        return MapperUtils.convertExceptionToResponse(e);
    }
}
