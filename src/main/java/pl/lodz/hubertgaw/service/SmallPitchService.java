package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.SmallPitch;
import pl.lodz.hubertgaw.dto.SmallPitch;
import pl.lodz.hubertgaw.mapper.SportObjectMapper;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.SmallPitchRepository;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SmallPitchEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SmallPitchEntity;
import pl.lodz.hubertgaw.service.exception.SmallPitchException;
import pl.lodz.hubertgaw.service.exception.RentEquipmentException;
import pl.lodz.hubertgaw.service.exception.SmallPitchException;
import pl.lodz.hubertgaw.service.exception.SportObjectException;
import pl.lodz.hubertgaw.service.utils.ServiceUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class SmallPitchService {
    private final SmallPitchRepository smallPitchRepository;
    private final SportObjectMapper sportObjectMapper;
    private final Logger logger;
    private final RentEquipmentRepository rentEquipmentRepository;
    private final ServiceUtils serviceUtils;

    public SmallPitchService(SmallPitchRepository smallPitchRepository,
                             SportObjectMapper sportObjectMapper,
                             Logger logger,
                             RentEquipmentRepository rentEquipmentRepository,
                             ServiceUtils serviceUtils) {
        this.smallPitchRepository = smallPitchRepository;
        this.sportObjectMapper = sportObjectMapper;
        this.logger = logger;
        this.rentEquipmentRepository = rentEquipmentRepository;
        this.serviceUtils = serviceUtils;

        logger.info("Constructor SmallPitchService called");
    }

    public List<SmallPitch> findAll() {
        logger.info("Method findAll() called");

        List<SmallPitchEntity> allSmallPitchesEntities = smallPitchRepository.listAll();

        logger.info("All smallPitchs as entities taken from repository: {}", allSmallPitchesEntities);

        List<SmallPitch> allSmallPitchesDto = allSmallPitchesEntities
                .stream()
                .map(sportObjectMapper::toDomain)
                .map(SmallPitch.class::cast)
                .collect(Collectors.toList());

        logger.info("All smallPitches found (after mapping from entity to DTO): {}", allSmallPitchesDto);

        return allSmallPitchesDto;
    }

    public SmallPitch findById(Integer smallPitchId) {
        logger.info("Method findById() called with argument: {}", smallPitchId);

        SmallPitchEntity entity = smallPitchRepository.findByIdOptional(smallPitchId)
                .orElseThrow(() -> {

                    logger.warn("Exception", SmallPitchException.smallPitchNotFoundException());

                    return SmallPitchException.smallPitchNotFoundException();
                });

        logger.info("SmallPitchEntity by id: {} found in database:{}", smallPitchId, entity);

        SmallPitch smallPitchDto = (SmallPitch) sportObjectMapper.toDomain(entity);

        logger.info("SmallPitch by id: {} found after mapping to DTO:{}", smallPitchId, smallPitchDto);

        return smallPitchDto;
    }

    @Transactional
    public SmallPitch save(SmallPitch smallPitch) {
        logger.info("Method save() called with argument: {}", smallPitch);

        if (serviceUtils.compareSportObjectNameWithExisting(smallPitch.getName())) {

            logger.warn("Exception", SportObjectException.sportObjectDuplicateNameException());

            throw SportObjectException.sportObjectDuplicateNameException();
        }

        SmallPitchEntity entity = (SmallPitchEntity) sportObjectMapper.toEntity(smallPitch);
        smallPitchRepository.persist(entity);

        logger.info("SmallPitch persisted in repository: {}", entity);

        SmallPitch smallPitchDto = (SmallPitch) sportObjectMapper.toDomain(entity);

        logger.info("SmallPitch mapped from entity to DTO: {}", smallPitchDto);

        return smallPitchDto;
    }

    @Transactional
    public SmallPitch update(SmallPitch smallPitch) {
        logger.info("Method update() called with argument: {}", smallPitch);

        if (smallPitch.getId() == null) {

            logger.warn("Exception", SmallPitchException.smallPitchEmptyIdException());

            throw SmallPitchException.smallPitchEmptyIdException();
        }
        SmallPitchEntity entity = smallPitchRepository.findByIdOptional(smallPitch.getId()).
                orElseThrow(() -> {

                    logger.warn("Exception", SmallPitchException.smallPitchNotFoundException());

                    return SmallPitchException.smallPitchNotFoundException();
                });

        if (!smallPitch.getName().equals(entity.getName())) {
            if (serviceUtils.compareSportObjectNameWithExisting(smallPitch.getName())) {

                logger.warn("Exception", SportObjectException.sportObjectNotFoundException());

                throw SportObjectException.sportObjectDuplicateNameException();
            }
        }

        logger.info("SmallPitch before update: {}", entity);

        entity.setFullPrice(smallPitch.getFullPrice());
        entity.setName(smallPitch.getName());
        entity.setHalfPitchPrice(smallPitch.getHalfPitchPrice());
        entity.setIsHalfRentable(smallPitch.getIsHalfRentable());
        smallPitchRepository.persist(entity);

        logger.info("SmallPitch updated and persisted: {}", entity);

        SmallPitch smallPitchDto = (SmallPitch) sportObjectMapper.toDomain(entity);

        logger.info("Updated smallPitch mapped from entity to DTO: {}", smallPitchDto);

        return smallPitchDto;
    }

    @Transactional
    public SmallPitch putEquipmentToObject(Integer sportObjectId, Integer rentEquipmentId) {
        logger.info("Method putEquipmentToObject() called with arguments: {}, {}", sportObjectId, rentEquipmentId);

        SmallPitchEntity smallPitchToUpdate = smallPitchRepository.findById(sportObjectId);
        if (smallPitchToUpdate == null) {

            logger.warn("Exception", SmallPitchException.smallPitchNotFoundException());

            throw SmallPitchException.smallPitchNotFoundException();
        }
        RentEquipmentEntity rentEquipmentToAdd = rentEquipmentRepository.findById(rentEquipmentId);
        if (rentEquipmentToAdd == null) {

            logger.warn("Exception", RentEquipmentException.rentEquipmentNotFoundException());

            throw RentEquipmentException.rentEquipmentNotFoundException();
        }

        logger.info("SmallPitchEntity and RentEquipmentEntity to be connected: {}, {}",
                smallPitchToUpdate, rentEquipmentToAdd);

        smallPitchToUpdate.addRentEquipment(rentEquipmentToAdd);
        smallPitchRepository.persistAndFlush(smallPitchToUpdate);

        logger.info("SmallPitchEntity with new rentEquipmentEntity in it persisted in database: {}",
                smallPitchToUpdate);

        SmallPitch smallPitchDto = (SmallPitch) sportObjectMapper.toDomain(smallPitchToUpdate);

        logger.info("SmallPitch with new rentEquipment mapped from entity to DTO: {}, {}",
                smallPitchDto, smallPitchDto.getRentEquipmentNames());

        return smallPitchDto;
    }

}
