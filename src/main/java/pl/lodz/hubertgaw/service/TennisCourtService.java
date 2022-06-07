package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.TennisCourt;
import pl.lodz.hubertgaw.mapper.SportObjectMapper;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.TennisCourtRepository;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.TennisCourtEntity;
import pl.lodz.hubertgaw.service.exception.RentEquipmentException;
import pl.lodz.hubertgaw.service.exception.SportObjectException;
import pl.lodz.hubertgaw.service.exception.TennisCourtException;
import pl.lodz.hubertgaw.service.utils.ServiceUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class TennisCourtService {
    private final TennisCourtRepository tennisCourtRepository;
    private final SportObjectMapper sportObjectMapper;
    private final Logger logger;
    private final RentEquipmentRepository rentEquipmentRepository;
    private final ServiceUtils serviceUtils;

    public TennisCourtService(TennisCourtRepository tennisCourtRepository,
                              SportObjectMapper sportObjectMapper,
                              Logger logger,
                              RentEquipmentRepository rentEquipmentRepository,
                              ServiceUtils serviceUtils) {
        this.tennisCourtRepository = tennisCourtRepository;
        this.sportObjectMapper = sportObjectMapper;
        this.logger = logger;
        this.rentEquipmentRepository = rentEquipmentRepository;
        this.serviceUtils = serviceUtils;

        logger.info("Constructor TennisCourtService called");
    }

    public List<TennisCourt> findAll() {
        logger.info("Method findAll() called");

        List<TennisCourtEntity> allTennisCourtsEntities = tennisCourtRepository.listAll();

        logger.info("All tennisCourts as entities taken from repository: {}", allTennisCourtsEntities);

        List<TennisCourt> allTennisCourtsDto = allTennisCourtsEntities
                .stream()
                .map(sportObjectMapper::toDomain)
                .map(TennisCourt.class::cast)
                .collect(Collectors.toList());

        logger.info("All tennisCourts found (after mapping from entity to DTO): {}", allTennisCourtsDto);

        return allTennisCourtsDto;
    }

    public TennisCourt findById(Integer tennisCourtId) {
        logger.info("Method findById() called with argument: {}", tennisCourtId);

        TennisCourtEntity entity = tennisCourtRepository.findByIdOptional(tennisCourtId)
                .orElseThrow(() -> {

                    logger.warn("Exception", TennisCourtException.tennisCourtNotFoundException());

                    return TennisCourtException.tennisCourtNotFoundException();
                });

        logger.info("TennisCourtEntity by id: {} found in database:{}", tennisCourtId, entity);

        TennisCourt tennisCourtDto = (TennisCourt) sportObjectMapper.toDomain(entity);

        logger.info("TennisCourt by id: {} found after mapping to DTO:{}", tennisCourtId, tennisCourtDto);

        return tennisCourtDto;
      }

    @Transactional
    public TennisCourt save(TennisCourt tennisCourt) {
        logger.info("Method save() called with argument: {}", tennisCourt);

        if (serviceUtils.compareSportObjectNameWithExisting(tennisCourt.getName())) {

            logger.warn("Exception", SportObjectException.sportObjectDuplicateNameException());

            throw SportObjectException.sportObjectDuplicateNameException();
        }
        TennisCourtEntity entity = (TennisCourtEntity) sportObjectMapper.toEntity(tennisCourt);
        tennisCourtRepository.persist(entity);

        logger.info("TennisCourt persisted in repository: {}", entity);

        TennisCourt tennisCourtDto = (TennisCourt) sportObjectMapper.toDomain(entity);

        logger.info("TennisCourt mapped from entity to DTO: {}", tennisCourtDto);

        return tennisCourtDto;
    }

    @Transactional
    public TennisCourt update(TennisCourt tennisCourt) {
        logger.info("Method update() called with argument: {}", tennisCourt);

        if (tennisCourt.getId() == null) {

            logger.warn("Exception", TennisCourtException.tennisCourtEmptyIdException());

            throw TennisCourtException.tennisCourtEmptyIdException();
        }
        TennisCourtEntity entity = tennisCourtRepository.findByIdOptional(tennisCourt.getId()).
                orElseThrow(() -> {

                    logger.warn("Exception", TennisCourtException.tennisCourtNotFoundException());

                    return TennisCourtException.tennisCourtNotFoundException();
                });

        if (!tennisCourt.getName().equals(entity.getName())) {
            if (serviceUtils.compareSportObjectNameWithExisting(tennisCourt.getName())) {

                logger.warn("Exception", SportObjectException.sportObjectNotFoundException());

                throw SportObjectException.sportObjectDuplicateNameException();
            }
        }

        logger.info("TennisCourt before update: {}", entity);

        entity.setFullPrice(tennisCourt.getFullPrice());
        entity.setName(tennisCourt.getName());
        tennisCourtRepository.persist(entity);

        logger.info("TennisCourt updated and persisted: {}", entity);

        TennisCourt tennisCourtDto = (TennisCourt) sportObjectMapper.toDomain(entity);

        logger.info("Updated tennisCourt mapped from entity to DTO: {}", tennisCourtDto);

        return tennisCourtDto;
    }

    @Transactional
    public TennisCourt putEquipmentToObject(Integer sportObjectId, Integer rentEquipmentId) {
        logger.info("Method putEquipmentToObject() called with arguments: {}, {}", sportObjectId, rentEquipmentId);

        TennisCourtEntity tennisCourtToUpdate = tennisCourtRepository.findById(sportObjectId);
        if (tennisCourtToUpdate == null) {

            logger.warn("Exception", TennisCourtException.tennisCourtNotFoundException());

            throw TennisCourtException.tennisCourtNotFoundException();
        }
        RentEquipmentEntity rentEquipmentToAdd = rentEquipmentRepository.findById(rentEquipmentId);
        if (rentEquipmentToAdd == null) {

            logger.warn("Exception", RentEquipmentException.rentEquipmentNotFoundException());

            throw RentEquipmentException.rentEquipmentNotFoundException();
        }

        logger.info("TennisCourtEntity and RentEquipmentEntity to be connected: {}, {}",
                tennisCourtToUpdate, rentEquipmentToAdd);

        tennisCourtToUpdate.addRentEquipment(rentEquipmentToAdd);
        tennisCourtRepository.persistAndFlush(tennisCourtToUpdate);

        logger.info("TennisCourtEntity with new rentEquipmentEntity in it persisted in database: {}",
                tennisCourtToUpdate);

        TennisCourt tennisCourtDto = (TennisCourt) sportObjectMapper.toDomain(tennisCourtToUpdate);

        logger.info("TennisCourt with new rentEquipment mapped from entity to DTO: {}, {}",
                tennisCourtDto, tennisCourtDto.getRentEquipmentNames());

        return tennisCourtDto;
    }

}
