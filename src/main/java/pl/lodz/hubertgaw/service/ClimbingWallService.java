package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.ClimbingWall;
import pl.lodz.hubertgaw.dto.ClimbingWall;
import pl.lodz.hubertgaw.mapper.SportObjectMapper;
import pl.lodz.hubertgaw.repository.ClimbingWallRepository;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.ClimbingWallEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.ClimbingWallEntity;
import pl.lodz.hubertgaw.service.exception.ClimbingWallException;
import pl.lodz.hubertgaw.service.exception.ClimbingWallException;
import pl.lodz.hubertgaw.service.exception.RentEquipmentException;
import pl.lodz.hubertgaw.service.exception.SportObjectException;
import pl.lodz.hubertgaw.service.utils.ServiceUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ClimbingWallService {
    private final ClimbingWallRepository climbingWallRepository;
    private final SportObjectMapper sportObjectMapper;
    private final Logger logger;
    private final RentEquipmentRepository rentEquipmentRepository;
    private final ServiceUtils serviceUtils;

    public ClimbingWallService(ClimbingWallRepository climbingWallRepository,
                               SportObjectMapper sportObjectMapper,
                               Logger logger,
                               RentEquipmentRepository rentEquipmentRepository,
                               ServiceUtils serviceUtils) {
        this.climbingWallRepository = climbingWallRepository;
        this.sportObjectMapper = sportObjectMapper;
        this.logger = logger;
        this.rentEquipmentRepository = rentEquipmentRepository;
        this.serviceUtils = serviceUtils;

        logger.info("Constructor ClimbingWallService called");
    }

    public List<ClimbingWall> findAll() {
        logger.info("Method findAll() called");

        List<ClimbingWallEntity> allClimbingWallsEntities = climbingWallRepository.listAll();

        logger.info("All climbingWalls as entities taken from repository: {}", allClimbingWallsEntities);

        List<ClimbingWall> allClimbingWallsDto = allClimbingWallsEntities
                .stream()
                .map(sportObjectMapper::toDomain)
                .map(ClimbingWall.class::cast)
                .collect(Collectors.toList());

        logger.info("All climbingWalls found (after mapping from entity to DTO): {}", allClimbingWallsDto);

        return allClimbingWallsDto;
    }

    public ClimbingWall findById(Integer climbingWallId) {
        logger.info("Method findById() called with argument: {}", climbingWallId);

        ClimbingWallEntity entity = climbingWallRepository.findByIdOptional(climbingWallId)
                .orElseThrow(() -> {

                    logger.warn("Exception", ClimbingWallException.climbingWallNotFoundException());

                    return ClimbingWallException.climbingWallNotFoundException();
                });

        logger.info("ClimbingWallEntity by id: {} found in database:{}", climbingWallId, entity);

        ClimbingWall climbingWallDto = (ClimbingWall) sportObjectMapper.toDomain(entity);

        logger.info("ClimbingWall by id: {} found after mapping to DTO:{}", climbingWallId, climbingWallDto);

        return climbingWallDto;
    }

    @Transactional
    public ClimbingWall save(ClimbingWall climbingWall) {
        logger.info("Method save() called with argument: {}", climbingWall);

        if (serviceUtils.compareSportObjectNameWithExisting(climbingWall.getName())) {

            logger.warn("Exception", SportObjectException.sportObjectDuplicateNameException());

            throw SportObjectException.sportObjectDuplicateNameException();
        }
        ClimbingWallEntity entity = (ClimbingWallEntity) sportObjectMapper.toEntity(climbingWall);
        climbingWallRepository.persist(entity);

        logger.info("ClimbingWall persisted in repository: {}", entity);

        ClimbingWall climbingWallDto = (ClimbingWall) sportObjectMapper.toDomain(entity);

        logger.info("ClimbingWall mapped from entity to DTO: {}", climbingWallDto);

        return climbingWallDto;
    }

    @Transactional
    public ClimbingWall update(ClimbingWall climbingWall) {
        logger.info("Method update() called with argument: {}", climbingWall);

        if (climbingWall.getId() == null) {

            logger.warn("Exception", ClimbingWallException.climbingWallEmptyIdException());

            throw ClimbingWallException.climbingWallEmptyIdException();
        }
        ClimbingWallEntity entity = climbingWallRepository.findByIdOptional(climbingWall.getId())
                .orElseThrow(() -> {

                    logger.warn("Exception", ClimbingWallException.climbingWallNotFoundException());

                    return ClimbingWallException.climbingWallNotFoundException();
                });

        logger.info("ClimbingWall before update: {}", entity);

        entity.setFullPrice(climbingWall.getFullPrice());
        entity.setName(climbingWall.getName());
        entity.setCapacity(climbingWall.getCapacity());
        entity.setSinglePrice(climbingWall.getSinglePrice());
        climbingWallRepository.persist(entity);

        logger.info("ClimbingWall updated and persisted: {}", entity);

        ClimbingWall climbingWallDto = (ClimbingWall) sportObjectMapper.toDomain(entity);

        logger.info("Updated climbingWall mapped from entity to DTO: {}", climbingWallDto);

        return climbingWallDto;
    }

    @Transactional
    public ClimbingWall putEquipmentToObject(Integer sportObjectId, Integer rentEquipmentId) {
        logger.info("Method putEquipmentToObject() called with arguments: {}, {}", sportObjectId, rentEquipmentId);

        ClimbingWallEntity climbingWallToUpdate = climbingWallRepository.findById(sportObjectId);
        if (climbingWallToUpdate == null) {

            logger.warn("Exception", ClimbingWallException.climbingWallNotFoundException());

            throw ClimbingWallException.climbingWallNotFoundException();
        }
        RentEquipmentEntity rentEquipmentToAdd = rentEquipmentRepository.findById(rentEquipmentId);
        if (rentEquipmentToAdd == null) {

            logger.warn("Exception", RentEquipmentException.rentEquipmentNotFoundException());

            throw RentEquipmentException.rentEquipmentNotFoundException();
        }

        logger.info("ClimbingWallEntity and RentEquipmentEntity to be connected: {}, {}",
                climbingWallToUpdate, rentEquipmentToAdd);

        climbingWallToUpdate.addRentEquipment(rentEquipmentToAdd);
        climbingWallRepository.persistAndFlush(climbingWallToUpdate);

        logger.info("ClimbingWallEntity with new rentEquipmentEntity in it persisted in database: {}",
                climbingWallToUpdate);

        ClimbingWall climbingWallDto = (ClimbingWall) sportObjectMapper.toDomain(climbingWallToUpdate);

        logger.info("ClimbingWall with new rentEquipment mapped from entity to DTO: {}, {}",
                climbingWallDto, climbingWallDto.getRentEquipmentNames());

        return climbingWallDto;
    }

}
