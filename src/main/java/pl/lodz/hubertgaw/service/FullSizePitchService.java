package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.FullSizePitch;
import pl.lodz.hubertgaw.mapper.SportObjectMapper;
import pl.lodz.hubertgaw.repository.FullSizePitchRepository;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.FullSizePitchEntity;
import pl.lodz.hubertgaw.service.exception.FullSizePitchException;
import pl.lodz.hubertgaw.service.exception.RentEquipmentException;
import pl.lodz.hubertgaw.service.exception.SportObjectException;
import pl.lodz.hubertgaw.service.utils.ServiceUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class FullSizePitchService {
    private final FullSizePitchRepository fullSizePitchRepository;
    private final SportObjectMapper sportObjectMapper;
    private final Logger logger;
    private final RentEquipmentRepository rentEquipmentRepository;
    private final ServiceUtils serviceUtils;

    public FullSizePitchService(FullSizePitchRepository fullSizePitchRepository,
                                SportObjectMapper sportObjectMapper,
                                Logger logger,
                                RentEquipmentRepository rentEquipmentRepository,
                                ServiceUtils serviceUtils) {
        this.fullSizePitchRepository = fullSizePitchRepository;
        this.sportObjectMapper = sportObjectMapper;
        this.logger = logger;
        this.rentEquipmentRepository = rentEquipmentRepository;
        this.serviceUtils = serviceUtils;

        logger.info("Constructor FullSizePitchService called");
    }

    public List<FullSizePitch> findAll() {
        logger.info("Method findAll() called");

        List<FullSizePitchEntity> allFullSizePitchsEntities = fullSizePitchRepository.listAll();

        logger.info("All fullSizePitches as entities taken from repository: {}", allFullSizePitchsEntities);

        List<FullSizePitch> allFullSizePitchesDto = allFullSizePitchsEntities
                .stream()
                .map(sportObjectMapper::toDomain)
                .map(FullSizePitch.class::cast)
                .collect(Collectors.toList());

        logger.info("All fullSizePitches found (after mapping from entity to DTO): {}", allFullSizePitchesDto);

        return allFullSizePitchesDto;
    }

    public FullSizePitch findById(Integer fullSizePitchId) {
        logger.info("Method findById() called with argument: {}", fullSizePitchId);

        FullSizePitchEntity entity = fullSizePitchRepository.findByIdOptional(fullSizePitchId)
                .orElseThrow(() -> {

                    logger.warn("Exception", FullSizePitchException.fullSizePitchNotFoundException());

                    return FullSizePitchException.fullSizePitchNotFoundException();
                });

        logger.info("FullSizePitchEntity by id: {} found in database:{}", fullSizePitchId, entity);

        FullSizePitch fullSizePitchDto = (FullSizePitch) sportObjectMapper.toDomain(entity);

        logger.info("FullSizePitch by id: {} found after mapping to DTO:{}", fullSizePitchId, fullSizePitchDto);

        return fullSizePitchDto;
    }

    @Transactional
    public FullSizePitch save(FullSizePitch fullSizePitch) {
        logger.info("Method save() called with argument: {}", fullSizePitch);

        if (serviceUtils.compareSportObjectNameWithExisting(fullSizePitch.getName())) {
            logger.warn("Exception", SportObjectException.sportObjectDuplicateNameException());

            throw SportObjectException.sportObjectDuplicateNameException();
        }

        FullSizePitchEntity entity = (FullSizePitchEntity) sportObjectMapper.toEntity(fullSizePitch);
        fullSizePitchRepository.persist(entity);

        logger.info("FullSizePitch persisted in repository: {}", entity);

        FullSizePitch fullSizePitchDto = (FullSizePitch) sportObjectMapper.toDomain(entity);

        logger.info("FullSizePitch mapped from entity to DTO: {}", fullSizePitchDto);

        return fullSizePitchDto;
    }

    @Transactional
    public FullSizePitch update(FullSizePitch fullSizePitch) {
        logger.info("Method update() called with argument: {}", fullSizePitch);

        if (fullSizePitch.getId() == null) {

            logger.warn("Exception", FullSizePitchException.fullSizePitchEmptyIdException());

            throw FullSizePitchException.fullSizePitchEmptyIdException();
        }
        FullSizePitchEntity entity = fullSizePitchRepository.findByIdOptional(fullSizePitch.getId())
                .orElseThrow(() -> {

                    logger.warn("Exception", FullSizePitchException.fullSizePitchNotFoundException());

                    return FullSizePitchException.fullSizePitchNotFoundException();
                });

        if (!fullSizePitch.getName().equals(entity.getName())) {
            if (serviceUtils.compareSportObjectNameWithExisting(fullSizePitch.getName())) {

                logger.warn("Exception", SportObjectException.sportObjectNotFoundException());

                throw SportObjectException.sportObjectDuplicateNameException();
            }
        }

        logger.info("FullSizePitch before update: {}", entity);

        entity.setFullPrice(fullSizePitch.getFullPrice());
        entity.setName(fullSizePitch.getName());
        entity.setHalfPitchPrice(fullSizePitch.getHalfPitchPrice());
        entity.setIsHalfRentable(fullSizePitch.getIsHalfRentable());
        fullSizePitchRepository.persist(entity);

        logger.info("FullSizePitch updated and persisted: {}", entity);

        FullSizePitch fullSizePitchDto = (FullSizePitch) sportObjectMapper.toDomain(entity);

        logger.info("Updated fullSizePitch mapped from entity to DTO: {}", fullSizePitchDto);

        return fullSizePitchDto;
    }

    @Transactional
    public FullSizePitch putEquipmentToObject(Integer sportObjectId, Integer rentEquipmentId) {
        logger.info("Method putEquipmentToObject() called with arguments: {}, {}", sportObjectId, rentEquipmentId);

        FullSizePitchEntity fullSizePitchToUpdate = fullSizePitchRepository.findById(sportObjectId);
        if (fullSizePitchToUpdate == null) {

            logger.warn("Exception", FullSizePitchException.fullSizePitchNotFoundException());

            throw FullSizePitchException.fullSizePitchNotFoundException();
        }
        RentEquipmentEntity rentEquipmentToAdd = rentEquipmentRepository.findById(rentEquipmentId);
        if (rentEquipmentToAdd == null) {

            logger.warn("Exception", RentEquipmentException.rentEquipmentNotFoundException());

            throw RentEquipmentException.rentEquipmentNotFoundException();
        }

        logger.info("FullSizePitchEntity and RentEquipmentEntity to be connected: {}, {}",
                fullSizePitchToUpdate, rentEquipmentToAdd);

        fullSizePitchToUpdate.addRentEquipment(rentEquipmentToAdd);
        fullSizePitchRepository.persistAndFlush(fullSizePitchToUpdate);

        logger.info("FullSizePitchEntity with new rentEquipmentEntity in it persisted in database: {}",
                fullSizePitchToUpdate);

        FullSizePitch fullSizePitchDto = (FullSizePitch) sportObjectMapper.toDomain(fullSizePitchToUpdate);

        logger.info("FullSizePitch with new rentEquipment mapped from entity to DTO: {}, {}",
                fullSizePitchDto, fullSizePitchDto.getRentEquipmentNames());

        return fullSizePitchDto;
    }

}
