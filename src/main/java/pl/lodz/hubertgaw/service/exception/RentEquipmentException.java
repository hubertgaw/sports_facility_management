package pl.lodz.hubertgaw.service.exception;

import pl.lodz.hubertgaw.service.exception.core.BaseException;
import pl.lodz.hubertgaw.service.exception.core.DuplicateEntryException;
import pl.lodz.hubertgaw.service.exception.core.EmptyPropertyException;
import pl.lodz.hubertgaw.service.exception.core.NotFoundException;

public class RentEquipmentException extends BaseException {

    public RentEquipmentException (BaseException e) {
        super(e, e.getErrorCode());
    }

    public static RentEquipmentException rentEquipmentNotFoundException() {
        return new RentEquipmentException(NotFoundException.notFound("Rent equipment for given id not found!"));
    }

    public static RentEquipmentException rentEquipmentForNameNotFoundException() {
        return new RentEquipmentException(NotFoundException.notFound("No rent equipment found for specified name"));
    }

    public static RentEquipmentException rentEquipmentEmptyIdException() {
        return new RentEquipmentException(
                EmptyPropertyException.emptyId("Id for updating rent equipment cannot be null"));
    }

    public static RentEquipmentException rentEquipmentDuplicateNameException() {
        return new RentEquipmentException(
                DuplicateEntryException.duplicateName("Rent equipment with given name already exists"));
    }

}
