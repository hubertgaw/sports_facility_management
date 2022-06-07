package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.SportSwimmingPool;
import pl.lodz.hubertgaw.mapper.SportObjectMapper;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.SportSwimmingPoolRepository;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SportSwimmingPoolEntity;
import pl.lodz.hubertgaw.service.exception.RentEquipmentException;
import pl.lodz.hubertgaw.service.exception.SportObjectException;
import pl.lodz.hubertgaw.service.exception.SportSwimmingPoolException;
import pl.lodz.hubertgaw.service.utils.ServiceUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class SportSwimmingPoolService {
    private final SportSwimmingPoolRepository sportSwimmingPoolRepository;
    private final SportObjectMapper sportObjectMapper;
    private final Logger logger;
    private final RentEquipmentRepository rentEquipmentRepository;
    private final ServiceUtils serviceUtils;

    public SportSwimmingPoolService(SportSwimmingPoolRepository sportSwimmingPoolRepository,
                                    SportObjectMapper sportObjectMapper,
                                    Logger logger,
                                    RentEquipmentRepository rentEquipmentRepository,
                                    ServiceUtils serviceUtils) {
        this.sportSwimmingPoolRepository = sportSwimmingPoolRepository;
        this.sportObjectMapper = sportObjectMapper;
        this.logger = logger;
        this.rentEquipmentRepository = rentEquipmentRepository;
        this.serviceUtils = serviceUtils;

        logger.info("Constructor SportSwimmingPoolService called");
    }

    public List<SportSwimmingPool> findAll() {
        logger.info("Method findAll() called");

        List<SportSwimmingPoolEntity> allSportSwimmingPoolsEntities = sportSwimmingPoolRepository.listAll();

        logger.info("All sportSwimmingPools as entities taken from repository: {}", allSportSwimmingPoolsEntities);

        List<SportSwimmingPool> allSportSwimmingPoolsDto = allSportSwimmingPoolsEntities
                .stream()
                .map(sportObjectMapper::toDomain)
                .map(SportSwimmingPool.class::cast)
                .collect(Collectors.toList());

        logger.info("All sportSwimmingPools found (after mapping from entity to DTO): {}", allSportSwimmingPoolsDto);

        return allSportSwimmingPoolsDto;
    }

    public SportSwimmingPool findById(Integer sportSwimmingPoolId) {
        logger.info("Method findById() called with argument: {}", sportSwimmingPoolId);

        SportSwimmingPoolEntity entity = sportSwimmingPoolRepository.findByIdOptional(sportSwimmingPoolId)
                .orElseThrow(() -> {

                    logger.warn("Exception", SportSwimmingPoolException.sportSwimmingPoolNotFoundException());

                    return SportSwimmingPoolException.sportSwimmingPoolNotFoundException();
                });

        logger.info("SportSwimmingPoolEntity by id: {} found in database:{}", sportSwimmingPoolId, entity);

        SportSwimmingPool sportSwimmingPoolDto = (SportSwimmingPool) sportObjectMapper.toDomain(entity);

        logger.info("SportSwimmingPool by id: {} found after mapping to DTO:{}", sportSwimmingPoolId, sportSwimmingPoolDto);

        return sportSwimmingPoolDto;
    }

    @Transactional
    public SportSwimmingPool save(SportSwimmingPool sportSwimmingPool) {
        logger.info("Method save() called with argument: {}", sportSwimmingPool);

        if (serviceUtils.compareSportObjectNameWithExisting(sportSwimmingPool.getName())) {

            logger.warn("Exception", SportObjectException.sportObjectDuplicateNameException());

            throw SportObjectException.sportObjectDuplicateNameException();
        }

        SportSwimmingPoolEntity entity = (SportSwimmingPoolEntity) sportObjectMapper.toEntity(sportSwimmingPool);
        sportSwimmingPoolRepository.persist(entity);

        logger.info("SportSwimmingPool persisted in repository: {}", entity);

        SportSwimmingPool sportSwimmingPoolDto = (SportSwimmingPool) sportObjectMapper.toDomain(entity);

        logger.info("SportSwimmingPool mapped from entity to DTO: {}", sportSwimmingPoolDto);

        return sportSwimmingPoolDto;
    }

    @Transactional
    public SportSwimmingPool update(SportSwimmingPool sportSwimmingPool) {
        logger.info("Method update() called with argument: {}", sportSwimmingPool);

        if (sportSwimmingPool.getId() == null) {

            logger.warn("Exception", SportSwimmingPoolException.sportSwimmingPoolEmptyIdException());

            throw SportSwimmingPoolException.sportSwimmingPoolEmptyIdException();
        }
        SportSwimmingPoolEntity entity = sportSwimmingPoolRepository.findByIdOptional(sportSwimmingPool.getId())
                .orElseThrow(() -> {

                    logger.warn("Exception", SportSwimmingPoolException.sportSwimmingPoolNotFoundException());

                    return SportSwimmingPoolException.sportSwimmingPoolNotFoundException();
                });

        if (!sportSwimmingPool.getName().equals(entity.getName())) {
            if (serviceUtils.compareSportObjectNameWithExisting(sportSwimmingPool.getName())) {

                logger.warn("Exception", SportObjectException.sportObjectNotFoundException());

                throw SportObjectException.sportObjectDuplicateNameException();
            }
        }

        logger.info("SportSwimmingPool before update: {}", entity);

        entity.setFullPrice(sportSwimmingPool.getFullPrice());
        entity.setName(sportSwimmingPool.getName());
        entity.setTrackPrice(sportSwimmingPool.getTrackPrice());
        entity.setTracksNumber(sportSwimmingPool.getCapacity());
        sportSwimmingPoolRepository.persist(entity);

        logger.info("SportSwimmingPool updated and persisted: {}", entity);

        SportSwimmingPool sportSwimmingPoolDto = (SportSwimmingPool) sportObjectMapper.toDomain(entity);

        logger.info("Updated sportSwimmingPool mapped from entity to DTO: {}", sportSwimmingPoolDto);

        return sportSwimmingPoolDto;
    }

    @Transactional
    public SportSwimmingPool putEquipmentToObject(Integer sportObjectId, Integer rentEquipmentId) {
        logger.info("Method putEquipmentToObject() called with arguments: {}, {}", sportObjectId, rentEquipmentId);

        SportSwimmingPoolEntity sportSwimmingPoolToUpdate = sportSwimmingPoolRepository.findById(sportObjectId);
        if (sportSwimmingPoolToUpdate == null) {

            logger.warn("Exception", SportSwimmingPoolException.sportSwimmingPoolNotFoundException());

            throw SportSwimmingPoolException.sportSwimmingPoolNotFoundException();
        }
        RentEquipmentEntity rentEquipmentToAdd = rentEquipmentRepository.findById(rentEquipmentId);
        if (rentEquipmentToAdd == null) {

            logger.warn("Exception", RentEquipmentException.rentEquipmentNotFoundException());

            throw RentEquipmentException.rentEquipmentNotFoundException();
        }

        logger.info("SportSwimmingPoolEntity and RentEquipmentEntity to be connected: {}, {}",
                sportSwimmingPoolToUpdate, rentEquipmentToAdd);

        sportSwimmingPoolToUpdate.addRentEquipment(rentEquipmentToAdd);
        sportSwimmingPoolRepository.persistAndFlush(sportSwimmingPoolToUpdate);

        logger.info("SportSwimmingPoolEntity with new rentEquipmentEntity in it persisted in database: {}",
                sportSwimmingPoolToUpdate);

        SportSwimmingPool sportSwimmingPoolDto = (SportSwimmingPool) sportObjectMapper.toDomain(sportSwimmingPoolToUpdate);

        logger.info("SportSwimmingPool with new rentEquipment mapped from entity to DTO: {}, {}",
                sportSwimmingPoolDto, sportSwimmingPoolDto.getRentEquipmentNames());

        return sportSwimmingPoolDto;
    }

}
