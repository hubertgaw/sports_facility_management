package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.Gym;
import pl.lodz.hubertgaw.mapper.SportObjectMapper;
import pl.lodz.hubertgaw.repository.GymRepository;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.GymEntity;
import pl.lodz.hubertgaw.service.exception.GymException;
import pl.lodz.hubertgaw.service.exception.RentEquipmentException;
import pl.lodz.hubertgaw.service.exception.SportObjectException;
import pl.lodz.hubertgaw.service.utils.ServiceUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class GymService {
    private final GymRepository gymRepository;
    private final SportObjectMapper sportObjectMapper;
    private final Logger logger;
    private final RentEquipmentRepository rentEquipmentRepository;
    private final ServiceUtils serviceUtils;

    public GymService(GymRepository gymRepository,
                      SportObjectMapper sportObjectMapper,
                      Logger logger,
                      RentEquipmentRepository rentEquipmentRepository,
                      ServiceUtils serviceUtils) {
        this.gymRepository = gymRepository;
        this.sportObjectMapper = sportObjectMapper;
        this.logger = logger;
        this.rentEquipmentRepository = rentEquipmentRepository;
        this.serviceUtils = serviceUtils;

        logger.info("Constructor GymService called");
    }

    public List<Gym> findAll() {
        logger.info("Method findAll() called");

        List<GymEntity> allGymsEntities = gymRepository.listAll();

        logger.info("All gyms as entities taken from repository: {}", allGymsEntities);

        List<Gym> allGymsDto = allGymsEntities
                .stream()
                .map(sportObjectMapper::toDomain)
                .map(Gym.class::cast)
                .collect(Collectors.toList());

        logger.info("All gyms found (after mapping from entity to DTO): {}", allGymsDto);

        return allGymsDto;
    }

    public Gym findById(Integer gymId) {
        logger.info("Method findById() called with argument: {}", gymId);

        GymEntity entity = gymRepository.findByIdOptional(gymId)
                .orElseThrow(() -> {

                    logger.warn("Exception", GymException.gymNotFoundException());

                    return GymException.gymNotFoundException();
                });

        logger.info("GymEntity by id: {} found in database:{}", gymId, entity);

        Gym gymDto = (Gym) sportObjectMapper.toDomain(entity);

        logger.info("Gym by id: {} found after mapping to DTO:{}", gymId, gymDto);

        return gymDto;
    }

    @Transactional
    public Gym save(Gym gym) {
        logger.info("Method save() called with argument: {}", gym);

        if (serviceUtils.compareSportObjectNameWithExisting(gym.getName())) {

            logger.warn("Exception", SportObjectException.sportObjectDuplicateNameException());

            throw SportObjectException.sportObjectDuplicateNameException();
        }

        GymEntity entity = (GymEntity) sportObjectMapper.toEntity(gym);
        gymRepository.persist(entity);

        logger.info("Gym persisted in repository: {}", entity);

        Gym gymDto = (Gym) sportObjectMapper.toDomain(entity);

        logger.info("Gym mapped from entity to DTO: {}", gymDto);

        return gymDto;
    }

    @Transactional
    public Gym update(Gym gym) {
        logger.info("Method update() called with argument: {}", gym);

        if (gym.getId() == null) {

            logger.warn("Exception", GymException.gymEmptyIdException());

            throw GymException.gymEmptyIdException();
        }
        GymEntity entity = gymRepository.findByIdOptional(gym.getId())
                .orElseThrow(() -> {

                    logger.warn("Exception", GymException.gymNotFoundException());

                    return GymException.gymNotFoundException();
                });

        if (!gym.getName().equals(entity.getName())) {
            if (serviceUtils.compareSportObjectNameWithExisting(gym.getName())) {

                logger.warn("Exception", SportObjectException.sportObjectNotFoundException());

                throw SportObjectException.sportObjectDuplicateNameException();
            }
        }

        logger.info("Gym before update: {}", entity);

        entity.setFullPrice(gym.getFullPrice());
        entity.setName(gym.getName());
        entity.setCapacity(gym.getCapacity());
        entity.setSinglePrice(gym.getSinglePrice());
        gymRepository.persist(entity);

        logger.info("Gym updated and persisted: {}", entity);

        Gym gymDto = (Gym) sportObjectMapper.toDomain(entity);

        logger.info("Updated gym mapped from entity to DTO: {}", gymDto);

        return gymDto;
    }

    @Transactional
    public Gym putEquipmentToObject(Integer sportObjectId, Integer rentEquipmentId) {
        logger.info("Method putEquipmentToObject() called with arguments: {}, {}", sportObjectId, rentEquipmentId);

        GymEntity gymToUpdate = gymRepository.findById(sportObjectId);
        if (gymToUpdate == null) {

            logger.warn("Exception", GymException.gymNotFoundException());

            throw GymException.gymNotFoundException();
        }
        RentEquipmentEntity rentEquipmentToAdd = rentEquipmentRepository.findById(rentEquipmentId);
        if (rentEquipmentToAdd == null) {

            logger.warn("Exception", RentEquipmentException.rentEquipmentNotFoundException());

            throw RentEquipmentException.rentEquipmentNotFoundException();
        }

        logger.info("GymEntity and RentEquipmentEntity to be connected: {}, {}",
                gymToUpdate, rentEquipmentToAdd);

        gymToUpdate.addRentEquipment(rentEquipmentToAdd);
        gymRepository.persistAndFlush(gymToUpdate);

        logger.info("GymEntity with new rentEquipmentEntity in it persisted in database: {}",
                gymToUpdate);

        Gym gymDto = (Gym) sportObjectMapper.toDomain(gymToUpdate);

        logger.info("Gym with new rentEquipment mapped from entity to DTO: {}, {}",
                gymDto, gymDto.getRentEquipmentNames());

        return gymDto;
    }

}
