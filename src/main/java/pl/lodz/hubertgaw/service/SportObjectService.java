package pl.lodz.hubertgaw.service;

import pl.lodz.hubertgaw.dto.SmallPitch;
import pl.lodz.hubertgaw.dto.SportObject;
import pl.lodz.hubertgaw.dto.SportsHall;
import pl.lodz.hubertgaw.mapper.SportObjectMapper;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.SportObjectRepository;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SmallPitchEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SportObjectEntity;
import pl.lodz.hubertgaw.service.exception.RentEquipmentException;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.cxf.jaxrs.impl.ResponseBuilderImpl;
import pl.lodz.hubertgaw.service.exception.SportObjectException;


@ApplicationScoped
public class SportObjectService {
    private final SportObjectRepository sportObjectRepository;
    private final SportObjectMapper sportObjectMapper;
    private final Logger logger;
    private final RentEquipmentRepository rentEquipmentRepository;

    public SportObjectService(SportObjectRepository sportObjectRepository,
                              SportObjectMapper sportObjectMapper,
                              Logger logger,
                              RentEquipmentRepository rentEquipmentRepository) {
        this.sportObjectRepository = sportObjectRepository;
        this.sportObjectMapper = sportObjectMapper;
        this.logger = logger;
        this.rentEquipmentRepository = rentEquipmentRepository;
    }

    public List<SportObject> findAll() {
        return sportObjectRepository.listAll()
                .stream()
                .map(sportObjectMapper::toDomain)
                .map(SportObject.class::cast)
                .collect(Collectors.toList());
    }

    public SportObject findById(Integer sportObjectId) {
        SportObjectEntity entity = sportObjectRepository.findByIdOptional(sportObjectId)
                .orElseThrow(SportObjectException::sportObjectNotFoundException);

        return sportObjectMapper.toDomain(entity);
//        return entity
//                .map(sportObjectMapper::toDomain)
//                .map(SportObject.class::cast);
    }

//    @Transactional
//    public SportObject update(SportObject sportObject) {
//        if (sportObject.getId() == null) {
//            throw new ServiceException("SportObject does not have a sportObjectId");
//        }
//        Optional<SportObjectEntity> optional = sportObjectRepository.findByIdOptional(sportObject.getId());
//        if (optional.isEmpty()) {
//            throw new ServiceException(String.format("No SportObject found for Id[%s]", sportObject.getId()));
//        }
//        SportObjectEntity entity = optional.get();
//        entity.setFullPrice(smallPitch.getFullPrice());
//        entity.setName(smallPitch.getName());
//        entity.setHalfPitchPrice(smallPitch.getHalfPitchPrice());
//        entity.setIsFullRented(smallPitch.getIsFullRented());
//        smallPitchRepository.persist(entity);
//        return (SmallPitch) sportObjectMapper.toDomain(entity);
//
//    }

    //todo mapowanie exceptionow
    @Transactional
    public SportObject putEquipmentToObject(Integer sportObjectId, Integer rentEquipmentId) {
        SportObjectEntity sportObjectToUpdate = sportObjectRepository.findById(sportObjectId);
        if (sportObjectToUpdate == null) {
//            ResponseBuilderImpl builder = new ResponseBuilderImpl();
//            builder.status(Response.Status.NOT_FOUND);
//            builder.entity("Sport object with id provided not found");
//            Response response = builder.build();
//            throw new WebApplicationException(response);
//            throw new WebApplicationException("Sport object with id provided not found", Response.Status.NOT_FOUND);
//            throw new NotFoundException("Sport object with id provided not found");
            throw SportObjectException.sportObjectNotFoundException();
        }
        RentEquipmentEntity rentEquipmentToAdd = rentEquipmentRepository.findById(rentEquipmentId);
        if (rentEquipmentToAdd == null) {
            throw RentEquipmentException.rentEquipmentNotFoundException();
//            throw new WebApplicationException("Rent equipment with id provided not found", Response.Status.NOT_FOUND);
//            throw new NotFoundException("Rent equipment with id provided not found");

        }
        sportObjectToUpdate.addRentEquipment(rentEquipmentToAdd);
        sportObjectRepository.persistAndFlush(sportObjectToUpdate);
        return sportObjectMapper.toDomain(sportObjectToUpdate);
    }

    @Transactional
    public void deleteSportObjectById(Integer sportObjectId) {
        SportObjectEntity sportObjectToDelete = sportObjectRepository.findById(sportObjectId);
        if (sportObjectToDelete == null) {
            throw SportObjectException.sportObjectNotFoundException();
        }
        Set<RentEquipmentEntity> rentEquipmentToDelete = sportObjectToDelete.getRentEquipment();
        for (RentEquipmentEntity rentEquipment : rentEquipmentToDelete) {
            rentEquipment.removeSportObject(sportObjectToDelete);
        }
        sportObjectRepository.delete(sportObjectToDelete);
//        sportObjectRepository.delete(sportObjectRepository.findById(sportObjectId));
//        return sportObjectRepository.deleteById(sportObjectId);
    }
}
