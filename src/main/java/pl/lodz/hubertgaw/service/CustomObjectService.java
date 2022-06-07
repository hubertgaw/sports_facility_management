package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.CustomObject;
import pl.lodz.hubertgaw.mapper.SportObjectMapper;
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

        logger.info("Constructor CustomObjectService called");

    }

    public List<CustomObject> findAll() {
        logger.info("Method findAll() called");

        List<CustomObjectEntity> allCustomObjectsEntities = customObjectRepository.listAll();

        logger.info("All customObjects as entities taken from repository: {}", allCustomObjectsEntities);

        List<CustomObject> allCustomObjectsDto = allCustomObjectsEntities
                .stream()
                .map(sportObjectMapper::toDomain)
                .map(CustomObject.class::cast)
                .collect(Collectors.toList());

        logger.info("All customObjects found (after mapping from entity to DTO): {}", allCustomObjectsDto);

        return allCustomObjectsDto;

    }

    public CustomObject findById(Integer customObjectId) {
        logger.info("Method findById() called with argument: {}", customObjectId);

        CustomObjectEntity entity = customObjectRepository.findByIdOptional(customObjectId)
                .orElseThrow(() -> {

                    logger.warn("Exception", CustomObjectException.customObjectNotFoundException());

                    return CustomObjectException.customObjectNotFoundException();
                });

        logger.info("CustomObjectEntity by id: {} found in database:{}", customObjectId, entity);

        CustomObject customObjectDto = (CustomObject) sportObjectMapper.toDomain(entity);

        logger.info("CustomObject by id: {} found after mapping to DTO:{}", customObjectId, customObjectDto);

        return customObjectDto;
    }

    public List<CustomObject> findByType(String type) {
        logger.info("Method findByType() called with argument: {}", type);

        List<CustomObjectEntity> customObjectEntitiesByType = customObjectRepository.findByType(type);

        logger.info("CustomObjects by type: {} as entities found in repository: {}", type, customObjectEntitiesByType);

        List<CustomObject> customObjectsByType = customObjectEntitiesByType
                .stream()
                .map(sportObjectMapper::toDomain)
                .map(CustomObject.class::cast)
                .collect(Collectors.toList());

        if (customObjectsByType.isEmpty()) {

            logger.warn("Exception", CustomObjectException.customObjectTypeNotFoundException());

            throw CustomObjectException.customObjectTypeNotFoundException();
        }

        logger.info("CustomObjects by type: {} found (after mapping from entity to DTO): {}",
                type, customObjectsByType);

        return customObjectsByType;
    }

    @Transactional
    public CustomObject save(CustomObject customObject) {
        logger.info("Method save() called with argument: {}", customObject);

        if (serviceUtils.compareSportObjectNameWithExisting(customObject.getName())) {

            logger.warn("Exception", SportObjectException.sportObjectDuplicateNameException());

            throw SportObjectException.sportObjectDuplicateNameException();
        }
        if (!customObject.getType().matches("^\\S*$")) {

            logger.warn("Exception", CustomObjectException.customObjectWrongTypeFormatException());

            throw CustomObjectException.customObjectWrongTypeFormatException();
        }
        CustomObjectEntity entity = (CustomObjectEntity) sportObjectMapper.toEntity(customObject);
        customObjectRepository.persist(entity);

        logger.info("CustomObject persisted in repository: {}", entity);

        CustomObject customObjectDto = (CustomObject) sportObjectMapper.toDomain(entity);

        logger.info("CustomObject mapped from entity to DTO: {}", customObjectDto);

        return customObjectDto;
    }

    @Transactional
    public CustomObject update(CustomObject customObject) {
        logger.info("Method update() called with argument: {}", customObject);

        if (customObject.getId() == null) {

            logger.warn("Exception", CustomObjectException.customObjectEmptyIdException());

            throw CustomObjectException.customObjectEmptyIdException();
        }
        CustomObjectEntity entity = customObjectRepository.findByIdOptional(customObject.getId())
                .orElseThrow(() -> {

                    logger.warn("Exception", CustomObjectException.customObjectNotFoundException());

                    return CustomObjectException.customObjectNotFoundException();
                });
        if (!customObject.getType().matches("^\\S*$")) {

            logger.warn("Exception", CustomObjectException.customObjectWrongTypeFormatException());

            throw CustomObjectException.customObjectWrongTypeFormatException();
        }

        if (!customObject.getName().equals(entity.getName())) {
            if (serviceUtils.compareSportObjectNameWithExisting(customObject.getName())) {

                logger.warn("Exception", SportObjectException.sportObjectNotFoundException());

                throw SportObjectException.sportObjectDuplicateNameException();
            }
        }

        logger.info("CustomObject before update: {}", entity);

        entity.setFullPrice(customObject.getFullPrice());
        entity.setName(customObject.getName());
        entity.setType(customObject.getType());
        entity.setCapacity(customObject.getCapacity());
        entity.setSinglePrice(customObject.getSinglePrice());
        customObjectRepository.persist(entity);

        logger.info("CustomObject updated and persisted: {}", entity);

        CustomObject customObjectDto = (CustomObject) sportObjectMapper.toDomain(entity);

        logger.info("Updated customObject mapped from entity to DTO: {}", customObjectDto);

        return customObjectDto;
    }

    @Transactional
    public CustomObject putEquipmentToObject(Integer sportObjectId, Integer rentEquipmentId) {
        logger.info("Method putEquipmentToObject() called with arguments: {}, {}", sportObjectId, rentEquipmentId);

        CustomObjectEntity customObjectToUpdate = customObjectRepository.findById(sportObjectId);
        if (customObjectToUpdate == null) {

            logger.warn("Exception", CustomObjectException.customObjectNotFoundException());

            throw CustomObjectException.customObjectNotFoundException();
        }
        RentEquipmentEntity rentEquipmentToAdd = rentEquipmentRepository.findById(rentEquipmentId);
        if (rentEquipmentToAdd == null) {

            logger.warn("Exception", RentEquipmentException.rentEquipmentNotFoundException());

            throw RentEquipmentException.rentEquipmentNotFoundException();
        }

        logger.info("CustomObjectEntity and RentEquipmentEntity to be connected: {}, {}",
                customObjectToUpdate, rentEquipmentToAdd);

        customObjectToUpdate.addRentEquipment(rentEquipmentToAdd);
        customObjectRepository.persistAndFlush(customObjectToUpdate);

        logger.info("CustomObjectEntity with new rentEquipmentEntity in it persisted in database: {}",
                customObjectToUpdate);

        CustomObject customObjectDto = (CustomObject) sportObjectMapper.toDomain(customObjectToUpdate);

        logger.info("CustomObject with new rentEquipment mapped from entity to DTO: {}, {}",
                customObjectDto, customObjectDto.getRentEquipmentNames());

        return customObjectDto;
    }

}
