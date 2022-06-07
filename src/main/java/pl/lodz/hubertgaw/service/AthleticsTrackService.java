package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.AthleticsTrack;
import pl.lodz.hubertgaw.mapper.SportObjectMapper;
import pl.lodz.hubertgaw.repository.AthleticsTrackRepository;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.AthleticsTrackEntity;
import pl.lodz.hubertgaw.service.exception.AthleticsTrackException;
import pl.lodz.hubertgaw.service.exception.RentEquipmentException;
import pl.lodz.hubertgaw.service.exception.SportObjectException;
import pl.lodz.hubertgaw.service.utils.ServiceUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class AthleticsTrackService {

    private final AthleticsTrackRepository athleticsTrackRepository;
    private final SportObjectMapper sportObjectMapper;
    private final Logger logger;
    private final RentEquipmentRepository rentEquipmentRepository;
    private final ServiceUtils serviceUtils;

    public AthleticsTrackService(AthleticsTrackRepository athleticsTrackRepository,
                                 SportObjectMapper sportObjectMapper,
                                 Logger logger,
                                 RentEquipmentRepository rentEquipmentRepository,
                                 ServiceUtils serviceUtils) {
        this.athleticsTrackRepository = athleticsTrackRepository;
        this.sportObjectMapper = sportObjectMapper;
        this.logger = logger;
        this.rentEquipmentRepository = rentEquipmentRepository;
        this.serviceUtils = serviceUtils;

        logger.info("Constructor AthleticsTrackService called");
    }

    public List<AthleticsTrack> findAll() {
        logger.info("Method findAll() called");

        List<AthleticsTrackEntity> allAthleticsTracksEntities = athleticsTrackRepository.listAll();

        logger.info("All athleticsTracks as entities taken from repository: {}", allAthleticsTracksEntities);

        List<AthleticsTrack> allAthleticsTracksDto = allAthleticsTracksEntities
                .stream()
                .map(sportObjectMapper::toDomain)
                .map(AthleticsTrack.class::cast)
                .collect(Collectors.toList());

        logger.info("All athleticsTracks found (after mapping from entity to DTO): {}", allAthleticsTracksDto);

        return allAthleticsTracksDto;
    }

    public AthleticsTrack findById(Integer athleticsTrackId) {
        logger.info("Method findById() called with argument: {}", athleticsTrackId);

        AthleticsTrackEntity entity = athleticsTrackRepository.findByIdOptional(athleticsTrackId)
                .orElseThrow(() -> {

                    logger.warn("Exception", AthleticsTrackException.athleticsTrackNotFoundException());

                    return AthleticsTrackException.athleticsTrackNotFoundException();
                });

        logger.info("AthleticsTrackEntity by id: {} found in database:{}", athleticsTrackId, entity);

        AthleticsTrack athleticsTrackDto = (AthleticsTrack) sportObjectMapper.toDomain(entity);

        logger.info("AthleticsTrack by id: {} found after mapping to DTO:{}", athleticsTrackId, athleticsTrackDto);

        return athleticsTrackDto;
    }

    @Transactional
    public AthleticsTrack save(AthleticsTrack athleticsTrack) {
        logger.info("Method save() called with argument: {}", athleticsTrack);

        if (serviceUtils.compareSportObjectNameWithExisting(athleticsTrack.getName())) {

            logger.warn("Exception", SportObjectException.sportObjectDuplicateNameException());

            throw SportObjectException.sportObjectDuplicateNameException();
        }

        AthleticsTrackEntity entity = (AthleticsTrackEntity) sportObjectMapper.toEntity(athleticsTrack);
        athleticsTrackRepository.persist(entity);

        logger.info("AthleticsTrack persisted in repository: {}", entity);

        AthleticsTrack athleticsTrackDto = (AthleticsTrack) sportObjectMapper.toDomain(entity);

        logger.info("AthleticsTrack mapped from entity to DTO: {}", athleticsTrackDto);

        return athleticsTrackDto;
    }

    @Transactional
    public AthleticsTrack update(AthleticsTrack athleticsTrack) {
        logger.info("Method update() called with argument: {}", athleticsTrack);

        if (athleticsTrack.getId() == null) {

            logger.warn("Exception", AthleticsTrackException.athleticsTrackEmptyIdException());

            throw AthleticsTrackException.athleticsTrackEmptyIdException();
        }
        AthleticsTrackEntity entity = athleticsTrackRepository.findByIdOptional(athleticsTrack.getId())
                .orElseThrow(() -> {

                    logger.warn("Exception", AthleticsTrackException.athleticsTrackNotFoundException());

                    return AthleticsTrackException.athleticsTrackNotFoundException();
                });

        if (!athleticsTrack.getName().equals(entity.getName())) {
            if (serviceUtils.compareSportObjectNameWithExisting(athleticsTrack.getName())) {

                logger.warn("Exception", SportObjectException.sportObjectNotFoundException());

                throw SportObjectException.sportObjectDuplicateNameException();
            }
        }

        logger.info("AthleticsTrack before update: {}", entity);

        entity.setName(athleticsTrack.getName());
        entity.setCapacity(athleticsTrack.getCapacity());
        entity.setSingleTrackPrice(athleticsTrack.getSingleTrackPrice());
        entity.setFullPrice(athleticsTrack.getFullPrice());
        athleticsTrackRepository.persist(entity);

        logger.info("AthleticsTrack updated and persisted: {}", entity);

        AthleticsTrack athleticsTrackDto = (AthleticsTrack) sportObjectMapper.toDomain(entity);

        logger.info("Updated athleticsTrack mapped from entity to DTO: {}", athleticsTrackDto);

        return athleticsTrackDto;
    }

    @Transactional
    public AthleticsTrack putEquipmentToObject(Integer sportObjectId, Integer rentEquipmentId) {
        logger.info("Method putEquipmentToObject() called with arguments: {}, {}", sportObjectId, rentEquipmentId);

        AthleticsTrackEntity athleticsTrackToUpdate = athleticsTrackRepository.findById(sportObjectId);
        if (athleticsTrackToUpdate == null) {

            logger.warn("Exception", AthleticsTrackException.athleticsTrackNotFoundException());

            throw AthleticsTrackException.athleticsTrackNotFoundException();
        }
        RentEquipmentEntity rentEquipmentToAdd = rentEquipmentRepository.findById(rentEquipmentId);
        if (rentEquipmentToAdd == null) {

            logger.warn("Exception", RentEquipmentException.rentEquipmentNotFoundException());

            throw RentEquipmentException.rentEquipmentNotFoundException();
        }

        logger.info("AthleticsTrackEntity and RentEquipmentEntity to be connected: {}, {}",
                athleticsTrackToUpdate, rentEquipmentToAdd);

        athleticsTrackToUpdate.addRentEquipment(rentEquipmentToAdd);
        athleticsTrackRepository.persistAndFlush(athleticsTrackToUpdate);

        logger.info("AthleticsTrackEntity with new rentEquipmentEntity in it persisted in database: {}",
                athleticsTrackToUpdate);

        AthleticsTrack athleticsTrackDto = (AthleticsTrack) sportObjectMapper.toDomain(athleticsTrackToUpdate);

        logger.info("AthleticsTrack with new rentEquipment mapped from entity to DTO: {}, {}",
                athleticsTrackDto, athleticsTrackDto.getRentEquipmentNames());

        return athleticsTrackDto;
    }
}
