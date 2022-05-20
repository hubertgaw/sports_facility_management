package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.BeachVolleyballCourt;
import pl.lodz.hubertgaw.dto.BeachVolleyballCourt;
import pl.lodz.hubertgaw.mapper.SportObjectMapper;
import pl.lodz.hubertgaw.repository.BeachVolleyballCourtRepository;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.BeachVolleyballCourtEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.BeachVolleyballCourtEntity;
import pl.lodz.hubertgaw.service.exception.BeachVolleyballCourtException;
import pl.lodz.hubertgaw.service.exception.BeachVolleyballCourtException;
import pl.lodz.hubertgaw.service.exception.RentEquipmentException;
import pl.lodz.hubertgaw.service.exception.SportObjectException;
import pl.lodz.hubertgaw.service.utils.ServiceUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class BeachVolleyballCourtService {
    private final BeachVolleyballCourtRepository beachVolleyballCourtRepository;
    private final SportObjectMapper sportObjectMapper;
    private final Logger logger;
    private final RentEquipmentRepository rentEquipmentRepository;
    private final ServiceUtils serviceUtils;

    public BeachVolleyballCourtService(BeachVolleyballCourtRepository beachVolleyballCourtRepository,
                                 SportObjectMapper sportObjectMapper,
                                 Logger logger,
                                 RentEquipmentRepository rentEquipmentRepository,
                                       ServiceUtils serviceUtils) {
        this.beachVolleyballCourtRepository = beachVolleyballCourtRepository;
        this.sportObjectMapper = sportObjectMapper;
        this.logger = logger;
        this.rentEquipmentRepository = rentEquipmentRepository;
        this.serviceUtils = serviceUtils;

        logger.info("Constructor BeachVolleyballCourtService called");
    }

    public List<BeachVolleyballCourt> findAll() {
        logger.info("Method findAll() called");

        List<BeachVolleyballCourtEntity> allBeachVolleyballCourtsEntities = beachVolleyballCourtRepository.listAll();

        logger.info("All beachVolleyballCourts as entities taken from repository: {}", allBeachVolleyballCourtsEntities);

        List<BeachVolleyballCourt> allBeachVolleyballCourtsDto = allBeachVolleyballCourtsEntities
                .stream()
                .map(sportObjectMapper::toDomain)
                .map(BeachVolleyballCourt.class::cast)
                .collect(Collectors.toList());

        logger.info("All beachVolleyballCourts found (after mapping from entity to DTO): {}", allBeachVolleyballCourtsDto);

        return allBeachVolleyballCourtsDto;
    }

    public BeachVolleyballCourt findById(Integer beachVolleyballCourtId) {
        logger.info("Method findById() called with argument: {}", beachVolleyballCourtId);

        BeachVolleyballCourtEntity entity = beachVolleyballCourtRepository.findByIdOptional(beachVolleyballCourtId)
                .orElseThrow(() -> {

                    logger.warn("Exception", BeachVolleyballCourtException.beachVolleyballCourtNotFoundException());

                    return BeachVolleyballCourtException.beachVolleyballCourtNotFoundException();
                });

        logger.info("BeachVolleyballCourtEntity by id: {} found in database:{}", beachVolleyballCourtId, entity);

        BeachVolleyballCourt beachVolleyballCourtDto = (BeachVolleyballCourt) sportObjectMapper.toDomain(entity);

        logger.info("BeachVolleyballCourt by id: {} found after mapping to DTO:{}", beachVolleyballCourtId, beachVolleyballCourtDto);

        return beachVolleyballCourtDto;
    }

    @Transactional
    public BeachVolleyballCourt save(BeachVolleyballCourt beachVolleyballCourt) {
        logger.info("Method save() called with argument: {}", beachVolleyballCourt);

        if (serviceUtils.compareSportObjectNameWithExisting(beachVolleyballCourt.getName())) {

            logger.warn("Exception", SportObjectException.sportObjectDuplicateNameException());

            throw SportObjectException.sportObjectDuplicateNameException();
        }
        BeachVolleyballCourtEntity entity = (BeachVolleyballCourtEntity) sportObjectMapper.toEntity(beachVolleyballCourt);
        beachVolleyballCourtRepository.persist(entity);

        logger.info("BeachVolleyballCourt persisted in repository: {}", entity);

        BeachVolleyballCourt beachVolleyballCourtDto = (BeachVolleyballCourt) sportObjectMapper.toDomain(entity);

        logger.info("BeachVolleyballCourt mapped from entity to DTO: {}", beachVolleyballCourtDto);

        return beachVolleyballCourtDto;
    }

    @Transactional
    public BeachVolleyballCourt update(BeachVolleyballCourt beachVolleyballCourt) {
        logger.info("Method update() called with argument: {} ", beachVolleyballCourt);

        if (beachVolleyballCourt.getId() == null) {

            logger.warn("Exception", BeachVolleyballCourtException.beachVolleyballCourtEmptyIdException());

            throw BeachVolleyballCourtException.beachVolleyballCourtEmptyIdException();
        }
        BeachVolleyballCourtEntity entity = beachVolleyballCourtRepository.findByIdOptional(beachVolleyballCourt.getId())
                .orElseThrow(() -> {

                    logger.warn("Exception", BeachVolleyballCourtException.beachVolleyballCourtNotFoundException());

                    return BeachVolleyballCourtException.beachVolleyballCourtNotFoundException();
                });

        logger.info("BeachVolleyballCourt before update: {}", entity);

        entity.setFullPrice(beachVolleyballCourt.getFullPrice());
        entity.setName(beachVolleyballCourt.getName());
        beachVolleyballCourtRepository.persist(entity);

        logger.info("BeachVolleyballCourt updated and persisted: {}", entity);

        BeachVolleyballCourt beachVolleyballCourtDto = (BeachVolleyballCourt) sportObjectMapper.toDomain(entity);

        logger.info("Updated beachVolleyballCourt mapped from entity to DTO: {}", beachVolleyballCourtDto);

        return beachVolleyballCourtDto;
    }

    @Transactional
    public BeachVolleyballCourt putEquipmentToObject(Integer sportObjectId, Integer rentEquipmentId) {
        logger.info("Method putEquipmentToObject() called with arguments: {}. {}", sportObjectId, rentEquipmentId);

        BeachVolleyballCourtEntity beachVolleyballCourtToUpdate = beachVolleyballCourtRepository.findById(sportObjectId);
        if (beachVolleyballCourtToUpdate == null) {

            logger.warn("Exception", BeachVolleyballCourtException.beachVolleyballCourtNotFoundException());

            throw BeachVolleyballCourtException.beachVolleyballCourtNotFoundException();
        }
        RentEquipmentEntity rentEquipmentToAdd = rentEquipmentRepository.findById(rentEquipmentId);
        if (rentEquipmentToAdd == null) {

            logger.warn("Exception", RentEquipmentException.rentEquipmentNotFoundException());

            throw RentEquipmentException.rentEquipmentNotFoundException();
        }

        logger.info("BeachVolleyballCourtEntity and RentEquipmentEntity to be connected: {}, {}",
                beachVolleyballCourtToUpdate, rentEquipmentToAdd);

        beachVolleyballCourtToUpdate.addRentEquipment(rentEquipmentToAdd);
        beachVolleyballCourtRepository.persistAndFlush(beachVolleyballCourtToUpdate);
        logger.info("BeachVolleyballCourtEntity with new rentEquipmentEntity in it persisted in database: {}",
                beachVolleyballCourtToUpdate);

        BeachVolleyballCourt beachVolleyballCourtDto = (BeachVolleyballCourt) sportObjectMapper.toDomain(beachVolleyballCourtToUpdate);

        logger.info("BeachVolleyballCourt with new rentEquipment mapped from entity to DTO: {}, {}",
                beachVolleyballCourtDto, beachVolleyballCourtDto.getRentEquipmentNames());

        return beachVolleyballCourtDto;
    }

}
