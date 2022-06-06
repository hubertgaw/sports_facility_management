package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.DartRoom;
import pl.lodz.hubertgaw.dto.DartRoom;
import pl.lodz.hubertgaw.mapper.SportObjectMapper;
import pl.lodz.hubertgaw.repository.DartRoomRepository;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.DartRoomEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.DartRoomEntity;
import pl.lodz.hubertgaw.service.exception.DartRoomException;
import pl.lodz.hubertgaw.service.exception.DartRoomException;
import pl.lodz.hubertgaw.service.exception.RentEquipmentException;
import pl.lodz.hubertgaw.service.exception.SportObjectException;
import pl.lodz.hubertgaw.service.utils.ServiceUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class DartRoomService {
    private final DartRoomRepository dartRoomRepository;
    private final SportObjectMapper sportObjectMapper;
    private final Logger logger;
    private final RentEquipmentRepository rentEquipmentRepository;
    private final ServiceUtils serviceUtils;

    public DartRoomService(DartRoomRepository dartRoomRepository,
                           SportObjectMapper sportObjectMapper,
                           Logger logger,
                           RentEquipmentRepository rentEquipmentRepository,
                           ServiceUtils serviceUtils) {
        this.dartRoomRepository = dartRoomRepository;
        this.sportObjectMapper = sportObjectMapper;
        this.logger = logger;
        this.rentEquipmentRepository = rentEquipmentRepository;
        this.serviceUtils = serviceUtils;

        logger.info("Constructor DartRoomService called");
    }

    public List<DartRoom> findAll() {
        logger.info("Method findAll() called");

        List<DartRoomEntity> allDartRoomsEntities = dartRoomRepository.listAll();

        logger.info("All dartRooms as entities taken from repository: {}", allDartRoomsEntities);

        List<DartRoom> allDartRoomsDto = allDartRoomsEntities
                .stream()
                .map(sportObjectMapper::toDomain)
                .map(DartRoom.class::cast)
                .collect(Collectors.toList());

        logger.info("All dartRooms found (after mapping from entity to DTO): {}", allDartRoomsDto);

        return allDartRoomsDto;
    }

    public DartRoom findById(Integer dartRoomId) {
        logger.info("Method findById() called with argument: {}", dartRoomId);

        DartRoomEntity entity = dartRoomRepository.findByIdOptional(dartRoomId)
                .orElseThrow(() -> {

                    logger.warn("Exception", DartRoomException.dartRoomNotFoundException());

                    return DartRoomException.dartRoomNotFoundException();
                });


        logger.info("DartRoomEntity by id: {} found in database:{}", dartRoomId, entity);

        DartRoom dartRoomDto = (DartRoom) sportObjectMapper.toDomain(entity);

        logger.info("DartRoom by id: {} found after mapping to DTO:{}", dartRoomId, dartRoomDto);

        return dartRoomDto;
    }

    @Transactional
    public DartRoom save(DartRoom dartRoom) {
        logger.info("Method save() called with argument: {}", dartRoom);

        if (serviceUtils.compareSportObjectNameWithExisting(dartRoom.getName())) {

            logger.warn("Exception", SportObjectException.sportObjectDuplicateNameException());

            throw SportObjectException.sportObjectDuplicateNameException();
        }

        DartRoomEntity entity = (DartRoomEntity) sportObjectMapper.toEntity(dartRoom);
        dartRoomRepository.persist(entity);

        logger.info("DartRoom persisted in repository: {}", entity);

        DartRoom dartRoomDto = (DartRoom) sportObjectMapper.toDomain(entity);

        logger.info("DartRoom mapped from entity to DTO: {}", dartRoomDto);

        return dartRoomDto;
    }

    @Transactional
    public DartRoom update(DartRoom dartRoom) {
        logger.info("Method update() called with argument: {}", dartRoom);

        if (dartRoom.getId() == null) {

            logger.warn("Exception", DartRoomException.dartRoomEmptyIdException());

            throw DartRoomException.dartRoomEmptyIdException();
        }
        DartRoomEntity entity = dartRoomRepository.findByIdOptional(dartRoom.getId())
                .orElseThrow(() -> {

                    logger.warn("Exception", DartRoomException.dartRoomNotFoundException());

                    return DartRoomException.dartRoomNotFoundException();
                });

        if (!dartRoom.getName().equals(entity.getName())) {
            if (serviceUtils.compareSportObjectNameWithExisting(dartRoom.getName())) {

                logger.warn("Exception", SportObjectException.sportObjectNotFoundException());

                throw SportObjectException.sportObjectDuplicateNameException();
            }
        }
        logger.info("DartRoom before update: {}", entity);

        entity.setFullPrice(dartRoom.getFullPrice());
        entity.setName(dartRoom.getName());
        entity.setStandPrice(dartRoom.getStandPrice());
        entity.setStandsNumber(dartRoom.getCapacity());
        dartRoomRepository.persist(entity);

        logger.info("DartRoom updated and persisted: {}", entity);

        DartRoom dartRoomDto = (DartRoom) sportObjectMapper.toDomain(entity);

        logger.info("Updated dartRoom mapped from entity to DTO: {}", dartRoomDto);

        return dartRoomDto;
    }

    @Transactional
    public DartRoom putEquipmentToObject(Integer sportObjectId, Integer rentEquipmentId) {
        logger.info("Method putEquipmentToObject() called with arguments: {}, {}", sportObjectId, rentEquipmentId);

        DartRoomEntity dartRoomToUpdate = dartRoomRepository.findById(sportObjectId);
        if (dartRoomToUpdate == null) {

            logger.warn("Exception", DartRoomException.dartRoomNotFoundException());

            throw DartRoomException.dartRoomNotFoundException();
        }
        RentEquipmentEntity rentEquipmentToAdd = rentEquipmentRepository.findById(rentEquipmentId);
        if (rentEquipmentToAdd == null) {

            logger.warn("Exception", RentEquipmentException.rentEquipmentNotFoundException());

            throw RentEquipmentException.rentEquipmentNotFoundException();
        }

        logger.info("DartRoomEntity and RentEquipmentEntity to be connected: {}, {}",
                dartRoomToUpdate, rentEquipmentToAdd);

        dartRoomToUpdate.addRentEquipment(rentEquipmentToAdd);
        dartRoomRepository.persistAndFlush(dartRoomToUpdate);

        logger.info("DartRoomEntity with new rentEquipmentEntity in it persisted in database: {}",
                dartRoomToUpdate);

        DartRoom dartRoomDto = (DartRoom) sportObjectMapper.toDomain(dartRoomToUpdate);

        logger.info("DartRoom with new rentEquipment mapped from entity to DTO: {}, {}",
                dartRoomDto, dartRoomDto.getRentEquipmentNames());

        return dartRoomDto;
    }

}
