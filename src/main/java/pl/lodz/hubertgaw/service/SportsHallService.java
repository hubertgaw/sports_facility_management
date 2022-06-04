package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.SportsHall;
import pl.lodz.hubertgaw.dto.SportsHall;
import pl.lodz.hubertgaw.mapper.SportObjectMapper;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.SportsHallRepository;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SportsHallEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SportsHallEntity;
import pl.lodz.hubertgaw.service.exception.SportsHallException;
import pl.lodz.hubertgaw.service.exception.RentEquipmentException;
import pl.lodz.hubertgaw.service.exception.SportObjectException;
import pl.lodz.hubertgaw.service.exception.SportsHallException;
import pl.lodz.hubertgaw.service.utils.ServiceUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class SportsHallService {
    private final SportsHallRepository sportsHallRepository;
    private final SportObjectMapper sportObjectMapper;
    private final Logger logger;
    private final RentEquipmentRepository rentEquipmentRepository;
    private final ServiceUtils serviceUtils;

    public SportsHallService(SportsHallRepository sportsHallRepository,
                             SportObjectMapper sportObjectMapper,
                             Logger logger,
                             RentEquipmentRepository rentEquipmentRepository,
                             ServiceUtils serviceUtils) {
        this.sportsHallRepository = sportsHallRepository;
        this.sportObjectMapper = sportObjectMapper;
        this.logger = logger;
        this.rentEquipmentRepository = rentEquipmentRepository;
        this.serviceUtils = serviceUtils;

        logger.info("Constructor SportsHallService called");
    }

    public List<SportsHall> findAll() {
        logger.info("Method findAll() called");

        List<SportsHallEntity> allSportsHallsEntities = sportsHallRepository.listAll();

        logger.info("All sportsHalls as entities taken from repository: {}", allSportsHallsEntities);

        List<SportsHall> allSportsHallsDto = allSportsHallsEntities
                .stream()
                .map(sportObjectMapper::toDomain)
                .map(SportsHall.class::cast)
                .collect(Collectors.toList());

        logger.info("All sportsHalls found (after mapping from entity to DTO): {}", allSportsHallsDto);

        return allSportsHallsDto;

    }

    public SportsHall findById(Integer sportsHallId) {
        logger.info("Method findById() called with argument: {}", sportsHallId);

        SportsHallEntity entity = sportsHallRepository.findByIdOptional(sportsHallId)
                .orElseThrow(() -> {

                    logger.warn("Exception", SportsHallException.sportsHallNotFoundException());

                    return SportsHallException.sportsHallNotFoundException();
                });

        logger.info("SportsHallEntity by id: {} found in database:{}", sportsHallId, entity);

        SportsHall sportsHallDto = (SportsHall) sportObjectMapper.toDomain(entity);

        logger.info("SportsHall by id: {} found after mapping to DTO:{}", sportsHallId, sportsHallDto);

        return sportsHallDto;
    }

    @Transactional
    public SportsHall save(SportsHall sportsHall) {
        logger.info("Method save() called with argument: {}", sportsHall);

        if (serviceUtils.compareSportObjectNameWithExisting(sportsHall.getName())) {

            logger.warn("Exception", SportObjectException.sportObjectDuplicateNameException());

            throw SportObjectException.sportObjectDuplicateNameException();
        }

        SportsHallEntity entity = (SportsHallEntity) sportObjectMapper.toEntity(sportsHall);
        sportsHallRepository.persist(entity);

        logger.info("SportsHall persisted in repository: {}", entity);

        SportsHall sportsHallDto = (SportsHall) sportObjectMapper.toDomain(entity);

        logger.info("SportsHall mapped from entity to DTO: {}", sportsHallDto);

        return sportsHallDto;
    }

    @Transactional
    public SportsHall update(SportsHall sportsHall) {
        logger.info("Method update() called with argument: {}", sportsHall);

        if (sportsHall.getId() == null) {

            logger.warn("Exception", SportsHallException.sportsHallEmptyIdException());

            throw SportsHallException.sportsHallEmptyIdException();
        }
        SportsHallEntity entity = sportsHallRepository.findByIdOptional(sportsHall.getId())
                .orElseThrow(() -> {

                    logger.warn("Exception", SportsHallException.sportsHallNotFoundException());

                    return SportsHallException.sportsHallNotFoundException();
                });

        if (!sportsHall.getName().equals(entity.getName())) {
            if (serviceUtils.compareSportObjectNameWithExisting(sportsHall.getName())) {

                logger.warn("Exception", SportObjectException.sportObjectNotFoundException());

                throw SportObjectException.sportObjectDuplicateNameException();
            }
        }

        logger.info("SportsHall before update: {}", entity);

        entity.setFullPrice(sportsHall.getFullPrice());
        entity.setName(sportsHall.getName());
        entity.setSectorPrice(sportsHall.getSectorPrice());
        sportsHall.setSectorsNumber(sportsHall.getCapacity());
        sportsHallRepository.persist(entity);

        logger.info("SportsHall updated and persisted: {}", entity);

        SportsHall sportsHallDto = (SportsHall) sportObjectMapper.toDomain(entity);

        logger.info("Updated sportsHall mapped from entity to DTO: {}", sportsHallDto);

        return sportsHallDto;
    }

    @Transactional
    public SportsHall putEquipmentToObject(Integer sportObjectId, Integer rentEquipmentId) {
        logger.info("Method putEquipmentToObject() called with arguments: {}, {}", sportObjectId, rentEquipmentId);

        SportsHallEntity sportsHallToUpdate = sportsHallRepository.findById(sportObjectId);
        if (sportsHallToUpdate == null) {

            logger.warn("Exception", SportsHallException.sportsHallNotFoundException());

            throw SportsHallException.sportsHallNotFoundException();
        }
        RentEquipmentEntity rentEquipmentToAdd = rentEquipmentRepository.findById(rentEquipmentId);
        if (rentEquipmentToAdd == null) {

            logger.warn("Exception", RentEquipmentException.rentEquipmentNotFoundException());

            throw RentEquipmentException.rentEquipmentNotFoundException();
        }

        logger.info("SportsHallEntity and RentEquipmentEntity to be connected: {}, {}",
                sportsHallToUpdate, rentEquipmentToAdd);

        sportsHallToUpdate.addRentEquipment(rentEquipmentToAdd);
        sportsHallRepository.persistAndFlush(sportsHallToUpdate);

        logger.info("SportsHallEntity with new rentEquipmentEntity in it persisted in database: {}",
                sportsHallToUpdate);

        SportsHall sportsHallDto = (SportsHall) sportObjectMapper.toDomain(sportsHallToUpdate);

        logger.info("SportsHall with new rentEquipment mapped from entity to DTO: {}, {}",
                sportsHallDto, sportsHallDto.getRentEquipmentNames());

        return sportsHallDto;
    }

}
