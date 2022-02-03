package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.CustomObject;
import pl.lodz.hubertgaw.mapper.SportObjectMapper;
import pl.lodz.hubertgaw.repository.CustomObjectRepository;
import pl.lodz.hubertgaw.repository.CustomObjectRepository;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.CustomObjectEntity;
import pl.lodz.hubertgaw.service.exception.CustomObjectException;
import pl.lodz.hubertgaw.service.exception.RentEquipmentException;
import pl.lodz.hubertgaw.service.exception.SportObjectException;
import pl.lodz.hubertgaw.service.utils.ServiceUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class CustomObjectService {
    private final CustomObjectRepository customObjectRepository;
    private final SportObjectMapper sportObjectMapper;
    private final Logger logger;
    private final RentEquipmentRepository rentEquipmentRepository;
    private final ServiceUtils serviceUtils;

    public CustomObjectService(CustomObjectRepository customObjectRepository,
                               SportObjectMapper sportObjectMapper,
                               Logger logger,
                               RentEquipmentRepository rentEquipmentRepository,
                               ServiceUtils serviceUtils) {
        this.customObjectRepository = customObjectRepository;
        this.sportObjectMapper = sportObjectMapper;
        this.logger = logger;
        this.rentEquipmentRepository = rentEquipmentRepository;
        this.serviceUtils = serviceUtils;
    }

    public List<CustomObject> findAll() {
        return customObjectRepository.listAll()
                .stream()
                .map(sportObjectMapper::toDomain)
                .map(CustomObject.class::cast)
                .collect(Collectors.toList());
    }

    public CustomObject findById(Integer wallId) {
        CustomObjectEntity entity = customObjectRepository.findByIdOptional(wallId)
                .orElseThrow(CustomObjectException::customObjectNotFoundException);
        return (CustomObject) sportObjectMapper.toDomain(entity);
    }

    public List<CustomObject> findByType(String type) {
        List<CustomObject> customObjectsByType = customObjectRepository.findByType(type)
                .stream()
                .map(sportObjectMapper::toDomain)
                .map(CustomObject.class::cast)
                .collect(Collectors.toList());

        if (customObjectsByType.isEmpty()) {
            throw CustomObjectException.customObjectTypeNotFoundException();
        }

        return customObjectsByType;
    }

    @Transactional
    public CustomObject save(CustomObject customObject) {
        if (serviceUtils.compareSportObjectNameWithExisting(customObject.getName())) {
            throw SportObjectException.sportObjectDuplicateNameException();
        }
        if (!customObject.getType().matches("^\\S*$")) {
            throw CustomObjectException.customObjectWrongTypeFormatException();
        }
        CustomObjectEntity entity = (CustomObjectEntity) sportObjectMapper.toEntity(customObject);
        customObjectRepository.persist(entity);
        return (CustomObject) sportObjectMapper.toDomain(entity);
    }

    @Transactional
    public CustomObject update(CustomObject customObject) {
        if (customObject.getId() == null) {
            throw CustomObjectException.customObjectEmptyIdException();
        }
        CustomObjectEntity entity = customObjectRepository.findByIdOptional(customObject.getId())
                .orElseThrow(CustomObjectException::customObjectNotFoundException);
        if (!customObject.getType().matches("^\\S*$")) {
            throw CustomObjectException.customObjectWrongTypeFormatException();
        }

        entity.setFullPrice(customObject.getFullPrice());
        entity.setName(customObject.getName());
        entity.setType(customObject.getType());
        entity.setCapacity(customObject.getCapacity());
        entity.setSinglePrice(customObject.getSinglePrice());
        entity.setStandsNumber(customObject.getStandsNumber());
        customObjectRepository.persist(entity);
        return (CustomObject) sportObjectMapper.toDomain(entity);
    }

    @Transactional
    public CustomObject putEquipmentToObject(Integer sportObjectId, Integer rentEquipmentId) {
        CustomObjectEntity customObjectToUpdate = customObjectRepository.findById(sportObjectId);
        if (customObjectToUpdate == null) {
            throw CustomObjectException.customObjectNotFoundException();
        }
        RentEquipmentEntity rentEquipmentToAdd = rentEquipmentRepository.findById(rentEquipmentId);
        if (rentEquipmentToAdd == null) {
            throw RentEquipmentException.rentEquipmentNotFoundException();
        }
        customObjectToUpdate.addRentEquipment(rentEquipmentToAdd);
        customObjectRepository.persistAndFlush(customObjectToUpdate);
        return (CustomObject) sportObjectMapper.toDomain(customObjectToUpdate);
    }

}
