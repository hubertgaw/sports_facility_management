package pl.lodz.hubertgaw.service;

import pl.lodz.hubertgaw.dto.Booking;
import pl.lodz.hubertgaw.dto.SportObject;
import pl.lodz.hubertgaw.mapper.BookingMapper;
import pl.lodz.hubertgaw.mapper.SportObjectMapper;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.SportObjectRepository;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.repository.entity.BookingEntity;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SportObjectEntity;
import pl.lodz.hubertgaw.service.exception.RentEquipmentException;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import pl.lodz.hubertgaw.service.exception.SportObjectException;


@ApplicationScoped
public class SportObjectService {
    private final SportObjectRepository sportObjectRepository;
    private final SportObjectMapper sportObjectMapper;
    private final BookingMapper bookingMapper;
    private final Logger logger;
    private final RentEquipmentRepository rentEquipmentRepository;

    public SportObjectService(SportObjectRepository sportObjectRepository,
                              SportObjectMapper sportObjectMapper,
                              BookingMapper bookingMapper,
                              Logger logger,
                              RentEquipmentRepository rentEquipmentRepository) {
        this.sportObjectRepository = sportObjectRepository;
        this.sportObjectMapper = sportObjectMapper;
        this.bookingMapper = bookingMapper;
        this.logger = logger;
        this.rentEquipmentRepository = rentEquipmentRepository;

        logger.info("Constructor SportObjectService called");
    }

    public List<SportObject> findAll() {
        logger.info("Method findAll() called");

        List<SportObjectEntity> allSportObjectsEntities = sportObjectRepository.listAll();

        logger.info("All sportObjects as entities taken from repository: {}", allSportObjectsEntities);

        List<SportObject> allObjectsDto = allSportObjectsEntities
                .stream()
                .map(sportObjectMapper::toDomain)
                .map(SportObject.class::cast)
                .collect(Collectors.toList());

        logger.info("All sportObjects found (after mapping from entity to dto): {}", allObjectsDto);

        return allObjectsDto;
    }

    public SportObject findById(Integer sportObjectId) {
        logger.info("Method findById() called with argument: {}", sportObjectId);

        SportObjectEntity entity = sportObjectRepository.findByIdOptional(sportObjectId)
                .orElseThrow(() -> {

                    logger.warn("Exception", SportObjectException.sportObjectNotFoundException());

                    return SportObjectException.sportObjectNotFoundException();
                });

        logger.info("SportObjectEntity by id: {} found in database:{}", sportObjectId, entity);

        SportObject sportObjectDto = sportObjectMapper.toDomain(entity);

        logger.info("SportObject by id: {} found after mapping to DTO:{}", sportObjectId, sportObjectDto);

        return sportObjectDto;
    }

    @Transactional
    public SportObject putEquipmentToObject(Integer sportObjectId, Integer rentEquipmentId) {
        logger.info("Method putEquipmentToObject() called with arguments: {}, {}", sportObjectId, rentEquipmentId);

        SportObjectEntity sportObjectToUpdate = sportObjectRepository.findById(sportObjectId);
        if (sportObjectToUpdate == null) {

            logger.warn("Exception", SportObjectException.sportObjectNotFoundException());

            throw SportObjectException.sportObjectNotFoundException();
        }
        RentEquipmentEntity rentEquipmentToAdd = rentEquipmentRepository.findById(rentEquipmentId);
        if (rentEquipmentToAdd == null) {

            logger.warn("Exception", RentEquipmentException.rentEquipmentNotFoundException());

            throw RentEquipmentException.rentEquipmentNotFoundException();
        }

        logger.info("SportObjectEntity and RentEquipmentEntity to be connected: {}, {}",
                sportObjectToUpdate, rentEquipmentToAdd);

        sportObjectToUpdate.addRentEquipment(rentEquipmentToAdd);
        sportObjectRepository.persistAndFlush(sportObjectToUpdate);

        logger.info("SportObjectEntity with new rentEquipmentEntity in it persisted in database: {}",
                sportObjectToUpdate);

        SportObject sportObjectDto = sportObjectMapper.toDomain(sportObjectToUpdate);

        logger.info("SportObject with new rentEquipment mapped from entity to DTO: {}", sportObjectDto);

        return sportObjectDto;
    }

    @Transactional
    public void deleteSportObjectById(Integer sportObjectId) {
        logger.info("Method deleteSportObjectById() called with argument: {}", sportObjectId);

        SportObjectEntity sportObjectToDelete = sportObjectRepository.findById(sportObjectId);
        if (sportObjectToDelete == null) {

            logger.warn("Exception", SportObjectException.sportObjectNotFoundException());

            throw SportObjectException.sportObjectNotFoundException();
        }

        logger.info("SportObjectEntity by id: {} found in database:{}", sportObjectId, sportObjectToDelete);

        Set<RentEquipmentEntity> rentEquipmentToDelete = sportObjectToDelete.getRentEquipment();
        logger.info("RentEquipmentEntities from sportObject with id: {} to delete: {}",
                sportObjectId, rentEquipmentToDelete);
        for (RentEquipmentEntity rentEquipment : rentEquipmentToDelete) {

            rentEquipment.removeSportObject(sportObjectToDelete);

            logger.info("SportObject with id: {} removed from rentEquipment with id: {}",
                    sportObjectId, rentEquipment.getId());
        }
        sportObjectRepository.delete(sportObjectToDelete);

        logger.info("SportObject with id: {} deleted successfully", sportObjectId);
    }

    public List<Booking> findBookingsForSportObject(Integer sportObjectId) {
        logger.info("Method findBookingsForSportObject() called with argument: {}", sportObjectId);

        SportObjectEntity entity = sportObjectRepository.findByIdOptional(sportObjectId)
                .orElseThrow(() -> {

                    logger.warn("Exception", SportObjectException.sportObjectNotFoundException());

                    return SportObjectException.sportObjectNotFoundException();
                });

        List<BookingEntity> allBookingsEntitiesForSportObject = entity.getBookings();

        logger.info("All BookingsEntities for sportObject with id: {} : {}",
                sportObjectId, allBookingsEntitiesForSportObject);

        List<Booking> allBookings = allBookingsEntitiesForSportObject
                .stream()
                .map(bookingMapper::toDomain)
                .map(Booking.class::cast)
                .collect(Collectors.toList());

        logger.info("All bookings found for sportObject with id :{} after mapping from entity to DTO: {}",
                sportObjectId, allBookings);

        return allBookings;

    }
}
